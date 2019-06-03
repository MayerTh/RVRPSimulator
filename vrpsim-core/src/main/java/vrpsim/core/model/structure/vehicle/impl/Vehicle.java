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
package vrpsim.core.model.structure.vehicle.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.impl.BreakdownEvent;
import vrpsim.core.model.events.impl.RepairedEvent;
import vrpsim.core.model.events.strategies.IBreakdownStrategy;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.storage.ICanStoreManager;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

/**
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Vehicle implements IVehicle {

	private static Logger logger = LoggerFactory.getLogger(Vehicle.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;

	private final double averageSpeed;
	private final Cost originalCosts;
	private Cost cost;
	private IVRPSimulationModelNetworkElement currentPlace;
	private final IVRPSimulationModelNetworkElement originalCurrentPlace;
	private boolean isBroken;

	private final IBreakdownStrategy breakdownStrategy;
	private final ICanStoreManager canStoreManager;

	private final List<IEventType> eventTypes;

	private IVRPSimulationBehaviourElementCanAllocate allocatedBy = null;

	public Vehicle(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final ICanStoreManager canStoreManager, final double averageSpeed, final Cost cost) {
		this(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, null, canStoreManager, averageSpeed,
				cost);
	}

	public Vehicle(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final IBreakdownStrategy breakdownStrategy, final ICanStoreManager canStoreManager, final double averageSpeed,
			final Cost cost) {

		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;

		this.averageSpeed = averageSpeed;
		this.cost = cost;
		this.originalCosts = cost;
		this.currentPlace = vrpSimulationModelStructureElementParameters.getHome();
		this.originalCurrentPlace = vrpSimulationModelStructureElementParameters.getHome();
		this.isBroken = false;

		this.breakdownStrategy = breakdownStrategy;
		this.canStoreManager = canStoreManager;

		this.eventTypes = new ArrayList<IEventType>();
		if (this.breakdownStrategy != null) {
			this.eventTypes.addAll(this.breakdownStrategy.getEventTypes());
		}
	}
	

	@Override
	public void reset() {
		this.cost = this.originalCosts;
		this.currentPlace = originalCurrentPlace;
		this.isBroken = false;
		this.allocatedBy = null;
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<>();
		if (this.breakdownStrategy != null) {
			BreakdownEvent breakDownEvent = this.breakdownStrategy.getBreakdown(this);
			events.add(breakDownEvent);
		}
		return events;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException {
		List<IEvent> events = new ArrayList<>();
		if (this.breakdownStrategy != null) {
			if (event instanceof BreakdownEvent) {
				this.isBroken = true;
				RepairedEvent repairedEvent = this.breakdownStrategy.getRepaired((BreakdownEvent) event, this);
				events.add(repairedEvent);
				logger.debug("BreakdownEvent processed, now isBroken = true.");
			} else if (event instanceof RepairedEvent) {
				this.isBroken = false;
				BreakdownEvent breakdownEvent = this.breakdownStrategy.getBreakdown(this);
				events.add(breakdownEvent);
				logger.debug("RepairedEvent processed, now isBroken = false.");
			}
		}
		return events;
	}

	@Override
	public double getAverageSpeed() {
		return this.averageSpeed;
	}

	@Override
	public void addCost(Cost cost) {
		this.cost = Cost.addCosts(this.cost, cost);
	}

	@Override
	public void setCurrentPlace(IVRPSimulationModelNetworkElement networkElement) {
		this.currentPlace = networkElement;
	}

	@Override
	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return this.currentPlace;
	}

	@Override
	public ICanStoreManager getCanStoreManager() {
		return this.canStoreManager;
	}

	@Override
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		this.allocatedBy = element;
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		this.allocatedBy = null;
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public boolean isAvailableForAllocation(IClock clock) {
		return this.allocatedBy == null;
	}

	@Override
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element) {
		return !isBroken && this.allocatedBy != null && this.allocatedBy.equals(element);
	}


}
