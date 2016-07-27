package vrpsim.examples.dynamicvrp.msa.algorithms.operators;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.simulator.IClock;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Route;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Stop;

public class TwoOptExchange implements IntraRouteOperator {

	private NetworkService networkService;
	private StructureService structureService;
	private IClock clock;

	@Override
	public List<Route> execute(Route route, Stop stop) throws OperatorException {

		List<Route> newRoutes = new ArrayList<>();
		if (!route.contains(stop)) {
			throw new OperatorException("The given Stop/Customer is not within the given Route.");
		}

//		for
//			for
//				2optSwap(Route route, i, k)
		
		return newRoutes;
	}

	/**
	 * https://en.wikipedia.org/wiki/2-opt
	 */
	private Route twoOptSwap(Route route, int i, int k) {
		return null;
	}

	@Override
	public void setServices(NetworkService networkService, StructureService structureService, IClock clock) {
		this.networkService = networkService;
		this.structureService = structureService;
		this.clock = clock;
	}

}
