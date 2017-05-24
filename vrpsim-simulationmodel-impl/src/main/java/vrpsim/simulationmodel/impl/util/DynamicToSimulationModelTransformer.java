/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.simulationmodel.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

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
import vrpsim.core.model.structure.customer.DynamicCustomer;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.StaticCustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.depot.SourceDepot;
import vrpsim.core.model.structure.driver.DefaultDriver;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.util.storage.CanStoreParameters;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.StorableGenerator;
import vrpsim.core.model.structure.vehicle.DefaultVehicle;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.functions.DeterministicTimeFunction;
import vrpsim.core.model.util.functions.Euclidean2DDistanceFunction;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.functions.TravelTimeIsDistanceTimeFunction;
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.DeterministicDistributionFunction;
import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.simulationmodel.api.ITransformationConfiguration;

public class DynamicToSimulationModelTransformer {

	private static Logger logger = LoggerFactory.getLogger(DynamicToSimulationModelTransformer.class);

	private final DynamicVRPREPModel dynamicModel;
	private final ITransformationConfiguration transformationConfiguration;

	public DynamicToSimulationModelTransformer(DynamicVRPREPModel dynamicModel, ITransformationConfiguration transformationConfiguration) {
		this.dynamicModel = dynamicModel;
		this.transformationConfiguration = transformationConfiguration;
	}

	public VRPSimulationModel transformTo() {

		Network network = getNetwork(this.dynamicModel.getVRPREPInstance());
		List<ICustomer> customers = getCustomers(this.dynamicModel, network.getNodes());
		List<IDepot> depots = getDepots(this.dynamicModel.getVRPREPInstance(), network.getNodes());
		List<IVehicle> vehicles = getVehicles(this.dynamicModel.getVRPREPInstance(), network.getNodes());
		List<IDriver> drivers = getDrivers(this.dynamicModel.getVRPREPInstance(), vehicles);
		Structure structure = new Structure(this.transformationConfiguration.getSTORABLEPARAMETERS(), depots, customers, vehicles, drivers, new ArrayList<>());

		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters(new Long(this.dynamicModel.getId()).toString(), "Thomas Mayer");
		logger.debug("Simultaion model generated");

		return new VRPSimulationModel(vrpSimulationModelParameters, structure, network);
	}

	private List<IDriver> getDrivers(Instance instance, List<IVehicle> vehicles) {

		List<IDriver> drivers = new ArrayList<IDriver>();
		for (int i = 0; i < vehicles.size(); i++) {
			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DRIVER-" + i, 0);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
					vehicles.get(i).getVRPSimulationModelStructureElementParameters().getHome());

			UncertainParamters breakdownParameters = new UncertainParamters();
			DefaultDriver driver = new DefaultDriver(elementParameters, structureElementParameters, breakdownParameters);

			drivers.add(driver);
			logger.debug("Driver {} created at location {}", driver.getVRPSimulationModelElementParameters().getId(),
					vehicles.get(i).getVRPSimulationModelStructureElementParameters().getHome().getVRPSimulationModelElementParameters().getId());
		}
		return drivers;

	}

	private List<IVehicle> getVehicles(Instance instance, List<INode> nodes) {

		List<IVehicle> vehicles = new ArrayList<IVehicle>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();
		Double vehicleCapacity = instance.getFleet().getVehicleProfile().get(0).getCapacity();

		for (int i = 0; i < transformationConfiguration.getNUMBER_OF_VEHICLES(); i++) {
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

			// No breakdown.
			UncertainParamters breakdownParameters = new UncertainParamters();

			StorableGenerator storableGenerator = new StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS());

			CanStoreParameters compartmentParameters = new CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
					new Capacity(this.transformationConfiguration.getCAPACITY_UNIT(), instance.getFleet().getVehicleProfile().get(0).getCapacity()), new LIFOLoadingPolicy(),
					storableGenerator);
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("VEHICLE-" + i, 0);
			IVehicle vehicle = new DefaultVehicle(elementParameters, structureElementParameters, breakdownParameters, storageManager, 80.0);
			vehicles.add(vehicle);
			logger.debug("Vehicle {} created with capacity {} and location {}", vehicle.getVRPSimulationModelElementParameters().getId(), vehicleCapacity, nodeId);
		}

		return vehicles;
	}

	private List<IDepot> getDepots(Instance instance, List<INode> nodes) {

		List<IDepot> depots = new ArrayList<IDepot>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();

		VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DEPOT", 0);
		VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

		// No arrival.
		UncertainParamters arrivalParameters = new UncertainParamters();
		StorableGenerator storableGenerator = new StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS());

		CanStoreParameters compartmentParameters = new CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
				new Capacity(this.transformationConfiguration.getCAPACITY_UNIT(), this.transformationConfiguration.getMAX_CAPACITY_IN_DEPOT_STORAGE()), new LIFOLoadingPolicy(),
				storableGenerator);
		ICanStore compartment = new Compartment(compartmentParameters);
		DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

		IDepot depot = new SourceDepot(elementParameters, structureElementParameters, arrivalParameters, storageManager, new DeterministicTimeFunction(0.0));

		logger.debug("Depot {} generated at location {}.", elementParameters.getId(), structureElementParameters.getHome().getVRPSimulationModelElementParameters().getId());

		depots.add(depot);
		return depots;
	}

	private List<ICustomer> getCustomers(DynamicVRPREPModel dynamicModel, List<INode> nodes) {

		List<Request> allRequests = dynamicModel.getVRPREPInstance().getRequests().getRequest();
		List<ICustomer> customers = new ArrayList<ICustomer>();
		for (Request request : allRequests) {

			String requestIdstr = request.getId().toString();
			double amount = request.getQuantity();
			String nodeId = request.getNode().toString();
			boolean isDynamicRequest = dynamicModel.getDynamicRequestInformation().containsKey(request.getId());

			logger.debug("Request (isDynamicRequest={}) found with amount {} on node with id {}. ", isDynamicRequest, amount, nodeId);

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("CUSTOMER-" + requestIdstr, 0);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(getNode(nodeId, nodes));

			CanStoreParameters compartmentParameters = new CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
					new Capacity(this.transformationConfiguration.getCAPACITY_UNIT(), this.transformationConfiguration.getMAX_CAPACITY_IN_CUSTOMER_STORAGE()), new LIFOLoadingPolicy(),
					new StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS()));

			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));
			ITimeFunction serviceTimeFunction = new DeterministicTimeFunction(0.0);

			ICustomer customer = null;
			if (isDynamicRequest) {

				long startTime = this.dynamicModel.getDynamicRequestInformation().get(request.getId()).getArrivalTime();

				UncertainParameterContainer dynamicContainer = new UncertainParameterContainer(this.transformationConfiguration.getSTORABLEPARAMETERS(),
						new DeterministicDistributionFunction(request.getQuantity()), new DeterministicDistributionFunction((double) startTime));
				UncertainParamters orderParameters = new UncertainParamters(dynamicContainer);

				customer = new DynamicCustomer(elementParameters, structureElementParameters, storageManager, orderParameters, serviceTimeFunction);

			} else {

				UncertainParameterContainer staticContainer = new UncertainParameterContainer(this.transformationConfiguration.getSTORABLEPARAMETERS(),
						new DeterministicDistributionFunction(request.getQuantity()), new DeterministicDistributionFunction(0.0));
				
				UncertainParamters orderParameters = new UncertainParamters(staticContainer);

				customer = new StaticCustomer(elementParameters, structureElementParameters, orderParameters, storageManager, serviceTimeFunction);
			}

			customers.add(customer);
			logger.debug("Parameterized customer build {}.", customer.getVRPSimulationModelElementParameters().getId());

		}

		return customers;
	}

	private Network getNetwork(Instance instance) {

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
//				IWay way = new DefaultWay(vrpSimulationModelElementParameters, new DeterministicTimeFunction(0.0), node1, node2, new Euclidean2DDistanceFunction(), 100.0);
				IWay way = new DefaultWay(vrpSimulationModelElementParameters, new TravelTimeIsDistanceTimeFunction(), node1, node2, new Euclidean2DDistanceFunction(), 100.0);
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
