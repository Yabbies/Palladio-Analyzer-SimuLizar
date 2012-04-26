/**
 * 
 */
package de.upb.pcm.interpreter.interpreter;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.pcm.usagemodel.EntryLevelSystemCall;
import de.uka.ipd.sdq.pcm.usagemodel.UsageScenario;
import de.uka.ipd.sdq.pcm.usagemodel.UsagemodelPackage;
import de.upb.pcm.interpreter.exceptions.PCMModelInterpreterException;
import de.upb.pcm.interpreter.interpreter.listener.EventType;
import de.upb.pcm.interpreter.interpreter.listener.IInterpreterListener;
import de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent;

/**
 * @author snowball
 *
 */
public class EventNotificationHelper {

	final Collection<IInterpreterListener> listener;

	public EventNotificationHelper() {
		super();
		this.listener = new ArrayList<IInterpreterListener>();
	}
	
	public void addListener(IInterpreterListener singleListener) {
		this.listener.add(singleListener);
	}
	
	public void removeListener(IInterpreterListener singleListener) {
		this.listener.remove(singleListener);
	}

	@SuppressWarnings("unchecked")
	<T extends EObject> void firePassedEvent(ModelElementPassedEvent<T> event) {
		for (IInterpreterListener singleListener : listener){
			switch(event.getModelElement().eClass().getClassifierID()) {
			case UsagemodelPackage.USAGE_SCENARIO:
				if (event.getEventType() == EventType.BEGIN)
					singleListener.beginUsageScenarioInterpretation((ModelElementPassedEvent<UsageScenario>) event);
				else
					singleListener.endUsageScenarioInterpretation((ModelElementPassedEvent<UsageScenario>) event);
				break;
			case UsagemodelPackage.ENTRY_LEVEL_SYSTEM_CALL:
				if (event.getEventType() == EventType.BEGIN)
					singleListener.beginEntryLevelSystemCallInterpretation((ModelElementPassedEvent<EntryLevelSystemCall>) event);
				else
					singleListener.endEntryLevelSystemCallInterpretation((ModelElementPassedEvent<EntryLevelSystemCall>) event);
				break;
			default:
				throw new PCMModelInterpreterException("Tried to fire unknown event");
			}
		}
	}

	public void removeAllListener() {
		this.listener.removeAll(new ArrayList<IInterpreterListener>(this.listener));
	}
}
