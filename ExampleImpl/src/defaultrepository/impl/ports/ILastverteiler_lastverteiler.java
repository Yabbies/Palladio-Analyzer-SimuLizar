
	  package defaultrepository.impl.ports;
      
   
	  /** 
	   * Operation Provide Role Port class for ILastverteiler_lastverteiler 
	   */
	  public class ILastverteiler_lastverteiler extends de.uka.ipd.sdq.prototype.framework.port.AbstractBasicPort<defaultrepository.impl.Ilastverteiler> implements defaultrepository.ILastverteiler {	  

		  protected static org.apache.log4j.Logger logger = 
			org.apache.log4j.Logger.getLogger(ILastverteiler_lastverteiler.class.getName());
				
		  public ILastverteiler_lastverteiler(defaultrepository.impl.Ilastverteiler myComponent, String assemblyContextParentStructure) throws java.rmi.RemoteException {
		  		this.myComponent = myComponent;
				de.uka.ipd.sdq.prototype.framework.registry.RmiRegistry.registerPort(de.uka.ipd.sdq.prototype.framework.registry.RmiRegistry.getRemoteAddress(), this, assemblyContextParentStructure + "ILastverteiler_lastverteiler");
		  }
		    
	      


	      
   public 
	
   
   de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe<Object>

   entpacke0
   ( 
	de.uka.ipd.sdq.simucomframework.variables.StackContext ctx
 )
 throws java.rmi.RemoteException
 {
   	  
   
	return myComponent.iLastverteiler_entpacke0(
	     
	ctx
);


   }   

	  }


   