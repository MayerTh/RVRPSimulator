package vrpsim.simulationmodel.initialbehaviour.simpleapi.api;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.BehaviourService;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.LoadActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.activities.impl.UnloadActivity;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.policies.impl.EuclideanNoWayDistanceIsTimeRouting;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.LocationAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

/**
 * @author Nikita
 *
 */
public class SimpleInitialBehaviorProvider implements IInitialBehaviourProvider {

	private final IInitialBehaviourProviderHandler handler;

	public SimpleInitialBehaviorProvider(IInitialBehaviourProviderHandler handler) {
		this.handler = handler;
	}

	@Override
	public Behaviour provideBehavior(NetworkService networkService, StructureService structureService) {

		List<IDriver> drivers = structureService.getDrivers();
		List<DriverAPI> driversAPI = new ArrayList<>();
		for (IDriver driver : drivers) {
			driversAPI.add(new DriverAPI(driver.getVRPSimulationModelElementParameters().getId()));
		}

		List<IVehicle> vehicles = structureService.getVehicles();
		List<VehicleAPI> vehiclesAPI = new ArrayList<>();
		for (IVehicle vehicle : vehicles) {
			double capacity = vehicle.getCanStoreManager()
					.getMaxCapacity(vehicle.getCanStoreManager().getAllCanStoreTypes().get(0));
			String vID = vehicle.getVRPSimulationModelElementParameters().getId();
			vehiclesAPI.add(new VehicleAPI(capacity, vID));
		}

		List<ICustomer> customers = structureService.getCustomers();
		List<CustomerAPI> customersAPI = new ArrayList<>();
		for (ICustomer customer : customers) {
			for (Order order : customer.getStaticOrdersBeforeEventGeneration()) {
				LocationAPI location = translateToLocationAPI(
						((INode) customer.getVRPSimulationModelStructureElementParameters().getHome()).getLocation());
				int load = order.getAmount();
				String cID = customer.getVRPSimulationModelElementParameters().getId();
				customersAPI.add(new CustomerAPI(load, location, cID));
			}

		}

		List<IDepot> depots = structureService.getDepots();
		List<DepotAPI> depotsAPI = new ArrayList<>();
		for (IDepot depot : depots) {
			LocationAPI location = translateToLocationAPI(
					((INode) depot.getVRPSimulationModelStructureElementParameters().getHome()).getLocation());
			int load = (int) depot.getCanStoreManager()
					.getMaxCapacity(depot.getCanStoreManager().getAllCanStoreTypes().get(0));
			String dID = depot.getVRPSimulationModelElementParameters().getId();
			depotsAPI.add(new DepotAPI(load, location, dID));
		}

		List<TourAPI> toursHandler = this.handler.handleOrder(vehiclesAPI, customersAPI, depotsAPI, driversAPI);
		
		if (toursValid(toursHandler) == false) {
			throw new Error("Keine valide Tour!");
		} else {

			List<ITour> toursBehaviour = new ArrayList<>();
			for (TourAPI tourAPI : toursHandler) {
				IVehicle vehicle = structureService.getVehicle(tourAPI.getVehicle().getId()); // zuordnung von
																								// VehicleAPI zu
																								// IVehicle
																								// über
																								// die id
				IDriver driver = structureService.getDriver(tourAPI.getDriver().getId());
				TourContext tourContext = new TourContext(0.0, vehicle, driver);

				IDepot startDepot = structureService.getDepot(tourAPI.getOrder().get(0).getId()); // zuordnen von
																									// DepotAPI zu
																									// IDepot
//				double capacity = vehicle.getCanStoreManager()
//						.getMaxCapacity(vehicle.getCanStoreManager().getAllCanStoreTypes().get(0)); // Kapazität beträgt in den meisten generierten Instanzen nur 1,7
				double capacity = 1000;
				LoadActivity tourStartActivity = new LoadActivity(structureService.getStorableparameters().get(0),
						(int) capacity, startDepot, false);

				ITour tour = new Tour(tourContext, tourStartActivity);
				toursBehaviour.add(tour);

				IActivity workWith = tourStartActivity;

				for (IJob jobAPI : tourAPI.getOrder().subList(1, tourAPI.getOrder().size() - 1)) { // Start-Depot wurde
																									// oben schon
																									// bearbeitet; End
																									// Depot kommt nach
																									// der Schleife

					// entsprechenden Customer oder Depot finden
					INode driveTo = null;
					if (jobAPI instanceof DepotAPI) {
						IDepot depot = structureService.getDepot(jobAPI.getId()); // Zuordnung von DepotAPI zu IDepot
						driveTo = ((INode) depot.getVRPSimulationModelStructureElementParameters().getHome());
					} else {
						ICustomer customer = structureService.getCustomer(jobAPI.getId());
						driveTo = ((INode) customer.getVRPSimulationModelStructureElementParameters().getHome());
					}
					// Drive to Customer or Depot
					TransportActivity transportActivity = new TransportActivity(driveTo,
							new EuclideanNoWayDistanceIsTimeRouting());
					workWith.setSuccessor(transportActivity);
					workWith = transportActivity;

					if (jobAPI instanceof DepotAPI) {
						// Load at Depot ...
						IDepot depot = structureService.getDepot(jobAPI.getId()); // Zuordnung von DepotAPI zu IDepot
						double freeCapacity = vehicle.getCanStoreManager()
								.getFreeCapacity(vehicle.getCanStoreManager().getAllCanStoreTypes().get(0));
						LoadActivity loadActivity = new LoadActivity(structureService.getStorableparameters().get(0),
								(int) freeCapacity, depot, true); // Vehicle bei Depot immer voll beladen
						workWith.setSuccessor(loadActivity);
						workWith = loadActivity;
					} else {
						// ...or unload at customer
						ICustomer customer = structureService.getCustomer(jobAPI.getId());
						int unload = customer.getStaticOrdersBeforeEventGeneration().get(0).getAmount();
						UnloadActivity unloadActivity = new UnloadActivity(
								structureService.getStorableparameters().get(0), unload, customer, true);
						workWith.setSuccessor(unloadActivity);
						workWith = unloadActivity;
					}
				}
				IJob jobAPI = tourAPI.getOrder().get((tourAPI.getOrder().size() - 1)); // letztes Depot; Endstation
				IDepot depot = structureService.getDepot(jobAPI.getId()); // Zuordnung von DepotAPI zu IDepot
				INode driveTo = (INode) depot.getVRPSimulationModelStructureElementParameters().getHome();
				TransportActivity transportActivity = new TransportActivity(driveTo,
						new EuclideanNoWayDistanceIsTimeRouting());
				workWith.setSuccessor(transportActivity);
			}
			Behaviour behaviour = new Behaviour(toursBehaviour);
//			BehaviourService bhService = new BehaviourService(behaviour);
//			bhService.reverse(behaviour.getTours().get(0), new EuclideanNoWayDistanceIsTimeRouting()) ;
			return behaviour;
		}
	}


	private boolean toursValid(List<TourAPI> tours) {
		for (TourAPI tour : tours) {
			if ((!(tour.getOrder().get(0) instanceof DepotAPI))
					|| (!(tour.getOrder().get(tour.getOrder().size() - 1) instanceof DepotAPI)))
				return false;
		}
		return true;
	}

	private LocationAPI translateToLocationAPI(Location location) {
		double coordX = location.getX();
		double coordY = location.getY();
		return new LocationAPI(coordX, coordY);
	}

}
