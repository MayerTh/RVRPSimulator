package vrpsim.examples.dynamicvrp.msa.algorithms.operators;

import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;

public interface Operator {
	
	public void setServices(NetworkService networkService, StructureService structureService, IClock clock);
	
}
