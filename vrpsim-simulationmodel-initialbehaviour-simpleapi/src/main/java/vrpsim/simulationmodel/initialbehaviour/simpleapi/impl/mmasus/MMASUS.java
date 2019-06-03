package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import isula.aco.exception.InvalidInputException;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

public class MMASUS implements IInitialBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(MMASUS.class);
	private final boolean applyLocalOptimizationForEveryBestAnt;
	
	public MMASUS(boolean applyLocalOptimizationForEveryBestAnt) {
		this.applyLocalOptimizationForEveryBestAnt = applyLocalOptimizationForEveryBestAnt;
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		// Static problem
		MMASUSImpl aocImpl = new MMASUSImpl(this.applyLocalOptimizationForEveryBestAnt);
		Integer[] solution = null;
		try {
			// TODO correct initial best solution (now it is 50)
			solution = aocImpl.impl(getProblemGraph(customers, depots.get(0)), 50);
		} catch (ConfigurationException | InvalidInputException e) {
			e.printStackTrace();
		}

		List<IJob> jobs = new ArrayList<>();
		int depotIndex = getDepotIndex(solution);
		for (int i = 0; i < solution.length; i++) {
			int index = (i + depotIndex) % solution.length;
			if (solution[index] >= customers.size()) {
				jobs.add(depots.get(0));
			} else {
				jobs.add(customers.get(solution[index]));
			}
		}
		jobs.add(depots.get(0));
		List<TourAPI> tours = new ArrayList<>();
		tours.add(new TourAPI(jobs, vehicles.get(0), drivers.get(0)));
		return tours;
	}

	private int getDepotIndex(Integer[] solution) {
		// Depot index is the number with the higest value.
		int max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int index = 0; index < solution.length; index++) {
			if (solution[index] > max) {
				maxIndex = index;
				max = solution[index];
			}
		}
		return maxIndex;
	}

	private double[][] getProblemGraph(List<CustomerAPI> customers, DepotAPI depot) {
		double[][] result = new double[customers.size() + 1][2];
		for (int i = 0; i < customers.size(); i++) {
			CustomerAPI customer = customers.get(i);
			result[i][0] = customer.getLocation().getxCoord();
			result[i][1] = customer.getLocation().getyCoord();
		}
		result[customers.size()][0] = depot.getLocation().getxCoord();
		result[customers.size()][1] = depot.getLocation().getyCoord();
		return result;
	}

}
