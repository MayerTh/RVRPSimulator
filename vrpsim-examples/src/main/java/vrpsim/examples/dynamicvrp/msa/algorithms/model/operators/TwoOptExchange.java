package vrpsim.examples.dynamicvrp.msa.algorithms.model.operators;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Route;
import vrpsim.examples.dynamicvrp.msa.algorithms.model.Stop;

public class TwoOptExchange implements IntraRouteOperator {

	private static Logger logger = LoggerFactory.getLogger(TwoOptExchange.class);

	private IClock clock;
	private StructureService structureService;

	@Override
	public List<Route> execute(Route route, Stop stop) throws OperatorException {

		List<Route> newRoutes = new ArrayList<>();
		if (!route.contains(stop)) {
			throw new OperatorException("The given Customer/Stop is not within the given Route.");
		}

		int i = route.getStops().indexOf(stop);
		for (int k = 0; k < route.getStops().size(); k++) {
			if (i != k) {
				try {
					logger.debug("Swapping customer/stop at position {} with customer/stop at position {}", i, k);
					Route newRoute = twoOptSwap(route, i, k);
					if (newRoute.isFeasible(structureService, clock)) {
						newRoutes.add(newRoute);
					}
				} catch (VRPArithmeticException e) {
					logger.error(
							"Feasability of new route could not be calculated because of an VRPArithmeticException. Message: "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return newRoutes;
	}

	/**
	 * https://en.wikipedia.org/wiki/2-opt Swaps Stop at position i with stop at
	 * position k.
	 */
	private Route twoOptSwap(Route route, int i, int k) {

		logger.debug("In: " + route.getStops());
		logger.debug("Swapping position {} with position {}.", i, k);

		List<Stop> newStopOrder = new ArrayList<>();

		int smaller = (i < k) ? i : k;
		int bigger = (i < k) ? k : i;

		// 1. take route[1] to route[i-1] and add them in order to new_route
		for (int index = 0; index < smaller; index++) {
			Stop stop = route.getStops().get(index);
			newStopOrder.add(stop);
		}
		// 2. take route[i] to route[k] and add them in reverse order to
		// new_route
		for (int index = bigger; index >= smaller; index--) {
			Stop stop = route.getStops().get(index);
			newStopOrder.add(stop);
		}
		// 3. take route[k+1] to end and add them in order to new_route
		for (int index = bigger + 1; index < route.getStops().size(); index++) {
			Stop stop = route.getStops().get(index);
			newStopOrder.add(stop);
		}

		Route newRoute = new Route(newStopOrder, route.getVehicleId(), route.getDepotClosingTime());
		logger.debug("Out: " + newRoute.getStops());
		return newRoute;
	}

	@Override
	public void setService(IClock clock, StructureService structureService) {
		this.clock = clock;
		this.structureService = structureService;

	}

}
