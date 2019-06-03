package vrpsim.simulationmodel.dynamicbehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vrpsim.core.model.behaviour.BehaviourService.LoadOrUnloadAmountSpDummy;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.activities.impl.UnloadActivity;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.solution.ADynamicBehaviourProvider;
import vrpsim.core.model.solution.HandleOrderEventException;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.policies.impl.EuclideanNoWayDistanceIsTimeRouting;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

public class DynamicBehaviourProvider extends ADynamicBehaviourProvider {

	private final IDynamicBehaviourProviderHandler handler;

	public DynamicBehaviourProvider(IDynamicBehaviourProviderHandler handler) {
		this.handler = handler;
	}

	@Override
	public void handleNotTakenOrder(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock) throws HandleOrderEventException {

		logger.debug("Hanlde order event.");

		List<TourState> currentTourState = new ArrayList<>();
		long tourStateIdCounter = 0;
		Map<Long, ITour> tourStateIdTourMapping = new HashMap<>();
		List<ITour> activeTours = this.behaviourService.getActiveTours();
		if (activeTours == null || activeTours.isEmpty()) {
			Set<String> drivers = new HashSet<>();
			for (IVehicle vehicle : this.structureService.getVehicles()) {
				IDriver driver = getUnBussyDriver(drivers);
				currentTourState.add(new TourState(tourStateIdCounter++, null, vehicle, driver, new ArrayList<>()));
				drivers.add(driver.getVRPSimulationModelElementParameters().getId());
			}
		} else {
			Set<String> vehicles = new HashSet<>();
			Set<String> drivers = new HashSet<>();
			for (ITour tour : activeTours) {
				IVehicle vehicle = tour.getTourContext().getVehicle();
				String vehicleId = vehicle.getVRPSimulationModelElementParameters().getId();
				IDriver driver = tour.getTourContext().getDriver();
				String driverId = tour.getTourContext().getDriver().getVRPSimulationModelElementParameters().getId();
				List<LoadOrUnloadAmountSpDummy> customerStillToServe = this.behaviourService
						.getCustomersOnTour(tour.getTourContext().getCurrentActivity());
				long tourStateId = tourStateIdCounter++;
				tourStateIdTourMapping.put(tourStateId, tour);
				currentTourState.add(new TourState(tourStateId, tour.getTourContext().getCurrentActivity(), vehicle, driver,
						translate(customerStillToServe)));
				vehicles.add(vehicleId);
				drivers.add(driverId);
			}

			if (vehicles.size() < this.structureService.getVehicles().size()) {
				for (IVehicle vehicle : this.structureService.getVehicles()) {
					if (!vehicles.contains(vehicle.getVRPSimulationModelElementParameters().getId())) {
						IDriver driver = getUnBussyDriver(drivers);
						currentTourState.add(new TourState(tourStateIdCounter++, null, vehicle, driver, new ArrayList<>()));
						drivers.add(driver.getVRPSimulationModelElementParameters().getId());
					}
				}
			}

		}

		ICustomer newCustomer = (ICustomer) orderEvent.getOwner();
		int amount = -1 * orderEvent.getOrder().getAmount();
		StorableParameters sp = orderEvent.getOrder().getStorableParameters();

		try {
			this.handler.hanldeOrder(currentTourState, new TourActivity(newCustomer, sp, amount), simulationClock, structureService,
					networkService);
		} catch (DynamicHandlerException e) {
			logger.error("DynamicHandlerException was thrown: {}", e.getMessage());
			logger.error("Throw HandleOrderEventException.");
			throw new HandleOrderEventException("From DynamicHandlerException: " + e.getMessage(), e);
		}

		for (TourState tourState : currentTourState) {
			if (!tourState.getTourActivities().isEmpty()) {
				if (tourStateIdTourMapping.containsKey(tourState.getId())) {
					updateTour(tourStateIdTourMapping.get(tourState.getId()), tourState, simulationClock);
				} else {
					updateTour(null, tourState, simulationClock);
				}
			}
		}

	}

	private IDriver getUnBussyDriver(Set<String> bussyDrivers) {
		IDriver result = null;
		for (IDriver driver : this.structureService.getDrivers()) {
			String id = driver.getVRPSimulationModelElementParameters().getId();
			if (!bussyDrivers.contains(id)) {
				result = driver;
				break;
			}
		}
		return result;
	}

	private void updateTour(ITour tour, TourState tourState, IClock clock) {

		boolean registerTour = false;
		if (tour == null) {
			registerTour = true;
		}

		// if(tour != null) {
		// logger.debug("Number to unload in tour before update: {}",
		// tour.getNumberToUnload());
		// IActivity test = tour.getStartActivity();
		// while (test != null) {
		// if(test instanceof UnloadActivity) {
		// String loadingPartnerId =
		// ((UnloadActivity)test).getLoadingPartner().getVRPSimulationModelElementParameters().getId();
		// logger.debug("Loading Partner on updated tour: {}", loadingPartnerId);
		// }
		// test = test.getSuccessor();
		// }
		// }

		String currentLocationId = tourState.getVehicle().getCurrentPlace().getVRPSimulationModelElementParameters().getId();
		String firstLocationId = tourState.getTourActivities().get(0).getElement().getVRPSimulationModelStructureElementParameters()
				.getHome().getVRPSimulationModelElementParameters().getId();
		boolean transportActivity = !currentLocationId.equals(firstLocationId);

		if (tour != null) {
			// bugfix transport twice to same place
			if (tour.getTourContext().getCurrentActivity() instanceof TransportActivity) {
				logger.debug("Correct need for transportation.");
				String transportLocationID = ((TransportActivity) tour.getTourContext().getCurrentActivity()).getTransportTarget()
						.getVRPSimulationModelElementParameters().getId();
				transportActivity = transportLocationID != firstLocationId;
			}
		}

		IActivity workWith = null;
		if (tour != null) {
			workWith = tour.getTourContext().getCurrentActivity();
		}

		IActivity startActivity = null;
		for (TourActivity tourActivity : tourState.getTourActivities()) {
			if (transportActivity) {
				TransportActivity ta = this.behaviourService.get(tourActivity.getElement(), new EuclideanNoWayDistanceIsTimeRouting());
				if (workWith == null) {
					startActivity = ta;
				} else {
					workWith.setSuccessor(ta);
				}
				workWith = ta;
			}

			IActivity activity = this.behaviourService.get(tourActivity.getElement(), tourActivity.getStoreableParameters(),
					tourActivity.getAmount());
			if (workWith == null) {
				startActivity = activity;
			} else {
				workWith.setSuccessor(activity);
			}
			workWith = activity;
			transportActivity = true;
		}

		boolean addBackToDepot = !tourState.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()
				.getVRPSimulationModelElementParameters().getId()
				.endsWith(workWith.getLocation().getVRPSimulationModelElementParameters().getId());

		if (addBackToDepot) {
			TransportActivity ta = new TransportActivity(tourState.getVehicle().getVRPSimulationModelStructureElementParameters().getHome(),
					new EuclideanNoWayDistanceIsTimeRouting());
			workWith.setSuccessor(ta);
		}

		if (registerTour) {
			TourContext context = new TourContext(tourState.getStartTime(), tourState.getVehicle(), tourState.getDriver());
			tour = new Tour(context, startActivity);
			this.behaviourService.registerNewTour(tour, this.eventListService, clock);
		}

		// logger.debug("Number to unload in tour after update: {}",
		// tour.getNumberToUnload());
		// IActivity test = tour.getStartActivity();
		// while (test != null) {
		// logger.trace(test.toString());
		// test = test.getSuccessor();
		// }

	}

	private List<TourActivity> translate(List<LoadOrUnloadAmountSpDummy> toTranslate) {
		List<TourActivity> result = new ArrayList<>();
		for (LoadOrUnloadAmountSpDummy dummy : toTranslate) {
			result.add(new TourActivity(dummy.getElement(), dummy.getSp(), dummy.getAmount()));
		}
		return result;
	}

}
