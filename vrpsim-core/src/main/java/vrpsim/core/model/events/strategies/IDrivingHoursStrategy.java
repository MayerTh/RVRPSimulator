package vrpsim.core.model.events.strategies;

import vrpsim.core.model.events.IEventStrategy;
import vrpsim.core.model.events.impl.EndWorkEvent;
import vrpsim.core.model.events.impl.StartWorkEvent;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;

public interface IDrivingHoursStrategy extends IEventStrategy  {

	/**
	 * Return the {@link StartWorkEvent}.
	 * 
	 * @param movable
	 * @return
	 */
	public StartWorkEvent getStart(IVRPSimulationModelStructureElement element);

	/**
	 * Returns the {@link EndWorkEvent}.
	 * 
	 * @param breakdownEvent
	 * @param movable
	 * @return
	 */
	public EndWorkEvent getEnd(IVRPSimulationModelStructureElement element);

}
