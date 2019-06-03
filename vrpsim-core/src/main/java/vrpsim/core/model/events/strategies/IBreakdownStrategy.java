package vrpsim.core.model.events.strategies;

import vrpsim.core.model.events.IEventStrategy;
import vrpsim.core.model.events.impl.BreakdownEvent;
import vrpsim.core.model.events.impl.RepairedEvent;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;

public interface IBreakdownStrategy extends IEventStrategy {

	/**
	 * Return the {@link BreakdownEvent}.
	 * 
	 * @param movable
	 * @return
	 */
	public BreakdownEvent getBreakdown(IVRPSimulationModelStructureElementMovable movable);

	/**
	 * Returns the {@link RepairedEvent} for the corresponding
	 * {@link BreakdownEvent}.
	 * 
	 * @param breakdownEvent
	 * @param movable
	 * @return
	 */
	public RepairedEvent getRepaired(BreakdownEvent breakdownEvent, IVRPSimulationModelStructureElementMovable movable);
	
}
