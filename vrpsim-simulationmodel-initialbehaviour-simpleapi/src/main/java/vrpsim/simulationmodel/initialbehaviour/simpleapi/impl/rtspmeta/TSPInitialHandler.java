package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.rtspmeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.r.util.api.IRServiceAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

public class TSPInitialHandler implements IInitialBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(TSPInitialHandler.class);

	private String method = "2-opt";
	private int seed = 123;

	private final IRServiceAPI rServiceAPI;
	private final String rConnectionAdress;
	private final int rConnectionPort;

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getAvailableMethods() {
		return new String[] { "2-opt", "nearest_insertion", "farthest_insertion", "cheapest_insertion", "arbitrary_insertion", "nn",
				"repetitive_nn" };
	}

	public TSPInitialHandler() throws InstantiationException, IllegalAccessException {
		this.rServiceAPI = IRServiceAPI.load("vrpsim");
		this.rConnectionAdress = IRServiceAPI.standardAdress;
		this.rConnectionPort = IRServiceAPI.standardPort;
	}

	public TSPInitialHandler(String rConnectionAdress, int rConnectionPort) throws InstantiationException, IllegalAccessException {
		this.rServiceAPI = IRServiceAPI.load("vrpsim");
		this.rConnectionAdress = rConnectionAdress;
		this.rConnectionPort = rConnectionPort;
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {

		RConnection connection = rServiceAPI.establishRConnection(rConnectionAdress, rConnectionPort);
		List<String> script = buildScript(method, buildData(depots.get(0), customers), seed);
		connection = rServiceAPI.evaluate(connection, script);

		List<TourAPI> tours = new ArrayList<>();

		try {

			double result = connection.eval("attr(tour, \"tour_length\")").asDouble();
			int[] tour = connection.eval("as.integer(tour)").asIntegers();

			logger.debug("Result is: {}", result);
			logger.debug("Tour is: {}", Arrays.toString(tour));

			// Depot is in the middle of the list
			int[] tourProcessed = new int[tour.length - 1];
			boolean depotFound = false;
			int depot = 1;
			int depotIndex = -1;
			int inserted = 0;
			for (int i = 0; i < tour.length; i++) {
				if (depotFound) {
					tourProcessed[inserted++] = tour[i];
				}
				if (tour[i] == depot) {
					depotIndex = i;
					depotFound = true;
				}
			}

			for (int i = 0; i < depotIndex; i++) {
				tourProcessed[inserted++] = tour[i];
			}

			logger.debug("Processed tour is: {}", Arrays.toString(tourProcessed));

			List<IJob> jobs = new ArrayList<>();
			jobs.add(depots.get(0));
			for (int i = 0; i < tourProcessed.length; i++) {
				jobs.add(customers.get(tourProcessed[i] - 2));
			}
			jobs.add(depots.get(0));

			tours.add(new TourAPI(jobs, vehicles.get(0), drivers.get(0)));

		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		connection.close();
		return tours;
	}

	private String[] buildData(DepotAPI depot, List<CustomerAPI> customers) {

		// data[0] x comma separated
		// data[1] y comma separated
		String[] data = new String[2];
		data[0] = depot.getLocation().getxCoord() + ",";
		data[1] = depot.getLocation().getyCoord() + ",";

		for (int i = 0; i < customers.size(); i++) {
			data[0] += customers.get(i).getLocation().getxCoord();
			data[1] += customers.get(i).getLocation().getyCoord();
			if (i < customers.size() - 1) {
				data[0] += ",";
				data[1] += ",";
			}
		}
		return data;
	}

	private List<String> buildScript(String method, String[] data, int seed) {
		List<String> result = new ArrayList<>();
		result.add("library(tspmeta)");
		result.add("set.seed(" + seed + ")");
		result.add("data.x  <- c(" + data[0] + ")");
		result.add("data.y  <- c(" + data[1] + ")");
		result.add("coords.df <- data.frame(long=data.x, lat=data.y)");
		result.add("coords.mx <- as.matrix(coords.df)");
		result.add("dist.mx <- dist(coords.mx)");
		result.add("mat <- as.matrix(dist.mx)");
		result.add("tsp.ins <- tsp_instance(coords.mx, mat )");
		result.add("tour <- run_solver(tsp.ins, method=\"" + method + "\")");
		return result;
	}

}
