/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.util.model.instances.generator.bent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import vrpsim.core.model.structure.customer.DynamicCustomer;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.StaticCustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.depot.SourceDepot;
import vrpsim.core.model.structure.driver.DefaultDriver;
import vrpsim.core.model.structure.driver.IDriver;
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
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.DeterministicDistributionFunction;
import vrpsim.core.model.util.uncertainty.UncertainParamters;

/**
 * {@link BentInstanceLoader} loads the problem instances introduced in: Bent,
 * Russell W., and Pascal Van Hentenryck.
 * "Scenario-based planning for partially dynamic vehicle routing with stochastic customers."
 * Operations Research 52.6 (2004): 977-987.
 * 
 * The instances are published from: Michael Saint-Guillain and Yves Deville and
 * Christine Solnon.
 * "A Multistage Stochastic Programming Approach to the Dynamic and Stochastic VRPTW"
 * Twelfth International Conference on Integration of Artificial Intelligence
 * (AI) and Operations Research (OR) techniques in Constraint Programming
 * (CPAIOR 2015).
 * 
 * The instances are are downloaded from:
 * http://becool.info.ucl.ac.be/resources/benchmarks-dynamic-and-stochastic-
 * vehicle-routing-problem-time-windows
 * 
 * @author mayert
 */
public class BentInstanceLoader {

	Logger logger = LoggerFactory.getLogger(BentInstanceLoader.class);

	private final CanStoreType canStoreType = new CanStoreType("package-storage"); // 1
	private final StorableType storableType = new StorableType("package", canStoreType); // 2

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
		br.close();

		List<IVehicle> vehicles = getVehicles(Integer.parseInt(firstLine[7]), Double.parseDouble(firstLine[3]),
				networkService);
		List<IDriver> drivers = getDrivers(Integer.parseInt(firstLine[7]), networkService);
		List<IDepot> depots = getDepots(networkService);
		List<ICustomer> customers = getCustomers(file, networkService);

		Structure structure = new Structure(storableParameters, depots, customers, vehicles, drivers, null);
		return structure;
	}

	private List<ICustomer> getCustomers(File file, NetworkService networkService) throws IOException {

		List<ICustomer> customers = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while (line != null) {

			if (line.startsWith(BentInstanceLoaderConstants.START_KNOWN_REQUESTS)) {
				while (!line.isEmpty()) {
					if (!line.startsWith(BentInstanceLoaderConstants.TEABLE_HEADER_CUSTOMER)
							&& !line.startsWith(BentInstanceLoaderConstants.START_KNOWN_REQUESTS)) {

						logger.debug("Start creating static customer from line: {}", line);

						// Create static customer.
						String[] staticCustomerInfo = line.split("\\t");
						String id = staticCustomerInfo[0];
						String earliestDueDate = staticCustomerInfo[2];
						String latestDueDate = staticCustomerInfo[3];
						String demand = staticCustomerInfo[5];
						String serviceTime = staticCustomerInfo[6];

						VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters(
								"staticcustomer-" + id, 1);
						VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
								networkService.getNetworkElement(id));

						UncertainParamters consumptionParameters = new UncertainParamters(
								new UncertainParamters.UncertainParameterContainer(this.storableParameters,
										new DeterministicDistributionFunction(Double.parseDouble(demand)),
										new DeterministicDistributionFunction(Double.parseDouble(earliestDueDate)),
										new DeterministicDistributionFunction(Double.parseDouble(latestDueDate)),
										false /* is cyclic */, false));

						// We create our first compartment where we can store
						CanStoreParameters cartonStorageParameters = new CanStoreParameters(this.canStoreType,
								new Capacity(this.capacityUnit, 100.0), new LIFOLoadingPolicy(),
								new StorableGenerator(this.storableParameters));
						ICanStore cartonStorage = new Compartment(cartonStorageParameters);
						DefaultStorageManager storageManager = new DefaultStorageManager(
								new DefaultStorage(cartonStorage));
						ITimeFunction service = new DeterministicTimeFunction(Double.parseDouble(serviceTime));

						StaticCustomer staticCustomer = new StaticCustomer(elementParameters,
								structureElementParameters, consumptionParameters, storageManager, service);

						logger.debug("Created static customer with id={} Start={}, Deadline={}, Demand={}, Service={}",
								id, earliestDueDate, latestDueDate, demand, serviceTime);
						customers.add(staticCustomer);

					}
					line = br.readLine();
				}
			}

			if (line.startsWith(BentInstanceLoaderConstants.START_UNKNOWN_REQUESTS)) {

				HashMap<String, DynamicCustomer> dynamicCustomers = new HashMap<>();

				while (line != null && !line.isEmpty()) {
					if (!line.startsWith(BentInstanceLoaderConstants.TEABLE_HEADER_CUSTOMER)
							&& !line.startsWith(BentInstanceLoaderConstants.START_UNKNOWN_REQUESTS)) {
						logger.debug("Start creating dynamic customer from line: {}", line);

						// Create static customer.
						String[] dynamicCustomerInfo = line.split("\\t");
						String id = dynamicCustomerInfo[0];
						String arrival = dynamicCustomerInfo[1];
						String earliestDueDate = dynamicCustomerInfo[2];
						String latestDueDate = dynamicCustomerInfo[3];
						String demand = dynamicCustomerInfo[5];
						String serviceTime = dynamicCustomerInfo[6];

						UncertainParamters.UncertainParameterContainer container = new UncertainParamters.UncertainParameterContainer(
								this.storableParameters,
								new DeterministicDistributionFunction(Double.parseDouble(demand)),
								new DeterministicDistributionFunction(Double.parseDouble(arrival)), null,
								new DeterministicDistributionFunction(Double.parseDouble(earliestDueDate)),
								new DeterministicDistributionFunction(Double.parseDouble(latestDueDate)), false);

						String finalCustomerId = "dynamiccustomer-" + id;

						if (!dynamicCustomers.containsKey(finalCustomerId)) {

							VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters(
									finalCustomerId, 1);
							VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
									networkService.getNetworkElement(id));

							UncertainParamters consumptionParameters = new UncertainParamters(container);

							// We create our first compartment where we can
							// store
							CanStoreParameters cartonStorageParameters = new CanStoreParameters(this.canStoreType,
									new Capacity(this.capacityUnit, 100.0), new LIFOLoadingPolicy(),
									new StorableGenerator(this.storableParameters));
							ICanStore cartonStorage = new Compartment(cartonStorageParameters);
							DefaultStorageManager storageManager = new DefaultStorageManager(
									new DefaultStorage(cartonStorage));
							ITimeFunction service = new DeterministicTimeFunction(Double.parseDouble(serviceTime));

							DynamicCustomer dynamicCustomer = new DynamicCustomer(elementParameters,
									structureElementParameters, storageManager, consumptionParameters, service);
							dynamicCustomers.put(dynamicCustomer.getVRPSimulationModelElementParameters().getId(),
									dynamicCustomer);

							logger.debug(
									"Created dynamic customer with id={} Arrival={}, Start={}, Deadline={}, Demand={}, Service={}",
									id, arrival, earliestDueDate, latestDueDate, demand, serviceTime);
							customers.add(dynamicCustomer);

						} else {
							dynamicCustomers.get(finalCustomerId).getUncertainParameters().addContainer(container);
							logger.debug(
									"Dynamic customer with id={} exists already. New UncertainParameterContainer added: Demand={}, Arrival={}, EarliestDD={}, LatestDD={}",
									id, demand, arrival, earliestDueDate, latestDueDate);
						}

					}

					line = br.readLine();
				}
			}

			line = br.readLine();
		}

		br.close();
		return customers;
	}

	private List<IDriver> getDrivers(int number, NetworkService networkService) {

		List<IDriver> drivers = new ArrayList<IDriver>();
		for (int i = 0; i < number; i++) {
			// Id + prio + where is the depot located.
			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters(
					"driver-" + (i + 1), 1);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
					networkService.getNetworkElement("D"));

			// We are modeling no sick leaves of a driver.
			UncertainParamters breakdownParameters = new UncertainParamters();
			DefaultDriver driver = new DefaultDriver(elementParameters, structureElementParameters,
					breakdownParameters);

			drivers.add(driver);
		}
		return drivers;
	}

	private List<IDepot> getDepots(NetworkService networkService) {
		// Id + prio + where is the depot located.
		VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
				"depot", 1); // 1
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
				vrpSimulationModelStructureElementParameters, arrivalParameters, storageManager,
				new DeterministicTimeFunction(0.0));

		// Return a list with our depot.
		List<IDepot> depots = new ArrayList<IDepot>();
		depots.add(depot);
		return depots;
	}

	private List<IVehicle> getVehicles(int number, double capacity, NetworkService networkService) {

		List<IVehicle> vehicles = new ArrayList<>();
		INode vehicleHome = (INode) networkService.getNetworkElement(BentInstanceLoaderConstants.DEPOT_INDEX);

		for (int i = 0; i < number; i++) {

			VRPSimulationModelElementParameters smep = new VRPSimulationModelElementParameters("vehicle-" + (i + 1), 1);
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
					/*
					 * Since Solomon 1987 (ALGORITHMS FOR THE VEHICLE ROUTING
					 * AND SCHEDULING PROBLEMS WITH TIME WINDOW CONSTRAINT),
					 * where the instances original coming from, travel time is
					 * equal to the distance between customers: "All the test
					 * problems are 100-customer euclidean problems. This
					 * problem size is not limiting for the methods presented,
					 * since much larger problems could be solved. Travel times
					 * between customers are taken to equal the corresponding
					 * distances."
					 */
					ITimeFunction travelTimeFunction = new SolomonTravelTimeFunction();
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
