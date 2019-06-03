package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.twoopt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

public class TwoOptStatic implements IInitialBehaviourProviderHandler {

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		HashMap<String, IJob> custBack = new HashMap<>();
		Point[] points = new Point[customers.size() + 1];
		DepotAPI depot = depots.get(0);
		custBack.put(depot.getId(), depot);
		Point d = new Point(depot.getLocation().getxCoord(), depot.getLocation().getyCoord(), depot.getId(), false, false, true);
		points[0] = d;

		for (int i = 0; i < customers.size(); i++) {
			CustomerAPI customer = customers.get(i);
			custBack.put(customer.getId(), customer);
			points[i + 1] = new Point(customer.getLocation().getxCoord(), customer.getLocation().getyCoord(), customer.getId(), false,
					false, false);
		}

		final FLS fls = new FLS();
		fls.optimise(points);

		int depotIndex = getDepotIndex(points);
		if (depotIndex < 0) {
			throw new RuntimeException("No Deopt found.");
		}

		List<IJob> jobs = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			int index = (i + depotIndex) % points.length;
			jobs.add(custBack.get(points[index].getId()));
		}
		jobs.add(custBack.get(points[depotIndex].getId()));

		TourAPI tour = new TourAPI(jobs, vehicles.get(0), drivers.get(0));
		List<TourAPI> tours = new ArrayList<>();
		tours.add(tour);
		return tours;
	}

	private int getDepotIndex(Point[] points) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].isDepot()) {
				return i;
			}
		}
		return -1;
	}

}
