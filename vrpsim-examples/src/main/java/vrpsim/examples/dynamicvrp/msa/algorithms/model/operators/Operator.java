package vrpsim.examples.dynamicvrp.msa.algorithms.model.operators;

import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;

public interface Operator {
	
	public void setService(IClock clock, StructureService structureService);
	
}
