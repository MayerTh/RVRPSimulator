package vrpsim.examples.dynamicvrp.msa.algorithms.model;

import java.util.List;

public class RoutingPlan {

	private List<Route> routes;

	/**
	 * Validates the {@link RoutingPlan}. Throws an {@link RuntimeException} if
	 * plan is invalid.
	 */
	public void validate() {
//		if (!isRoutingPlanValid()) {
//			throw new RuntimeException("Routing plan is invalid, due to customers getting served more than once.");
//		}
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	private boolean isFeasible() {
//		HashSet<String> ids = new HashSet<>();
//		for (Route route : this.routes) {
//			for (String customerId : route.getCustomerIds()) {
//				if (ids.contains(customerId)) {
//					return false;
//				} else {
//					ids.add(customerId);
//				}
//			}
//		}
		return true;
	}

}
