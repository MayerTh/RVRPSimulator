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
package vrpsim.simulationmodel.dynamicbehaviour.generator.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.LoadActivity;
import vrpsim.core.model.behaviour.activities.TransportActivity;
import vrpsim.core.model.behaviour.activities.UnloadActivity;
import vrpsim.core.model.behaviour.activities.util.LoadUnloadJob;
import vrpsim.core.model.behaviour.activities.util.TransportJob;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.solution.AbstractOrderManager;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;
import vrpsim.r.util.api.IRExporterAPI;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.util.CannotCreateRModelException;
import vrpsim.r.util.api.util.RModelTransformator;
import vrpsim.r.util.impl.RExporterImpl;

public class DynamicBehaviourProvider extends AbstractOrderManager {

	private final String statisticsOutputFolder;
	private final boolean createStatistics;
	ICustomersStillToServeSorter sorter = null;

	public DynamicBehaviourProvider(String statisticsOutputFolder, boolean createStatistics, ICustomersStillToServeSorter sorter) {
		this.statisticsOutputFolder = statisticsOutputFolder;
		this.createStatistics = createStatistics;
		this.sorter = sorter;
	}

	@Override
	public void handleNotTakenOrder(Order order) {
		logger.error("handleNotTakenOrder(Order order) is not support.");
		throw new RuntimeException("handleNotTakenOrder(Order order) is not support.");
	}

	@Override
	public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver) {
		logger.error("handleNotTakenOrder(Order order) is not support.");
		throw new RuntimeException("handleNotTakenOrder(Order order) is not support.");
	}

	@Override
	public void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock) {

		int amountToUnload = orderEvent.getOrder().getAmount();
		ICustomer unloadAt = (ICustomer) orderEvent.getOrder().getOwner();
		INode transportTo = (INode) unloadAt.getVRPSimulationModelStructureElementParameters().getHome();
		StorableParameters sp = orderEvent.getOrder().getStorableParameters();

		int numberOfActiveTours = this.behaviourService.getActiveTours().size();
		logger.info("JspritDynamicBehaviourProvider is processing order event for {} at {}. Active tours count is {}", unloadAt.getVRPSimulationModelElementParameters().getId(),
				transportTo.getVRPSimulationModelElementParameters().getId(), numberOfActiveTours);

		try {
			if (numberOfActiveTours > 0) {
				// Adapt existing tour.
				adaptExistingTour(unloadAt, sp, amountToUnload, simulationClock.getCurrentSimulationTime());
				logger.debug("Existing tour adapted.");
			} else {
				// Create a new Tour and initialize the Tour.
				createNewTour(unloadAt, transportTo, sp, amountToUnload, simulationClock);
				logger.debug("New tour created.");

				// logger.error("No active ITour to adapt. Not
				// yetimplemented.");
				// throw new RuntimeException("No active ITour to adapt. Not
				// yetimplemented.");
			}
		} catch (VRPArithmeticException e) {
			e.printStackTrace();
			logger.error("Not able to hanlde dynmic order event.");
			throw new RuntimeException("Not able to hanlde dynmic order event.");
		}

	}

	private void createNewTour(ICustomer unloadAt, INode transportTo, StorableParameters sp, int amountToUnload, IClock clock) throws VRPArithmeticException {

		IVehicle vehicle = this.structureService.getVehicles().get(0);
		IDriver driver = this.structureService.getDrivers().get(0);

		ITime activityTimeTillStart = eventListService.getNextOccurenceByType(IEventType.ACTIVITY_EVENT, clock);
		if (activityTimeTillStart == null) {
			activityTimeTillStart = new Clock.Time(0.0);// clock.getCurrentSimulationTime();
		}
		logger.debug("Time till activity from new tour gets activated: {}.", activityTimeTillStart.getValue());

		int capacityNeeded = vehicle.getFreeCapacity(vehicle.getAllCanStoreTypes().get(0)).getValue().intValue();
		if (capacityNeeded > 1000) {
			int setTo = 1000;
			logger.warn("Note that only {} units are loaded at the depot, due to loading procedure (capacity of the vehicle is {})", setTo, capacityNeeded);
			capacityNeeded = setTo;
		}

		// Load stuff at the depot.
		LoadActivity tourStartActivity = new LoadActivity(new LoadUnloadJob(sp, capacityNeeded, structureService.getDepots().get(0)));

		logger.debug("Build TourContext with vehicle {}, driver {} and activityStartTime {}", vehicle, driver, activityTimeTillStart);
		TourContext tourContext = new TourContext(activityTimeTillStart, vehicle, driver);

		// Drive to customer
		TransportActivity transportActivity = new TransportActivity(new TransportJob(transportTo));
		logger.debug("Build: Drive to node {} at {}.", transportTo.getVRPSimulationModelElementParameters().getId(), transportTo.getLocation());
		tourStartActivity.setSuccessor(transportActivity);

		// Unload at customer
		UnloadActivity unload = new UnloadActivity(new LoadUnloadJob(structureService.getStorableparameters().get(0), amountToUnload, unloadAt));
		logger.debug("Build: Unlaod {} at {}", amountToUnload, unloadAt.getVRPSimulationModelElementParameters().getId());
		transportActivity.setSuccessor(unload);

		// Drive back to depot
		INode driveTo = (INode) structureService.getDepots().get(0).getVRPSimulationModelStructureElementParameters().getHome();
		TransportActivity transportActivityToDepot = new TransportActivity(new TransportJob(driveTo));
		logger.debug("Build: Drive back to depot node {} at {}.", driveTo.getVRPSimulationModelElementParameters().getId(), driveTo.getLocation());
		unload.setSuccessor(transportActivityToDepot);

		ITour tour = new Tour(tourContext, tourStartActivity);

		if (this.createStatistics) {
			IRExporterAPI exporter = new RExporterImpl();
			try {
				exporter.export(this.statisticsOutputFolder,
						new RConfig(clock.getCurrentSimulationTime().getValue() + "-new-tour-01.NewPlanned", clock.getCurrentSimulationTime().getValue() + " New Planned", "", ""),
						RModelTransformator.transformStartActivityTo(tour.getStartActivity(), tour.getTourContext()));
			} catch (IOException e) {
				logger.error("Can not create R model. {}", e.getMessage());
			}
		}

		this.behaviourService.registerNewTour(tour, this.eventListService, clock);
	}

	private void adaptExistingTour(ICustomer newUnloadAt, StorableParameters sp, int amountToUnload, ITime simulationTime) throws VRPArithmeticException {

		ITour tourToAdapt = this.behaviourService.getActiveTours().get(0);

		if (this.createStatistics) {
			IRExporterAPI exporter = new RExporterImpl();
			try {
				exporter.export(this.statisticsOutputFolder,
						new RConfig(simulationTime.getValue() + "-active-tour-01.CurrentState", simulationTime.getValue() + " Current State", "", ""),
						RModelTransformator.transformTourContextTo(tourToAdapt.getTourContext(), tourToAdapt.getStartActivity()));
			} catch (IOException | CannotCreateRModelException e) {
				logger.error("Can not create R model. {}", e.getMessage());
			}

			try {
				exporter.export(this.statisticsOutputFolder,
						new RConfig(simulationTime.getValue() + "-active-tour-02.CurrentPlanned", simulationTime.getValue() + " Current Planned", "", ""),
						RModelTransformator.transformStartActivityTo(tourToAdapt.getStartActivity(), tourToAdapt.getTourContext()));
			} catch (IOException e) {
				logger.error("Can not create R model. {}", e.getMessage());
			}
		}

		IVehicle tourVehicle = (IVehicle) tourToAdapt.getTourContext().getVehicle();
		String activeActivityClass = tourToAdapt.getTourContext().getCurrentActivity().getClass().getSimpleName();
		logger.debug("Active activity in ITour is {}", activeActivityClass);

		// This guy get a new successor (all the time from type
		// UnloadActivity) and is the start location for the new optimization
		// via jsprit.

		IActivity firstPossibeToAddNewActivityAsSuccessor = getFirstPossibeToAddNewActivityAsSuccessor(tourToAdapt.getTourContext().getCurrentActivity());
		List<CustomersStillToServe> planned = this.getAllFollowingUnloads(firstPossibeToAddNewActivityAsSuccessor);
		logger.debug("Customers already planned: {}", planned);
		CustomersStillToServe newCustomer = new CustomersStillToServe(newUnloadAt, sp, amountToUnload);

		String instanceName = "Reroute";
		Location vehicleLocation = ((INode) ((LoadUnloadJob) firstPossibeToAddNewActivityAsSuccessor.getJob()).getLoadingPartner().getVRPSimulationModelStructureElementParameters().getHome())
				.getLocation();
		Location depot = ((INode) tourVehicle.getVRPSimulationModelStructureElementParameters().getHome()).getLocation();
		List<CustomersStillToServe> newOrder = sorter.sort(planned, newCustomer, tourVehicle, vehicleLocation, depot, instanceName, this.statisticsOutputFolder, this.createStatistics);
		buildActivityChain(firstPossibeToAddNewActivityAsSuccessor, newOrder, (INode) tourVehicle.getVRPSimulationModelStructureElementParameters().getHome());

		if (this.createStatistics) {
			IRExporterAPI exporter = new RExporterImpl();
			try {
				exporter.export(this.statisticsOutputFolder,
						new RConfig(simulationTime.getValue() + "-active-tour-03.NewPlanned", simulationTime.getValue() + " New Planned", "", ""),
						RModelTransformator.transformStartActivityTo(tourToAdapt.getStartActivity(), tourToAdapt.getTourContext()));
			} catch (IOException e) {
				logger.error("Can not create R model. {}", e.getMessage());
			}
		}

	}

	private void buildActivityChain(IActivity firstPossibeToAddNewActivityAsSuccessor, List<CustomersStillToServe> newOrder, INode depotNode) {

		IActivity workWith = firstPossibeToAddNewActivityAsSuccessor;
		for (CustomersStillToServe sts : newOrder) {
			IActivity transport = new TransportActivity(new TransportJob(sts.getCustomer().getVRPSimulationModelStructureElementParameters().getHome()));
			IActivity unload = new UnloadActivity(new LoadUnloadJob(sts.getStoreableParameters(), sts.getAmount(), sts.getCustomer()));
			logger.debug("buildActivityChain: Serve customer {}", sts.getCustomer().getVRPSimulationModelElementParameters().getId());
			transport.setSuccessor(unload);
			workWith.setSuccessor(transport);
			workWith = unload;
		}

		// Back to the depot.
		workWith.setSuccessor(new TransportActivity(new TransportJob(depotNode)));

	}

	private List<CustomersStillToServe> getAllFollowingUnloads(IActivity firstPossibeToAddNewActivityAsSuccessor) {
		List<CustomersStillToServe> result = new ArrayList<>();

		IActivity act = null;
		if (firstPossibeToAddNewActivityAsSuccessor instanceof UnloadActivity) {
			act = firstPossibeToAddNewActivityAsSuccessor.getSuccessor();
		} else {
			act = firstPossibeToAddNewActivityAsSuccessor;
		}

		while (act != null) {
			if (act instanceof UnloadActivity) {
				logger.debug("Following Activity added {} with loading partner id {}", act,
						((LoadUnloadJob) act.getJob()).getLoadingPartner().getVRPSimulationModelElementParameters().getId());
				ICustomer customer = (ICustomer) ((LoadUnloadJob) act.getJob()).getLoadingPartner();
				result.add(new CustomersStillToServe(customer, ((LoadUnloadJob) act.getJob()).getStoreableParameters(), ((LoadUnloadJob) act.getJob()).getNumber()));
			}
			act = act.getSuccessor();
		}
		return result;
	}

	private IActivity getFirstPossibeToAddNewActivityAsSuccessor(IActivity currentActivity) {
		logger.debug("Find first possibe to add new activity as successor (first after activte activity which is from type UnloadActivity).");
		IActivity start = currentActivity;
		while (!(start instanceof UnloadActivity)) {
			start = start.getSuccessor();
			if (start == null) {
				break;
			}
		}

		if (start == null) {
			logger.warn("Can not find an UnloadActivity, this is only possible if the vehicle is on the way to the depot.");
			start = currentActivity;
			while (!(start instanceof TransportActivity)) {
				start = start.getSuccessor();
				if (start == null) {
					break;
				}
			}
		}

		if (start == null) {
			logger.error("Can not find an UnloadActivity and also no TransportActivity after active activity.");
			throw new RuntimeException("Can not find an UnloadActivity and also no TransportActivity after active activity.");
		}

		logger.debug("First possible to add new activity as successor found ({}).", start);
		return start;
	}

}
