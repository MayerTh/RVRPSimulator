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
package vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliverService;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.LoadActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.activities.impl.UnloadActivity;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.policies.impl.EuclideanNoWayDistanceIsTimeRouting;

public class JspritSimpleSolutionTranslater {

	private static Logger logger = LoggerFactory.getLogger(JspritSimpleSolutionTranslater.class);

	public IInitialBehaviourProvider translateSolution(VehicleRoutingProblemSolution solution, StructureService structureService,
			NetworkService networkService) throws VRPArithmeticException {

		if (solution.getUnassignedJobs().size() > 0) {
			logger.error("There are unassigned jobs in the Jsprit solution.");
			throw new RuntimeException("There are unassigned jobs in the Jsprit solution.");
		}

		List<ITour> tours = new ArrayList<>();
		Set<String> drivers = new HashSet<>();
		Set<String> vehicles = new HashSet<>();

		int vrc = 1;
		for (VehicleRoute vehicleRoute : solution.getRoutes()) {

			IVehicle vehicle = null;
			for (IVehicle v : structureService.getVehicles()) {
				if (!vehicles.contains(v.getVRPSimulationModelElementParameters().getId())) {
					vehicle = v;
					vehicles.add(v.getVRPSimulationModelElementParameters().getId());
					break;
				}
			}
			
			IDriver driver = null;
			for(IDriver d : structureService.getDrivers()) {
				if (!drivers.contains(d.getVRPSimulationModelElementParameters().getId())) {
					driver = d;
					drivers.add(d.getVRPSimulationModelElementParameters().getId());
					break;
				}
			}
		
//		IVehicle vehicle = structureService.getVehicle(vehicleRoute.getVehicle().getId());
//		IDriver driver = structureService.getDriver(vehicleRoute.getDriver().getId());

		if (driver == null) {
			driver = structureService.getDrivers().get(0);
		}
		double activityStartTime = 0D;

		// int capacityNeeded = this.calculateNeeded(vehicleRoute.getActivities());
		
		// TODO Test
		if(vehicle == null) {
			logger.error("Jsprit planning error, Number jsprit routes is {}, number available vehicles is {}", solution.getRoutes().size(), structureService.getVehicles().size());
			throw new RuntimeException("Jsprit planning error, number jsprit routes is "+solution.getRoutes().size()+", number available vehicles is "+structureService.getVehicles().size()+".");
		}
		
		int capacityNeeded = new Double(
				vehicle.getCanStoreManager().getMaxCapacity(vehicle.getCanStoreManager().getAllCanStoreTypes().get(0))).intValue();
		if (capacityNeeded > 1000) {
			int setTo = 1000;
			logger.warn("Note that only {} units are loaded at the depot, due to loading procedure (capacity of the vehicle is {})", setTo,
					capacityNeeded);
			capacityNeeded = setTo;
		}
		logger.debug("VehicleRoute {}, capacity needed for the rout is {}.", vrc++, capacityNeeded);
		LoadActivity tourStartActivity = new LoadActivity(structureService.getStorableparameters().get(0), capacityNeeded,
				structureService.getDepots().get(0), false);

		logger.debug("Build TourContext with vehicle {}, driver {} and activityStartTime {}", vehicle, driver, activityStartTime);
		TourContext tourContext = new TourContext(activityStartTime, vehicle, driver);
		ITour tour = new Tour(tourContext, tourStartActivity);
		tours.add(tour);

		int tac = 1;
		IActivity workWith = tourStartActivity;
		for (TourActivity tourActivity : vehicleRoute.getActivities()) {

			logger.debug("TourActivity {}.", tac++);

			if (tourActivity instanceof DeliverService) {

				logger.debug("based on TourActivity at location: {} with name {}", tourActivity.getLocation(), tourActivity.getName());

				// Drive to customer
				INode driveTo = networkService.getNodeByLoaction(this.transformTo(tourActivity.getLocation()));
				TransportActivity transportActivity = new TransportActivity(driveTo, new EuclideanNoWayDistanceIsTimeRouting());
				logger.debug("Build: Drive to node {} at {}.", driveTo.getVRPSimulationModelElementParameters().getId(),
						driveTo.getLocation());
				workWith.setSuccessor(transportActivity);
				workWith = transportActivity;

				// Unload at customer
				logger.debug("Delivery to node {}.", driveTo.getVRPSimulationModelElementParameters().getId());

				ICustomer unloadBy = structureService.getCustomersByNodeId(driveTo).get(0);
				UnloadActivity unload = new UnloadActivity(structureService.getStorableparameters().get(0),
						tourActivity.getSize().getNuOfDimensions(), unloadBy, true);
				logger.debug("Build: Unlaod {} at {}", tourActivity.getSize().getNuOfDimensions(),
						unloadBy.getVRPSimulationModelElementParameters().getId());
				workWith.setSuccessor(unload);
				workWith = unload;

			}

		}

		// Drive back to depot
		INode driveTo = (INode) structureService.getDepots().get(0).getVRPSimulationModelStructureElementParameters().getHome();
		TransportActivity transportActivity = new TransportActivity(driveTo, new EuclideanNoWayDistanceIsTimeRouting());
		logger.debug("Build: Drive back to depot node {} at {}.", driveTo.getVRPSimulationModelElementParameters().getId(),
				driveTo.getLocation());
		workWith.setSuccessor(transportActivity);

	}

	Behaviour behaviour = new Behaviour(tours);
	IInitialBehaviourProvider provider = new IInitialBehaviourProvider() {

			@Override
			public Behaviour provideBehavior(NetworkService networkService, StructureService structureService) {
				return behaviour;
			}

		};

		return provider;
	}

	private Location transformTo(com.graphhopper.jsprit.core.problem.Location location) {
		return new Location(location.getCoordinate().getX(), location.getCoordinate().getY(), null);
	}

}
