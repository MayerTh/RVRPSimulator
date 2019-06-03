package vrpsim.core.model.util.policies.impl;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.util.policies.IServiceTimePolicy;
import vrpsim.core.simulator.IClock;

public class NoServiceTimePolicy implements IServiceTimePolicy {

	@Override
	public Double getServiceTime(IVRPSimulationModelStructureElement p1, IVRPSimulationModelStructureElement p2,
			IVRPSimulationModelStructureElement p3, int amount, IClock clock) {
		return 0d;
	}

}
