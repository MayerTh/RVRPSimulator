package vrpsim.examples.dynamicvrp.msa.algorithms.operators;

import java.util.List;

import vrpsim.examples.dynamicvrp.msa.algorithms.model.Route;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Stop;

public interface IntraRouteOperator extends Operator {

	public List<Route> execute(Route route, Stop stop) throws OperatorException;

}
