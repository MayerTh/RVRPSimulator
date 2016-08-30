package vrpsim.examples.dynamicvrp.msa.algorithms.model.operators;

import java.util.List;

import vrpsim.examples.dynamicvrp.msa.algorithms.model.RoutingPlan;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Stop;

public interface InterRouteOperator extends Operator {

	public List<RoutingPlan> execute(RoutingPlan route, Stop stop);
	
}
