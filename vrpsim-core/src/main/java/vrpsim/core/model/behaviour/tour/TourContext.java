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
package vrpsim.core.model.behaviour.tour;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class TourContext {

	private static Logger logger = LoggerFactory.getLogger(TourContext.class);

	private final Double tourStartTime;

	private final IDriver driver;
	private final IVehicle vehicle;

	private Cost currentTourCost;
	private IActivity currentActivity;
	private IVRPSimulationModelNetworkElement currentPlace;

	private List<IVRPSimulationModelElement> elementsUpdated;
	private List<IVRPSimulationModelNetworkElement> placeHistory;

	public TourContext(Double tourStartTime, IVehicle vehicle, IDriver driver) {
		this.tourStartTime = tourStartTime;
		this.vehicle = vehicle;
		this.driver = driver;
		this.placeHistory = new ArrayList<>();
		this.elementsUpdated = new ArrayList<>();
		this.currentTourCost = new Cost();
		this.currentPlace = this.vehicle.getCurrentPlace();
	}

	public void reset() {
		this.vehicle.reset();
		this.driver.reset();
		this.placeHistory = new ArrayList<>();
		this.elementsUpdated = new ArrayList<>();
		this.currentTourCost = new Cost();
		this.currentPlace = this.vehicle.getCurrentPlace();
	}

	public Double getTourStartTime() {
		return tourStartTime;
	}

	public void addElementsUpdated(IVRPSimulationModelElement element) {
		this.elementsUpdated.add(element);
	}

	public IActivity getCurrentActivity() {
		return this.currentActivity;
	}

	public boolean isTourActive() {
		return this.currentActivity.getSuccessor() != null;
	}

	public void setCurrentActivity(IActivity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public void updateTourContextElementsAndCosts(IVRPSimulationModelNetworkElement currentPlace, Cost cost) {

		if (currentPlace != null) {
			this.placeHistory.add(this.vehicle.getCurrentPlace());
			this.currentPlace = currentPlace;
			this.vehicle.setCurrentPlace(currentPlace);
			this.driver.setCurrentPlace(currentPlace);
		}

		this.updateTourContextCosts(cost);
	}

	public void updateTourContextCosts(Cost cost) {
		if (cost != null) {
			this.vehicle.addCost(cost);
			this.driver.addCost(cost);
			this.currentTourCost = Cost.addCosts(this.currentTourCost, cost);
			logger.info("Current tour cost set to {}", currentTourCost);
		}
	}

	public IVehicle getVehicle() {
		return vehicle;
	}

	public IDriver getDriver() {
		return driver;
	}

	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return currentPlace;
	}

	/**
	 * Returns the elements updated and deletes them.
	 * 
	 * @return
	 */
	public List<IVRPSimulationModelElement> consumeElementsUpdated() {
		List<IVRPSimulationModelElement> result = new ArrayList<>(this.elementsUpdated);
		elementsUpdated.clear();
		return result;
	}

	public List<IVRPSimulationModelNetworkElement> getPlaceHistory() {
		return placeHistory;
	}

	public Cost getCurrentTourCost() {
		return currentTourCost;
	}

}
