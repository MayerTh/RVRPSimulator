package vrpsim.examples.dynamicvrp.msa.instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.VRPSimulationModelParameters;
import vrpsim.core.model.network.DefaultNode;
import vrpsim.core.model.network.DefaultWay;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.depot.SourceDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.CanStoreParameters;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.StorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.structure.vehicle.DefaultVehicle;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.functions.DeterministicTimeFunction;
import vrpsim.core.model.util.functions.Euclidean2DDistanceFunction;
import vrpsim.core.model.util.functions.IDistanceFunction;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.functions.LinearMotionTravelTimeFunction;
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.UncertainParamters;

public class BentInstanceLoader {

	Logger logger = LoggerFactory.getLogger(BentInstanceLoader.class);

	private final CanStoreType canStoreType = new CanStoreType("pizza-carton-storage"); // 1
	private final StorableType storableType = new StorableType("pizza-carton", canStoreType); // 2

	private final String capacityUnit = "piece"; // 3
	private final Capacity singleCapacity = new Capacity(capacityUnit, 1.0); // 4
	private final StorableParameters storableParameters = new StorableParameters(1, singleCapacity, storableType); // 5

	public VRPSimulationModel loadBentInstance(String path) throws IOException {
		File file = new File(this.getClass().getResource(path).getFile());
		Network network = createNetwork(file);
		Structure structure = createStructure(file, network.getNetworkService());
		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters(path, path);
		return new VRPSimulationModel(vrpSimulationModelParameters, structure, network);
	}

	private Structure createStructure(File file, NetworkService networkService) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		String[] firstLine = br.readLine().split("\\t");
		List<IVehicle> vehicles = getVehicles(Integer.parseInt(firstLine[7]), Double.parseDouble(firstLine[3]),
				networkService);
		List<IDepot> depots = getDepots(networkService);
		
		List<ICustomer> customers = new ArrayList<ICustomer>();
		List<IDriver> drivers = new ArrayList<IDriver>();
		List<IOccasionalDriver> occasionalDrivers = new ArrayList<IOccasionalDriver>();

		br.close();

		Structure structure = new Structure(storableParameters, depots, customers, vehicles, drivers,
				occasionalDrivers);
		return structure;
	}

	private List<IDepot> getDepots(NetworkService networkService) {
		// Id + prio + where is the depot located.
		VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
				"Depot", 1); // 1
		VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
				networkService.getNetworkElement("D")); // 2

		// Are there frequently coming new storables into the depot?
		UncertainParamters arrivalParameters = new UncertainParamters(); // 3

		// We create our first compartment where we can store
		CanStoreParameters cartonStorageParameters = new CanStoreParameters(this.canStoreType,
				new Capacity(this.capacityUnit, 1000.0), new LIFOLoadingPolicy(),
				new StorableGenerator(this.storableParameters));
		ICanStore cartonStorage = new Compartment(cartonStorageParameters);
		DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(cartonStorage));

		// The depot
		IDepot depot = new SourceDepot(vrpSimulationModelElementParameters,
				vrpSimulationModelStructureElementParameters, arrivalParameters, storageManager, new DeterministicTimeFunction(0.0));

		// Return a list with our depot.
		List<IDepot> depots = new ArrayList<IDepot>();
		depots.add(depot);
		return depots;
	}

	private List<IVehicle> getVehicles(int number, double capacity, NetworkService networkService) {

		List<IVehicle> vehicles = new ArrayList<>();
		INode vehicleHome = (INode) networkService.getNetworkElement(BentInstanceLoaderConstants.DEPOT_INDEX);

		for (int i = 0; i < number; i++) {

			VRPSimulationModelElementParameters smep = new VRPSimulationModelElementParameters("vehicle-" + i, 1);
			VRPSimulationModelStructureElementParameters smsep = new VRPSimulationModelStructureElementParameters(
					vehicleHome);

			CanStoreParameters csp = new CanStoreParameters(canStoreType, new Capacity(capacityUnit, capacity),
					new LIFOLoadingPolicy(), new StorableGenerator(storableParameters));

			IVehicle vehicle = new DefaultVehicle(smep, smsep, new UncertainParamters(),
					new DefaultStorageManager(new DefaultStorage(new Compartment(csp))), 100.0);

			vehicles.add(vehicle);
		}
		return vehicles;
	}

	private Network createNetwork(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		List<INode> nodes = new ArrayList<>();

		boolean nodesCreated = false;
		while (!nodesCreated) {
			String line = br.readLine();
			if (isTableHeader(line)) {
				line = br.readLine();
				while (!line.isEmpty()) {

					String[] split = line.split("\\t");
					String id = split[0];
					String cx = split[1];
					String cy = split[2];

					VRPSimulationModelElementParameters parameters = new VRPSimulationModelElementParameters(id, 1);
					INode node = new DefaultNode(parameters,
							new Location(Integer.parseInt(cx), Integer.parseInt(cy), 0));
					nodes.add(node);

					logger.debug("Node with id {} created at location {},{},0.", id, cx, cy);

					line = br.readLine();
				}
				nodesCreated = true;
			}
		}
		br.close();

		List<IWay> ways = new ArrayList<IWay>();
		int wayCounter = 1;
		for (INode node_1 : nodes) {
			for (INode node_2 : nodes) {
				if (!node_1.equals(node_2)) {
					VRPSimulationModelElementParameters para = new VRPSimulationModelElementParameters(
							wayCounter++ + "", 1);
					ITimeFunction travelTimeFunction = new LinearMotionTravelTimeFunction();
					IDistanceFunction distanceFunction = new Euclidean2DDistanceFunction();
					double maxSpeed = 50.0;
					INode source = node_1;
					INode target = node_2;
					IWay way = new DefaultWay(para, travelTimeFunction, source, target, distanceFunction, maxSpeed);
					node_1.addWay(way);
					ways.add(way);
				}
			}
		}

		Network network = new Network(nodes, ways);
		return network;
	}

	private boolean isTableHeader(String line) {
		return line != null ? line.startsWith(BentInstanceLoaderConstants.TEABLE_HEADER_CUSTOMER + "\t") : false;
	}
}
