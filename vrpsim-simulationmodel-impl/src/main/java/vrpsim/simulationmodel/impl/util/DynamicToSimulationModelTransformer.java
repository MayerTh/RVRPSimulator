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
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.VRPSimulationModelParameters;
import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.events.strategies.IOrderStrategy;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.Network;
import vrpsim.core.model.network.impl.Node;
import vrpsim.core.model.network.impl.Way;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.impl.Customer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.depot.impl.SourceDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.driver.impl.Driver;
import vrpsim.core.model.structure.storage.ICanStore;
import vrpsim.core.model.structure.storage.ICanStoreManager;
import vrpsim.core.model.structure.storage.impl.CanStoreManager;
import vrpsim.core.model.structure.storage.impl.CanStoreParameters;
import vrpsim.core.model.structure.storage.impl.Compartment;
import vrpsim.core.model.structure.storage.impl.StorableGenerator;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.structure.vehicle.impl.Vehicle;
import vrpsim.core.model.util.policies.impl.LIFOLoadingPolicy;
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
		Structure structure = new Structure(this.transformationConfiguration.getSTORABLEPARAMETERS(), depots, customers, vehicles, drivers,
				new ArrayList<>());

		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters(
				new Long(this.dynamicModel.getId()).toString(), "Thomas Mayer");
		logger.debug("Simultaion model generated");

		return new VRPSimulationModel(vrpSimulationModelParameters, structure, network);
	}

	private List<IDriver> getDrivers(Instance instance, List<IVehicle> vehicles) {

		List<IDriver> drivers = new ArrayList<IDriver>();
		for (int i = 0; i < vehicles.size(); i++) {
			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DRIVER-" + i, 0);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
					vehicles.get(i).getVRPSimulationModelStructureElementParameters().getHome());

			IDriver driver = new Driver(elementParameters, structureElementParameters, 7d, new Cost());

			drivers.add(driver);
			logger.debug("Driver {} created at location {}", driver.getVRPSimulationModelElementParameters().getId(), vehicles.get(i)
					.getVRPSimulationModelStructureElementParameters().getHome().getVRPSimulationModelElementParameters().getId());
		}
		return drivers;

	}

	private List<IVehicle> getVehicles(Instance instance, List<INode> nodes) {

		List<IVehicle> vehicles = new ArrayList<IVehicle>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();
		Double vehicleCapacity = instance.getFleet().getVehicleProfile().get(0).getCapacity();

		for (int i = 0; i < transformationConfiguration.getNUMBER_OF_VEHICLES(); i++) {
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
					getNode(nodeId, nodes));

			StorableGenerator storableGenerator = new StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS());
			CanStoreParameters compartmentParameters = new CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
					instance.getFleet().getVehicleProfile().get(0).getCapacity().intValue(), new LIFOLoadingPolicy(storableGenerator));
			ICanStore compartment = new Compartment(compartmentParameters);
			ICanStoreManager canStoreManager = new CanStoreManager(compartment);

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("VEHICLE-" + i, 0);
			IVehicle vehicle = new Vehicle(elementParameters, structureElementParameters, canStoreManager, 80d, new Cost());
			vehicles.add(vehicle);
			logger.debug("Vehicle {} created with capacity {} and location {}", vehicle.getVRPSimulationModelElementParameters().getId(),
					vehicleCapacity, nodeId);
		}

		return vehicles;
	}

	private List<IDepot> getDepots(Instance instance, List<INode> nodes) {

		List<IDepot> depots = new ArrayList<IDepot>();
		String nodeId = instance.getFleet().getVehicleProfile().get(0).getArrivalNode().get(0).toString();

		VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("DEPOT", 0);
		VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
				getNode(nodeId, nodes));

		// No arrival.
		// UncertainParamters arrivalParameters = new UncertainParamters();
		// StorableGenerator storableGenerator = new
		// StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS());
		//
		// CanStoreParameters compartmentParameters = new
		// CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
		// new Capacity(this.transformationConfiguration.getCAPACITY_UNIT(),
		// this.transformationConfiguration.getMAX_CAPACITY_IN_DEPOT_STORAGE()),
		// new LIFOLoadingPolicy(), storableGenerator);
		// ICanStore compartment = new Compartment(compartmentParameters);
		// DefaultStorageManager storageManager = new DefaultStorageManager(new
		// DefaultStorage(compartment));
		//
		// IDepot depot = new SourceDepot(elementParameters, structureElementParameters,
		// arrivalParameters, storageManager,
		// new DeterministicTimeFunction(0.0));

		IDepot depot = new SourceDepot(elementParameters, structureElementParameters,
				this.transformationConfiguration.getSTORABLEPARAMETERS());

		logger.debug("Depot {} generated at location {}.", elementParameters.getId(),
				structureElementParameters.getHome().getVRPSimulationModelElementParameters().getId());

		depots.add(depot);
		return depots;
	}

	private List<ICustomer> getCustomers(DynamicVRPREPModel dynamicModel, List<INode> nodes) {

		int orderIdCounter = 0;
		List<Request> allRequests = dynamicModel.getVRPREPInstance().getRequests().getRequest();
		List<ICustomer> customers = new ArrayList<ICustomer>();
		for (Request request : allRequests) {

			String requestIdstr = request.getId().toString();
			int amount = request.getQuantity().intValue();
			String nodeId = request.getNode().toString();
			boolean isDynamicRequest = dynamicModel.getDynamicRequestInformation().containsKey(request.getId());

			logger.debug("Request (isDynamicRequest={}) found with amount {} on node with id {}. ", isDynamicRequest, amount, nodeId);

			VRPSimulationModelElementParameters elementParameters = new VRPSimulationModelElementParameters("CUSTOMER-" + requestIdstr, 0);
			VRPSimulationModelStructureElementParameters structureElementParameters = new VRPSimulationModelStructureElementParameters(
					getNode(nodeId, nodes));
			
			double initialStartTime;
			if(isDynamicRequest) {
				initialStartTime = dynamicModel.getDynamicRequestInformation().get(request.getId()).getArrivalTime();
			} else {
				initialStartTime = 0;
			}
			
			
			int orderId = orderIdCounter++;
			IOrderStrategy orderStrategy = new IOrderStrategy() {

				IEventType eventType = null;
				boolean dynamicOrderIsDelivered = false;
				double startTime = initialStartTime;
				final double originalStartTime = initialStartTime;
				
				@Override
				public void reset() {
					this.eventType = null;
					this.dynamicOrderIsDelivered = false;
					this.startTime = originalStartTime;
				}
				
				private void setEventType() {
					eventType = new IEventType() {
						@Override
						public String getType() {
							return IEventType.ORDER_EVENT;
						}
					};
				}
				
				@Override
				public List<IEventType> getEventTypes() {
					if(eventType  == null) {
						setEventType();
					}
					
					List<IEventType> eventTypes = new ArrayList<>();
					eventTypes.add(eventType);
					return eventTypes;
				}

				@Override
				public List<Order> getStaticOrders(IVRPSimulationModelStructureElementWithStorage withStorage) {
					List<Order> result = new ArrayList<>();
					if (!isDynamicRequest) {
						String id = withStorage.getVRPSimulationModelElementParameters().getId();
						Order order = new Order(orderId + "_" + id, null, null, transformationConfiguration.getSTORABLEPARAMETERS(),
								amount, withStorage);
						result.add(order);
					} 
					return result;
				}

				@Override
				public OrderEvent getNextDynamicOrder(IVRPSimulationModelStructureElementWithStorage withStorage) {
					if(eventType  == null) {
						setEventType();
					}
					
					OrderEvent result = null;
					if(isDynamicRequest && !dynamicOrderIsDelivered) {
						String id = withStorage.getVRPSimulationModelElementParameters().getId();
						Order order = new Order(orderId + "_" + id, null, null, transformationConfiguration.getSTORABLEPARAMETERS(),
								amount, withStorage);
						double st = startTime;
						result = new OrderEvent((IEventOwner)withStorage, eventType, 0, st, order);
						dynamicOrderIsDelivered = true;
					}
					return result;
				}

				@Override
				public boolean hasDynamicEvents() {
					return isDynamicRequest;
				}

				@Override
				public void setStartTime(double startTime) {
					this.startTime = startTime;
				}

				@Override
				public double getStartTime() {
					return this.startTime;
				}
			};

			CanStoreParameters compartmentParameters = new CanStoreParameters(this.transformationConfiguration.getCANSTORETYPE(),
					this.transformationConfiguration.getMAX_CAPACITY_IN_CUSTOMER_STORAGE().intValue(),
					new LIFOLoadingPolicy(new StorableGenerator(this.transformationConfiguration.getSTORABLEPARAMETERS())));
			ICanStore compartment = new Compartment(compartmentParameters);
			ICanStoreManager canStoreManager = new CanStoreManager(compartment);
			ICustomer customer = new Customer(elementParameters, structureElementParameters, canStoreManager, orderStrategy);

			

			customers.add(customer);
			logger.debug("Parameterized customer build {}.", customer.getVRPSimulationModelElementParameters().getId());

		}

		return customers;
	}

	private Network getNetwork(Instance instance) {

		List<INode> nodes = new ArrayList<INode>();
		for (org.vrprep.model.instance.Instance.Network.Nodes.Node node : instance.getNetwork().getNodes().getNode()) {
			logger.debug("Node {} with coords: {}, {}, {}.", node.getId(), node.getCx(), node.getCy(), node.getCz());
			Location location = new Location(node.getCx(), node.getCy(), node.getCz());
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"NODE-" + node.getId().toString(), 0);
			INode simNode = new Node(vrpSimulationModelElementParameters, location);
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

				String id = node1.getVRPSimulationModelElementParameters().getId() + "-"
						+ node2.getVRPSimulationModelElementParameters().getId();
				VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(id, 0);
				// IWay way = new DefaultWay(vrpSimulationModelElementParameters, new
				// DeterministicTimeFunction(0.0), node1, node2, new
				// Euclidean2DDistanceFunction(), 100.0);
				IWay way = new Way(vrpSimulationModelElementParameters, node2, 100d);
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
