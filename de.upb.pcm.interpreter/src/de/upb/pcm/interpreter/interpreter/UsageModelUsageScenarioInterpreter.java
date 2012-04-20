package de.upb.pcm.interpreter.interpreter;


import javax.activation.UnsupportedDataTypeException;

import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.pcm.usagemodel.UsageModel;
import de.uka.ipd.sdq.pcm.usagemodel.UsageScenario;
import de.uka.ipd.sdq.probespec.framework.calculator.Calculator;
import de.uka.ipd.sdq.simucomframework.SimuComSimProcess;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.usage.IScenarioRunner;
import de.upb.pcm.interpreter.access.AbstractPCMModelAccess;
import de.upb.pcm.interpreter.access.IModelAccessFactory;
import de.upb.pcm.interpreter.access.PMSAccess;
import de.upb.pcm.interpreter.metrics.aggregators.ResponseTimeAggregator;
import de.upb.pcm.interpreter.simulation.InterpreterDefaultContext;
import de.upb.pcm.interpreter.switches.UsageModelUsageScenarioSwitch;
import de.upb.pcm.interpreter.utils.InterpreterLogger;
import de.upb.pcm.pms.MeasurementSpecification;
import de.upb.pcm.pms.PerformanceMetricEnum;
import de.upb.pcm.prm.PrmFactory;

/**
 * Interpreter for usage scenario in usage model. Class implements an IScenarioRunner and can be
 * used in SimuCom workload drivers.
 * 
 * @author Joachim Meyer
 * 
 */
public class UsageModelUsageScenarioInterpreter 
	extends AbstractPCMModelInterpreter<UsageModel> 
	implements IScenarioRunner
{
   private PMSAccess pmsModelAccess;

   /**
    * Constructor
    * 
    * @param contex the interpreter default context for the pcm model interpreter, may be null.
    * @param modelHelper the model helper.
    */
   public UsageModelUsageScenarioInterpreter(final IModelAccessFactory modelAccessFactory,
		   final InterpreterDefaultContext context)
   {
      super(modelAccessFactory, context);
      this.pmsModelAccess = modelAccessFactory.getPMSModelAccess();
   }

   /**
    * @see de.upb.pcm.interpreter.interpreter.AbstractPCMModelInterpreter#getModelSwitch()
    */
   @Override
   protected UsageModelUsageScenarioSwitch<Object> getModelSwitch()
   {
      return new UsageModelUsageScenarioSwitch<Object>(this.context,this.modelAccessFactory,this.pcmInterpreterProbeSpecUtil);
   }


   /**
    * Initializes response time measurement.
    * 
    * @param modelElement the model element to be measured.
    * @param calculatorName the name of the response time calculator.
    * @param startProbeId start probe id.
    * @param stopProbeId stop probe id.
    */
   private void initReponseTimeMeasurement(final EObject modelElement, final String calculatorName,
         final String startProbeId, final String stopProbeId, SimuComModel simuComModel)
   {
      MeasurementSpecification measurementSpecification;
      if ((measurementSpecification = pmsModelAccess.isMonitored(modelElement, PerformanceMetricEnum.RESPONSE_TIME)) != null)
      {

         final Calculator calculator = this.pcmInterpreterProbeSpecUtil.createResponseTimeCalculator(startProbeId,
               stopProbeId, calculatorName, calculatorName, measurementSpecification, modelElement,
               simuComModel);

         if (calculator != null)
         {
            try
            {
               new ResponseTimeAggregator(this.modelAccessFactory, measurementSpecification, calculator, calculatorName, modelElement,
                     PrmFactory.eINSTANCE.createPCMModelElementMeasurement(),simuComModel.getSimulationControl().getCurrentSimulationTime());
            }
            catch (final UnsupportedDataTypeException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   /**
    * @see de.uka.ipd.sdq.simucomframework.usage.IScenarioRunner#scenarioRunner(de.uka.ipd.sdq.simucomframework.abstractSimEngine.SimProcess)
    */
   @Override
   public void scenarioRunner(final SimuComSimProcess thread)
   {
      InterpreterLogger.debug(logger, "Start scenario: " + thread.getModel());
      this.context.setSimProcess(thread);

      // interpret usage scenario in local model
      this.interpret(getModelAccess().getModel().getUsageScenario_UsageModel().get(0));

      InterpreterLogger.debug(logger, "Scenario finished: " + getModelAccess().getModel().getUsageScenario_UsageModel().get(0));
   }

   /**
    * @see de.upb.pcm.interpreter.interpreter.AbstractPCMModelInterpreter#startInterpretation(org.eclipse.emf.ecore.EObject,
    *      de.upb.pcm.interpreter.interfaces.IPCMModelSwitch)
    */
   @Override
   protected void startInterpretation(final EObject startElement, final Object... o)
   {
      InterpreterLogger.debug(logger, "Start Interpretation of Usage Scenario: " + startElement);
      if (!(startElement instanceof UsageScenario))
      {
         throw new IllegalArgumentException("startElement must be of type UsageScenario");
      }

      /*
       * Measure Response Time of Usage Scenario Steps: Take time sample at the start and a time
       * sample at the end of the usage scenario
       */

      final String calculatorName = ((UsageScenario) startElement).getEntityName();
      final String startProbeId = calculatorName + "_resp1";
      final String stopProbeId = calculatorName + "_resp2";

      initReponseTimeMeasurement(startElement, calculatorName, startProbeId, stopProbeId, getModelAccess().getSimProcess().getModel());

      // ############## Measurement START ##############
      this.pcmInterpreterProbeSpecUtil.takeCurrentTimeSample(startProbeId, calculatorName, getModelAccess().getSimProcess());
      // ############## Measurement END ##############

      // start visitor for usage scenario
      getModelSwitch().doSwitch(startElement);

      // ############## Measurement START ##############
      this.pcmInterpreterProbeSpecUtil.takeCurrentTimeSample(stopProbeId, calculatorName, getModelAccess().getSimProcess());
      // ############## Measurement END ##############

      InterpreterLogger.debug(logger, "Finished Interpretation of Usage Scenario: " + startElement);
   }

	@Override
	protected AbstractPCMModelAccess<UsageModel> createModelAccess(
			IModelAccessFactory modelAccessFactory,
			InterpreterDefaultContext context) {
		// TODO Auto-generated method stub
		return modelAccessFactory.getUsageModelAccess(context);
	}
}
