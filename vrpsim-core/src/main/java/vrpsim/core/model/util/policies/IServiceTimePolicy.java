package vrpsim.core.model.util.policies;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.simulator.IClock;

public interface IServiceTimePolicy {

	public Double getServiceTime(IVRPSimulationModelStructureElement p1, IVRPSimulationModelStructureElement p2,
			IVRPSimulationModelStructureElement p3, int amount, IClock clock);

}
