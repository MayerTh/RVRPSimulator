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
package vrpsim.core.model.structure.driver.impl;

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
import vrpsim.core.model.events.impl.EndWorkEvent;
import vrpsim.core.model.events.impl.StartWorkEvent;
import vrpsim.core.model.events.strategies.IDrivingHoursStrategy;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

/**
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Driver implements IDriver {

	private static Logger logger = LoggerFactory.getLogger(Driver.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;

	private final double averageSpeed;
	private IVRPSimulationModelNetworkElement currentPlace;
	private final IVRPSimulationModelNetworkElement orignialCurrentPlace;
	private boolean isOutOfWork;
	private Cost cost;
	private final Cost originalCosts;

	final IDrivingHoursStrategy drivingHoursStrategy;

	private final List<IEventType> eventTypes;
	private IVRPSimulationBehaviourElementCanAllocate isAllocatedBy;

	public Driver(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters, final double averageSpeed,
			final Cost cost) {
		this(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, null, averageSpeed, cost);
	}

	public Driver(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final IDrivingHoursStrategy drivingHoursStrategy, final double averageSpeed, final Cost cost) {

		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;

		this.averageSpeed = averageSpeed;
		this.cost = cost;
		this.originalCosts = cost;
		this.currentPlace = vrpSimulationModelStructureElementParameters.getHome();
		this.orignialCurrentPlace = vrpSimulationModelStructureElementParameters.getHome();
		this.isOutOfWork = false;

		this.drivingHoursStrategy = drivingHoursStrategy;

		this.eventTypes = new ArrayList<IEventType>();
		if (drivingHoursStrategy != null) {
			this.eventTypes.addAll(this.drivingHoursStrategy.getEventTypes());
		}
	}
	
	@Override
	public void reset() {
		this.cost = this.originalCosts;
		this.currentPlace = this.orignialCurrentPlace;
		this.isOutOfWork = false;
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<>();
		if (drivingHoursStrategy != null) {
			events.add(this.drivingHoursStrategy.getEnd(this));
		}
		return events;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException {
		List<IEvent> events = new ArrayList<>();
		if (this.drivingHoursStrategy != null) {
			if (event instanceof EndWorkEvent) {
				this.isOutOfWork = true;
				StartWorkEvent startWorkEvent = this.drivingHoursStrategy.getStart(this);
				events.add(startWorkEvent);
				logger.debug("EndWorkEvent processed, now isOutOfWork = true.");
			} else if (event instanceof StartWorkEvent) {
				this.isOutOfWork = false;
				EndWorkEvent endWorkEvent = this.drivingHoursStrategy.getEnd(this);
				events.add(endWorkEvent);
				logger.debug("StartWorkEvent processed, now isOutOfWork = false.");
			}
		}
		return events;
	}

	@Override
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	@Override
	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return this.currentPlace;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public boolean isAvailableForAllocation(IClock clock) {
		return this.isAllocatedBy == null;
	}

	@Override
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element) {
		return this.isAllocatedBy != null && this.isAllocatedBy.equals(element) && !this.isOutOfWork;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		this.isAllocatedBy = element;

	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		this.isAllocatedBy = null;
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		throw new RuntimeException("Not implemented.");
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

}
