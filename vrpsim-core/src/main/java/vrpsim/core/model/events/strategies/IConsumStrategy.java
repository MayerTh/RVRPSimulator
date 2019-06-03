package vrpsim.core.model.events.strategies;

import vrpsim.core.model.events.IEventStrategy;
import vrpsim.core.model.events.impl.ConsumEvent;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;

public interface IConsumStrategy extends IEventStrategy  {

	/**
	 * Returns the {@link ConsumEvent}.
	 * 
	 * @param withStorage
	 * @return
	 */
	public ConsumEvent getConsum(IVRPSimulationModelStructureElementWithStorage withStorage);

	public void reset();
}
