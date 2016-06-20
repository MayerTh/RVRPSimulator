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
package vrpsim.core.model.structure.depot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.UncertainEvent;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.DefaultNonDynamicCustomer;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * @date 18.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class DefaultDepot extends DefaultStorageManager implements IDepot {

	private static Logger logger = LoggerFactory.getLogger(DefaultNonDynamicCustomer.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;

	private final List<IEventType> eventTypes;
	private final UncertainParamters arrivalParameters;

	private boolean available = true;

	public DefaultDepot(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final UncertainParamters arrivalParameters, final DefaultStorage storage) {

		super(storage);
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
		this.arrivalParameters = arrivalParameters;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			public String getType() {
				return IEventType.ARRIVAL_EVENT;
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

		// Create an event for each storable type consumed by the consumer.
		List<IEvent> events = new ArrayList<IEvent>();
		for (UncertainParamters.UncertainParameterContainer container : this.arrivalParameters.getParameter()) {
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

			// Logic of a consumer.
			UncertainEvent cEvent = (UncertainEvent) event;
			int numberOfArrived = cEvent.getContainer().getNumber().getNumber().intValue();
			logger.debug("In {} with id {} just arrived {} from storable type {}.", this.getClass().getSimpleName(),
					this.vrpSimulationModelElementParameters.getId(), numberOfArrived,
					cEvent.getContainer().getStorableParameters().getStorableType().getId());

			for (CanStoreType type : this.getAllCanStoreTypes()) {
				try {
					this.loadGeneratedIn(type, cEvent.getContainer().getNumber().getNumber().intValue(),
							cEvent.getContainer().getStorableParameters());
				} catch (StorageException | VRPArithmeticException exception) {
					exception.printStackTrace();
					String msg = exception.getClass().getSimpleName() + ": " + exception.getMessage();
					logger.error(msg);
					throw new ErrorDuringEventProcessingException(msg);
				}
			}

			this.printDebugInformationForStorage(this.vrpSimulationModelElementParameters.getId());
			List<IEvent> events = new ArrayList<>();
			events.add(createEvent(cEvent.getContainer(), clock));
			return events;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.IVRPSimModelEntity#getVRPSimModelElementParameters()
	 */
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.IVRPSimModelEntity#isOutOfOrder()
	 */
	public boolean isAvailable(IClock clock) {
		return this.available;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.depot.IDepot#getArrivalParameters()
	 */
	public UncertainParamters getArrivalParameters() {
		return this.arrivalParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.util.events.IEventOwner#getAllEventTypes()
	 */
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	private IEvent createEvent(UncertainParamters.UncertainParameterContainer container, IClock clock) {
		UncertainEvent event = new UncertainEvent(this, this.eventTypes.get(0),
				clock.getCurrentSimulationTime().createTimeFrom(container.getCycle().getNumber()), container);
		return event;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.structure.IVRPSimulationModelStructureElement#
	 * getServiceTime(vrpsim.core.model.behaviour.ActivityJob,
	 * vrpsim.core.simulator.IClock)
	 */
	public ITime getServiceTime(IJob job, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.structure.IVRPSimulationModelStructureElement#
	 * allocateBy(vrpsim.core.model.structure.
	 * IVRPSimulationModelStructureElement)
	 */
	public void allocateBy(IVRPSimulationModelElement element) {
		// TODO FIX
		// this.notAvailable = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement#freeFrom(
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement)
	 */
	public void freeFrom(IVRPSimulationModelElement element) {
		// TODO FIX
		// this.notAvailable = false;
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

}
