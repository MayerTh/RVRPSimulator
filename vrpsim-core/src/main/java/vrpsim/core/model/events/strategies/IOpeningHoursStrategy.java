package vrpsim.core.model.events.strategies;

import vrpsim.core.model.events.IEventStrategy;
import vrpsim.core.model.events.impl.CloseEvent;
import vrpsim.core.model.events.impl.OpenEvent;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;

public interface IOpeningHoursStrategy extends IEventStrategy  {

	/**
	 * Return the {@link OpenEvent}.
	 * 
	 * @param movable
	 * @return
	 */
	public OpenEvent getOpen(IVRPSimulationModelStructureElementWithStorage withStorage);

	/**
	 * Returns the {@link CloseEvent}.
	 * 
	 * @param breakdownEvent
	 * @param movable
	 * @return
	 */
	public CloseEvent getClose(IVRPSimulationModelStructureElementWithStorage withStorage);

	public void reset();

}
