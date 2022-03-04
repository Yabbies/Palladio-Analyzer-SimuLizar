package org.palladiosimulator.simulizar.simulation.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simulizar.test.commons.annotation.LoadPCMInstanceFromBundle;
import org.palladiosimulator.simulizar.test.commons.annotation.RunSimuLizar;
import org.palladiosimulator.simulizar.test.commons.annotation.SimulationConfig;
import org.palladiosimulator.simulizar.test.commons.util.MeasurementTestUtils;

import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import tools.mdsd.junit5utils.annotations.PluginTestOnly;

@PluginTestOnly
class UsageevolutionTest {

    /**
     * Tests Usageevolution with a simple deterministic example. This test only tests the correct
     * responste time evolution.
     */
    @Test
    @LoadPCMInstanceFromBundle(bundleName = "org.palladiosimulator.simulizar.tests", basePath = "testmodels/usageevolutionTest", modelFiles = {
            "default.allocation", "default.usagemodel", "default.repository", "default.usageevolution" })
    @SimulationConfig(maxMeasurements = "100")
    @RunSimuLizar
    void testUsageevolutionResponseTimeVariation(UsageScenario scenario, ExperimentRun expRun)
            throws JobFailedException, UserCanceledException {
        var measurement = MeasurementTestUtils.getMeasurementOfAt(expRun.getMeasurement(),
                MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, scenario);
        assertTrue(measurement.isPresent());

        List<Measure<?, Quantity>> responseTimeMeasurements = MeasurementTestUtils
            .allMeasurementsOfMetric(measurement.get(), MetricDescriptionConstants.RESPONSE_TIME_METRIC);
        List<Measure<?, Quantity>> timeMeasurements = MeasurementTestUtils.allMeasurementsOfMetric(measurement.get(),
                MetricDescriptionConstants.POINT_IN_TIME_METRIC);
        assertEquals(responseTimeMeasurements.size(), timeMeasurements.size());

        // The simulated test scenario evolves response time and user amount.
        // Population is 1 and Think Time is 99.0
        // It evolves in 3 stages.
        // Stage 1: 2000s with Constant 1.0
        // Stage 2: 2000s with Constant 2.0 (Leads to Response Time 4.0 as both values evolve)
        // Stage 3: 6000s with Constant 1.0
        for (int i = 0; i < 100; i++) {
            if ((i < 20) || (i >= 60 && i < 100)) {
                assertEquals((Double) responseTimeMeasurements.get(i)
                    .getValue(), 1.0, 0.001);
            }
            if (i >= 20 && i < 60) {
                assertEquals((Double) responseTimeMeasurements.get(i)
                    .getValue(), 4.0, 0.001);
            }
        }
    }

    /**
     * Tests Usageevolution with a simple deterministic example. This test tests, that the evolution
     * happens at the correct time.
     */
    @Test
    @LoadPCMInstanceFromBundle(bundleName = "org.palladiosimulator.simulizar.tests", basePath = "testmodels/usageevolutionTest", modelFiles = {
            "default.allocation", "default.usagemodel", "default.repository", "default.usageevolution" })
    @SimulationConfig(maxMeasurements = "100")
    @RunSimuLizar
    void testUsageevolutionResponseTimeTimestampTuple(UsageScenario scenario, ExperimentRun expRun)
            throws JobFailedException, UserCanceledException {
        var measurement = MeasurementTestUtils.getMeasurementOfAt(expRun.getMeasurement(),
                MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, scenario);
        assertTrue(measurement.isPresent());

        List<Measure<?, Quantity>> responseTimeMeasurements = MeasurementTestUtils
            .allMeasurementsOfMetric(measurement.get(), MetricDescriptionConstants.RESPONSE_TIME_METRIC);
        List<Measure<?, Quantity>> timeMeasurements = MeasurementTestUtils.allMeasurementsOfMetric(measurement.get(),
                MetricDescriptionConstants.POINT_IN_TIME_METRIC);
        assertEquals(responseTimeMeasurements.size(), timeMeasurements.size());

        // The simulated test scenario evolves response time and user amount.
        // Population is 1 and Think Time is 99.0
        // It evolves in 3 stages.
        // Stage 1: 2000s with Constant 1.0
        // Stage 2: 2000s with Constant 2.0 (Leads to Response Time 4.0 as both values evolve)
        // Stage 3: 6000s with Constant 1.0
        // It is important to know, that this constant evolves the user requests from 1 to 2 aswell
        // as the response time.
        // In Stage 2, two requests finish at the exact same time stamp.
        // Section 1 -> Section 2 time stamps with 2 measurements at the same time for whole section
        // 2 with response time 4s
        assertEquals((Double) timeMeasurements.get(19)
            .getValue(), 1901.0, 0.001);
        assertEquals((Double) timeMeasurements.get(20)
            .getValue(), 2004.0, 0.001);
        assertEquals((Double) timeMeasurements.get(21)
            .getValue(), 2004.0, 0.001);
        assertEquals((Double) timeMeasurements.get(22)
            .getValue(), 2107.0, 0.001);
        // Section 1 -> Section 2 matching evolution of response time
        assertEquals((Double) responseTimeMeasurements.get(19)
            .getValue(), 1.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(20)
            .getValue(), 4.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(21)
            .getValue(), 4.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(22)
            .getValue(), 4.0, 0.001);
        // Section 2 -> Section 3 same as above but with Section 3 only having 1 user again.
        assertEquals((Double) timeMeasurements.get(59)
            .getValue(), 3961.0, 0.001);
        assertEquals((Double) timeMeasurements.get(60)
            .getValue(), 4061.0, 0.001);
        assertEquals((Double) timeMeasurements.get(61)
            .getValue(), 4161.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(59)
            .getValue(), 4.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(60)
            .getValue(), 1.0, 0.001);
        assertEquals((Double) responseTimeMeasurements.get(61)
            .getValue(), 1.0, 0.001);
    }
}
