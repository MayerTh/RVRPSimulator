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
/**
 * 
 */
package vrpsim.util.model.instances.generator.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;
import org.vrprep.model.util.Instances;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.VRPSimulationModelParameters;
import vrpsim.core.model.network.DefaultNode;
import vrpsim.core.model.network.DefaultWay;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.StaticCustomerWithConsumption;
import vrpsim.core.model.structure.depot.DefaultDepot;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.DefaultDriver;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.CanStoreParameters;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.structure.vehicle.DefaultVehicle;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.functions.DeterministicTimeFunction;
import vrpsim.core.model.util.functions.Euclidean2DDistanceFunction;
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.DeterministicDistributionFunction;
import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.model.util.uncertainty.UncertainParamters;

/**
 * @date 24.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class VRPREPImporter {

	private static Logger logger = LoggerFactory.getLogger(VRPREPImporter.class);

	private final String STORAGE_TYPE_1;
	private final String STORABLE_TYPE_1;
	private final String CAPACITY_UNIT_1;
	private final double consumptionCycleTime;
	private final double maxCustomerStorageCapacity;
	private final int numberOfStorablesInDepot;
	private final int numberOfVehicles;

	private final String capacityUnit;
	private final CanStoreType canStoreType;
	private final StorableParameters storableParameters;

	public VRPREPImporter(String STORAGE_TYPE_1, String STORABLE_TYPE_1, String CAPACITY_TYPE_1, double consumptionCycleTime, double maxCustomerStorageCapacity,
			int numberOfStorablesInDepot, int numberOfVehicles) {
		this.STORAGE_TYPE_1 = STORAGE_TYPE_1;
		this.STORABLE_TYPE_1 = STORABLE_TYPE_1;
		this.CAPACITY_UNIT_1 = CAPACITY_TYPE_1;
		this.consumptionCycleTime = consumptionCycleTime;
		this.maxCustomerStorageCapacity = maxCustomerStorageCapacity;
		this.numberOfStorablesInDepot = numberOfStorablesInDepot;
		this.numberOfVehicles = numberOfVehicles;

		this.canStoreType = new CanStoreType(this.STORAGE_TYPE_1);
		this.capacityUnit = this.CAPACITY_UNIT_1;
		this.storableParameters = new StorableParameters(1, new Capacity(this.capacityUnit, 1.0), new StorableType(this.STORABLE_TYPE_1, this.canStoreType));
	}

	public VRPSimulationModel getSimulationModelWithoutSolutionFromVRPREPModel(Path inputPath) throws JAXBException, VRPArithmeticException, StorageException {

		Instance instance = loadInstance(inputPath);
		Network network = getNetworkFromVRPREP(instance);
		List<ICustomer> customers = getCustomersFromVRPREP(instance, network.getNodes());
		List<IDepot> depots = getDepotsFromVRPREP(instance, network.getNodes());
		List<IVehicle> vehicles = getVehiclesFromVRPREP(instance, network.getNodes());
		List<IDriver> drivers = getDriversFromVRPREP(instance, vehicles);
		List<IOccasionalDriver> occasionalDrivers = new ArrayList<>();
		Structure structure = new Structure(this.storableParameters, depots, customers, vehicles, drivers, occasionalDrivers);

		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters(inputPath.toString(), "Thomas Mayer");
		logger.debug("Simultaion model generated");

		return new VRPSimulationModel(vrpSimulationModelParameters, structure, network);
	}

	private List<IDriver> getDriversFromVRPREP(Instance instance, List<IVehicle> vehicles) {
		VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DRIVER-1", 0);
		VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
				vehicles.get(0).getVRPSimulationModelStructureElementParameters().getHome());
		UncertainParamters breakdownParameters = new UncertainParamters();
		DefaultDriver driver = new DefaultDriver(elementParameters, structureElementParameters, breakdownParameters);

		List<IDriver> drivers = new ArrayList<IDriver>();
		drivers.add(driver);
		return drivers;
	}

	private List<IVehicle> getVehiclesFromVRPREP(Instance instance, List<INode> nodes) {

		List<IVehicle> vehicles = new ArrayList<IVehicle>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();
		Double vehicleCapacity = instance.getFleet().getVehicleProfile().get(0).getCapacity();

		for (int i = 0; i < this.numberOfVehicles; i++) {
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

			// No breakdown.
			UncertainParamters breakdownParameters = new UncertainParamters();

			CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType, new Capacity(capacityUnit, vehicleCapacity), new LIFOLoadingPolicy(),
					new StorableGenerator(this.storableParameters));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("VEHICLE-" + i, 0);
			IVehicle vehicle = new DefaultVehicle(elementParameters, structureElementParameters, breakdownParameters, storageManager, 80.0);
			vehicles.add(vehicle);
			logger.debug("Vehicle {} created with capacity {} and location {}", vehicle.getVRPSimulationModelElementParameters().getId(), vehicleCapacity, nodeId);
		}

		return vehicles;
	}

	private List<IDepot> getDepotsFromVRPREP(Instance instance, List<INode> nodes) throws VRPArithmeticException, StorageException {
		List<IDepot> depots = new ArrayList<IDepot>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();

		VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DEPOT", 0);
		VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

		// No arrival.
		UncertainParamters arrivalParameters = new UncertainParamters();
		StorableGenerator storableGenerator = new StorableGenerator(this.storableParameters);

		CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType, new Capacity(capacityUnit, this.maxCustomerStorageCapacity), new LIFOLoadingPolicy(),
				storableGenerator);
		ICanStore compartment = new Compartment(compartmentParameters);
		DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

		IDepot depot = new DefaultDepot(elementParameters, structureElementParameters, arrivalParameters, storageManager, new DeterministicTimeFunction(0.0));

		logger.debug("Depot generated on location {}.", structureElementParameters.getHome().getVRPSimulationModelElementParameters().getId());

		for (int i = 0; i < this.numberOfStorablesInDepot; i++) {
			IStorable storable = storableGenerator.generateStorable(storableParameters);
			compartment.load(storable);
		}

		logger.debug("{} storables filled in created depot.", this.numberOfStorablesInDepot);

		depots.add(depot);
		return depots;
	}

	private List<ICustomer> getCustomersFromVRPREP(Instance instance, List<INode> nodes) {

		List<ICustomer> customers = new ArrayList<ICustomer>();
		for (Request request : instance.getRequests().getRequest()) {

			String requestId = request.getId().toString();
			double amount = request.getQuantity();
			String nodeId = request.getNode().toString();
			logger.debug("Request found with amount {} on node with id {}", amount, nodeId);

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("CUSTOMER-" + requestId, 0);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

			UncertainParameterContainer consumparameterContainer = new UncertainParameterContainer(storableParameters,
					new DeterministicDistributionFunction(amount), new DeterministicDistributionFunction(0.0), new DeterministicDistributionFunction(consumptionCycleTime), false);
			UncertainParamters consumptionParameters = new UncertainParamters(consumparameterContainer);

			CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType, new Capacity(capacityUnit, this.maxCustomerStorageCapacity), new LIFOLoadingPolicy(),
					new StorableGenerator(this.storableParameters));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			StaticCustomerWithConsumption dndc = new StaticCustomerWithConsumption(elementParameters, structureElementParameters, consumptionParameters, storageManager,
					new DeterministicTimeFunction(0.0));
			customers.add(dndc);
			logger.debug("Parameterized customer build {}.", dndc.getVRPSimulationModelElementParameters().getId());
		}

		return customers;
	}

	private Network getNetworkFromVRPREP(Instance instance) {

		List<INode> nodes = new ArrayList<INode>();
		for (Node node : instance.getNetwork().getNodes().getNode()) {
			logger.debug("Node {} with coords: {}, {}, {}.", node.getId(), node.getCx(), node.getCy(), node.getCz());
			Location location = new Location(node.getCx(), node.getCy(), node.getCz());
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters("NODE-" + node.getId().toString(), 0);
			INode simNode = new DefaultNode(vrpSimulationModelElementParameters, location);
			nodes.add(simNode);
			logger.debug("StaticDefaultNode build {}", simNode.getVRPSimulationModelElementParameters().getId());
		}

		List<IWay> allWays = new ArrayList<IWay>();
		for (INode node1 : nodes) {

			List<IWay> ways = new ArrayList<IWay>();
			for (INode node2 : nodes) {

				// Connection within nodes
				// if (!node1.getVRPSimulationModelElementParameters().getId()
				// .equals(node2.getVRPSimulationModelElementParameters().getId()))
				// {

				String id = node1.getVRPSimulationModelElementParameters().getId() + "-" + node2.getVRPSimulationModelElementParameters().getId();
				VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(id, 0);
				IWay way = new DefaultWay(vrpSimulationModelElementParameters, new DeterministicTimeFunction(0.0), node1, node2, new Euclidean2DDistanceFunction(), 100.0);
				ways.add(way);

				logger.trace("Build StaticDefaultWay {} from {} to {}.", way.getVRPSimulationModelElementParameters().getId(),
						node1.getVRPSimulationModelElementParameters().getId(), node2.getVRPSimulationModelElementParameters().getId());
				// }

			}
			allWays.addAll(ways);
			node1.setWays(ways);
		}

		Network network = new Network(nodes, allWays);
		return network;
	}

	private Instance loadInstance(Path inputPath) throws JAXBException {
		Instance instance = Instances.read(inputPath);
		return instance;
	}

	private INode getNode(String nodeId, List<INode> nodes) {
		INode result = null;
		for (INode node : nodes) {
			if (node.getVRPSimulationModelElementParameters().getId().endsWith(nodeId)) {
				result = node;
				break;
			}
		}
		return result;
	}

}
