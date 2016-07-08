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
package vrpsim.core.model.structure.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.behaviour.activities.util.ServiceTimeCalculationInformationContainer;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.UncertainEvent;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
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
public class DefaultDriver extends Observable implements IDriver {

	private static Logger logger = LoggerFactory.getLogger(DefaultDriver.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;

	private final List<IEventType> eventTypes;
	private final UncertainParamters breakdownParameters;

	protected boolean isBroken = false;
	protected boolean isAvailable = true;
	protected IVRPSimulationBehaviourElementCanAllocate isAllocatedBy;

	public DefaultDriver(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final UncertainParamters breakdownParameters) {

		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
		this.breakdownParameters = breakdownParameters;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {

			public String getType() {
				return IEventType.BREAKDOWN_EVENT;
			}
		});
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		// Create an breakdownevent for each container.
		List<IEvent> events = new ArrayList<IEvent>();
		for (UncertainParamters.UncertainParameterContainer container : this.breakdownParameters.getParameter()) {
			events.add(createEvent(container, clock, true));
		}
		return events;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws EventException {

		if (!(event.getType().equals(this.eventTypes.get(0))) || !(event instanceof UncertainEvent)) {
			throw new RejectEventException(
					"Event type " + event.getType() + " not supported from " + this.getClass().getSimpleName());
		} else {

			// Logic of breakdown.

			if (!this.isBroken) {
				// Currently not broken, have to change to broken.
				if (this.isAllocatedBy != null) {
					// Vehicle is allocated, means used with in a Tour.
					// Inform the allocated by, that vehicle is now broken.
					try {
						this.isAllocatedBy.allocatedElementStateChanged(this);
					} catch (BehaviourException e) {
						throw new ErrorDuringEventProcessingException(
								"DefaultDriver is allocated and internal state changed from unbroken to broken. The state changed is not propper handled by the allocating element. Original message: "
										+ e.getMessage());
					}
				}

				this.isBroken = true;
			} else {
				// Currently broken, have to change to not broken.
				if (this.isAllocatedBy != null) {
					// Vehicle is allocated, means used with in a Tour.
					// Inform the allocated by, that vehicle is not broken
					// anymore.
					try {
						this.isAllocatedBy.allocatedElementStateChanged(this);
					} catch (BehaviourException e) {
						throw new ErrorDuringEventProcessingException(
								"DefaultDriver is allocated and internal state changed from broken to unbroken. The state changed is not propper handled by the allocating element. Original message: "
										+ e.getMessage());
					}
				}
				this.isBroken = false;
			}

			logger.debug("{} from type {} is out of order now? {}.", this.vrpSimulationModelElementParameters.getId(),
					this.getClass().getSimpleName(), this.isAvailable);
		}

		List<IEvent> events = new ArrayList<>();
		if (((UncertainEvent) event).getContainer().isCyclic()) {
			events.add(createEvent(((UncertainEvent) event).getContainer(), clock, false));
		}
		return events;
	}

	private IEvent createEvent(UncertainParameterContainer container, IClock clock, boolean isInitialEvent) {
		double t = isInitialEvent ? container.getStart().getNumber() : container.getCycle().getNumber();
		double timeFrom = this.isAvailable(clock) ? container.getNumber().getNumber() : t;
		ITime time = clock.getCurrentSimulationTime().createTimeFrom(timeFrom);
		return new UncertainEvent(this, this.getAllEventTypes().get(0), time, container);
	}

	@Override
	public boolean isAvailable(IClock clock) {
		return isBroken ? !isBroken : this.isAvailable;
	}

	@Override
	public UncertainParamters getBreakdownParameters() {
		return this.breakdownParameters;
	}

	@Override
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public ITime getServiceTime(ServiceTimeCalculationInformationContainer serviceTimeCalculationInformationContainer,
			IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		this.isAvailable = false;
		this.isAllocatedBy = element;
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		this.isAvailable = true;
		this.isAllocatedBy = null;
		this.setChanged();
		this.notifyObservers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.IVRPSimulationModelElement#
	 * getVRPSimulationModelElementParameters()
	 */
	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return this.vrpSimulationModelStructureElementParameters.getHome();
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
	}

}
