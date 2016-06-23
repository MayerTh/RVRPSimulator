/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.structure.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.UncertainEvent;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.model.util.uncertainty.UncertainParamters.UncertainParameterContainer;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class DefaultVehicle extends AbstractVRPSimulationModelStructureElementWithStorage implements IVehicle {

	private static Logger logger = LoggerFactory.getLogger(DefaultVehicle.class);

	private final List<IEventType> eventTypes;
	private final UncertainParamters breakdownParameters;
	private final Double averageSpeed;

	private boolean isBroken = false;

	public DefaultVehicle(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final UncertainParamters breakdownParameters, final DefaultStorageManager storageManager,
			final Double averageSpeed) {

		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);
		this.breakdownParameters = breakdownParameters;
		this.averageSpeed = averageSpeed;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {

			public String getType() {
				return IEventType.BREAKDOWN_EVENT;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.util.events.IEventOwner#getOwnEvents(vrpsim.core.
	 * simulator.IClock)
	 */
	public List<IEvent> getInitialEvents(IClock clock) {
		// Create an breakdownevent for each container.
		List<IEvent> events = new ArrayList<IEvent>();
		for (UncertainParamters.UncertainParameterContainer container : this.breakdownParameters.getParameter()) {
			events.add(createEvent(container, clock));
		}
		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.util.events.IEventOwner#processOwnEvent(vrpsim.core.
	 * model.util.events.IEvent, vrpsim.core.simulator.IClock)
	 */
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws EventException {

		if (!(event.getType().equals(this.eventTypes.get(0))) || !(event instanceof UncertainEvent)) {
			throw new RejectEventException(
					"Event type " + event.getType() + " not supported from " + this.getClass().getSimpleName());
		} else {

			// Logic of breakdown.
			this.isBroken = !this.isBroken;

			logger.debug("{} from type {} is out of order now? {}.", this.vrpSimulationModelElementParameters.getId(),
					this.getClass().getSimpleName(), this.isAvailable);
		}

		List<IEvent> events = new ArrayList<>();
		events.add(createEvent(((UncertainEvent) event).getContainer(), clock));
		return events;
	}

	@Override
	public boolean isAvailable(IClock clock) {
		return isBroken ? !isBroken : super.isAvailable(clock);
	}

	private IEvent createEvent(UncertainParameterContainer container, IClock clock) {
		Double timeFrom = this.isAvailable(clock) ? container.getNumber().getNumber()
				: container.getCycle().getNumber();
		ITime time = clock.getCurrentSimulationTime().createTimeFrom(timeFrom);
		return new UncertainEvent(this, this.getAllEventTypes().get(0), time, container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.vehicle.IVehicle#getBreakdownParameters()
	 */
	public UncertainParamters getBreakdownParameters() {
		return this.breakdownParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.util.events.IEventOwner#getAllEventTypes()
	 */
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.structure.IVRPSimulationModelStructureElement#
	 * getServiceTime(vrpsim.core.model.behaviour.ActivityJob)
	 */
	public ITime getServiceTime(IJob job, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	@Override
	public Double getAverageSpeed() {
		return this.averageSpeed;
	}

	@Override
	public void setCurrentPlace(IVRPSimulationModelNetworkElement networkElement) {
		this.currentPlace = networkElement;
	}

}
