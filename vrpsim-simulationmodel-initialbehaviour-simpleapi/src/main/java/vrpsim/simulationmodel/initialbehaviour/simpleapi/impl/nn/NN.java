package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.nn;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

public class NN implements IInitialBehaviourProviderHandler {

	private CustomerAPI getNN(CustomerAPI customer, List<CustomerAPI> customers) {

		EuclideanDistance ed = new EuclideanDistance();
		double[] refCoord = new double[2];
		refCoord[0] = customer.getLocation().getxCoord();
		refCoord[1] = customer.getLocation().getyCoord();

		CustomerAPI nnCustomer = null;
		double min = Double.MAX_VALUE;

		for (CustomerAPI c : customers) {
			double[] cCoord = new double[2];
			cCoord[0] = c.getLocation().getxCoord();
			cCoord[1] = c.getLocation().getyCoord();
			double distance = ed.compute(refCoord, cCoord);
			if (distance != 0 && distance < min) {
				min = distance;
				nnCustomer = c;
			}
		}
		return nnCustomer;
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		// Static problem

		List<CustomerAPI> orderedCustomer = new ArrayList<>();
		CustomerAPI workwithCustomer = customers.remove(0);
		orderedCustomer.add(workwithCustomer);
		while (customers.size() > 0) {
			CustomerAPI nnCustomer = getNN(workwithCustomer, customers);
			orderedCustomer.add(nnCustomer);
			customers.remove(nnCustomer);
			workwithCustomer = nnCustomer;
		}

		List<IJob> jobs = new ArrayList<>();
		jobs.add(depots.get(0));
		jobs.addAll(orderedCustomer);
		jobs.add(depots.get(0));

		TourAPI tourAPI = new TourAPI(jobs, vehicles.get(0), drivers.get(0));
		List<TourAPI> tours = new ArrayList<>();
		tours.add(tourAPI);
		return tours;

	}

}
