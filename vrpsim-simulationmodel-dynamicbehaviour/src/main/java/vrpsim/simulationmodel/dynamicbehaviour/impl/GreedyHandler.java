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
package vrpsim.simulationmodel.dynamicbehaviour.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.IDynamicBehaviourProviderHandler;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

/**
 * Insertion in an existing Tour.
 * 
 * @author mayert
 */
public class GreedyHandler implements IDynamicBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(GreedyHandler.class);

	private double getLenght(Location start, List<Location> sortedLocations, Location end) {
		double sum = 0.0;
		if (!sortedLocations.isEmpty()) {
			sum += getLenght(start, sortedLocations.get(0));
			for (int i = 0; i < sortedLocations.size() - 1; i++) {
				sum += getLenght(sortedLocations.get(i), sortedLocations.get(i + 1));
			}
			sum += getLenght(sortedLocations.get(sortedLocations.size() - 1), end);
		} else {
			sum = getLenght(start, end);
		}
		return sum;
	}

	private double getLenght(Location location1, Location location2) {
		double a = Math.abs(location1.getX() - location2.getX());
		double b = Math.abs(location1.getY() - location2.getY());
		double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		return c;
	}

	@Override
	public void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) {

		if (allEmpty(currentTourStates)) {
			currentTourStates.get(0).getTourActivities().add(newOrder);
		} else {

			Map<TourState, Double> costs = new HashMap<>();
			List<List<Location>> toursLocations = new ArrayList<>();
			for (int i = 0; i < currentTourStates.size(); i++) {
				List<Location> locations = new ArrayList<>();
				toursLocations.add(locations);
				for (int j = 0; j < currentTourStates.get(i).getTourActivities().size(); j++) {
					locations.add(((INode) currentTourStates.get(i).getTourActivities().get(j).getElement()
							.getVRPSimulationModelStructureElementParameters().getHome()).getLocation());
				}

				Location vehicleLocation = ((INode) currentTourStates.get(i).getVehicle().getCurrentPlace()).getLocation();
				Location depotLocation = ((INode) currentTourStates.get(i).getVehicle().getVRPSimulationModelStructureElementParameters()
						.getHome()).getLocation();
				costs.put(currentTourStates.get(i), getLenght(vehicleLocation, locations, depotLocation));

			}

			int bestTourIndex = 0;
			int bestPlanInTourindex = 0;
			double min = Double.MAX_VALUE;
			Location newCustomerLocation = ((INode) newOrder.getElement().getVRPSimulationModelStructureElementParameters().getHome())
					.getLocation();

			for (int i = 0; i < toursLocations.size(); i++) {

				Location vehicleLocation = ((INode) currentTourStates.get(i).getVehicle().getCurrentPlace()).getLocation();
				Location depotLocation = ((INode) currentTourStates.get(i).getVehicle().getVRPSimulationModelStructureElementParameters()
						.getHome()).getLocation();

				for (int j = currentTourStates.get(i).isAddAtFirstPlaceAllowed() ? 0 : 1; j <= toursLocations.get(i).size(); j++) {
					List<Location> workWith = new ArrayList<>(toursLocations.get(i));
					workWith.add(j, newCustomerLocation);
					double value = getLenght(vehicleLocation, workWith, depotLocation);
					if (workWith.size() == 1) {
						value = 2 * value;
					}
					double otherCost = getLenghtExcept(costs, currentTourStates.get(i));
					value += otherCost;
					logger.trace("Add at position {} from tour {} costs {}, other costs {}", j, i, value, otherCost);
					if (value < min) {
						min = value;
						bestTourIndex = i;
						bestPlanInTourindex = j;
					}
				}
			}

			TourState tourState = currentTourStates.get(bestTourIndex);
			if (tourState.getTourActivities().isEmpty()) {
				IDepot depot = structureService
						.getDepotsByNode((INode) tourState.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).get(0);
				tourState.getTourActivities().add(new TourActivity(depot, newOrder.getStoreableParameters(), 100));
				tourState.getTourActivities().add(newOrder);
			} else {
				currentTourStates.get(bestTourIndex).getTourActivities().add(bestPlanInTourindex, newOrder);
			}

		}

	}

	private double getLenghtExcept(Map<TourState, Double> costs, TourState tourState) {
		double result = 0;
		for (TourState ts : costs.keySet()) {
			if (!ts.equals(tourState)) {
				result += costs.get(ts);
			}
		}
		return result;
	}

	private boolean allEmpty(List<TourState> currentTourStates) {
		boolean allEmpty = true;
		for (TourState tourState : currentTourStates) {
			if (!tourState.getTourActivities().isEmpty()) {
				allEmpty = false;
				break;
			}
		}
		return allEmpty;
	}

}
