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
package vrpsim.core.model.structure.customer;

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
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

public class DefaultNonDynamicCustomer extends DefaultStorageManager implements ICustomer {

	private static Logger logger = LoggerFactory.getLogger(DefaultNonDynamicCustomer.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;

	private final UncertainParamters consumptionParameters;
	private final List<IEventType> eventTypes;

	private boolean available = true;

	public DefaultNonDynamicCustomer(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final UncertainParamters consumptionParameters, final DefaultStorage storage) {

		super(storage);
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.consumptionParameters = consumptionParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			public String getType() {
				return IEventType.CONSUMPTION_EVENT;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.IVRPSimModelEntity#isOutOfOrder()
	 */
	public boolean isAvailable(IClock clock) {
		return available;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.consumer.ICustomer#getConsumptionParamters()
	 */
	public UncertainParamters getUncertainParameters() {
		return this.consumptionParameters;
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
	 * @see vrpsim.core.model.events.IEventOwner#getAllEventTypes()
	 */
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEventOwner#getOwnEvents()
	 */
	public List<IEvent> getInitialEvents(IClock clock) {

		// Create an event for each storable type consumed by the consumer.
		List<IEvent> events = new ArrayList<IEvent>();
		for (UncertainParamters.UncertainParameterContainer container : this.consumptionParameters.getParameter()) {
			events.add(createEvent(container, clock));
		}
		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.events.IEventOwner#processOwnEvent(vrpsim.core.model.
	 * events.IEvent)
	 */
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws RejectEventException, ErrorDuringEventProcessingException {

		if (!(event.getType().equals(this.eventTypes.get(0))) || !(event instanceof UncertainEvent)) {
			throw new RejectEventException(
					"Event type " + event.getType() + " not supported from " + this.getClass().getSimpleName());
		} else {

			// Logic of a consumer.
			UncertainEvent cEvent = (UncertainEvent) event;
			int numberOfConsumed = cEvent.getContainer().getNumber().getNumber().intValue();

			try {

				logger.debug("{} with id {} will consum {} from type {}. Current capacity {}.",
						this.getClass().getSimpleName(), this.vrpSimulationModelElementParameters.getId(),
						numberOfConsumed, cEvent.getContainer().getStorableParameters().getStorableType().getId(),
						this.getCurrentCapacity(cEvent.getContainer().getStorableParameters().getStorableType())
								.getValue());

			} catch (VRPArithmeticException vrpArithmeticException) {
				vrpArithmeticException.printStackTrace();
				String msg = vrpArithmeticException.getClass().getSimpleName() + ": "
						+ vrpArithmeticException.getMessage();
				logger.error(msg);
				throw new ErrorDuringEventProcessingException(msg);
			}

			for (int i = 0; i < cEvent.getContainer().getNumber().getNumber().intValue(); i++) {
				try {
					IStorable consumedStorable = this
							.unload(cEvent.getContainer().getStorableParameters().getStorableType());
					logger.debug("Following storable is consumed: {}", consumedStorable.getStorableId());
				} catch (StorageException storageException) {
					storageException.printStackTrace();
					String msg = storageException.getClass().getSimpleName() + ": " + storageException.getMessage();
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

	private IEvent createEvent(UncertainParamters.UncertainParameterContainer container, IClock clock) {
		UncertainEvent event = new UncertainEvent(this, this.eventTypes.get(0),
				clock.getCurrentSimulationTime().createTimeFrom(container.getCycle().getNumber()), container);
		return event;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement#allocate(
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement)
	 */
	public void allocateBy(IVRPSimulationModelElement element) {
		// TODO FIX
		// this.notAvailable = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement#free(
	 * vrpsim.core.model.structure.IVRPSimulationModelStructureElement)
	 */
	public void freeFrom(IVRPSimulationModelElement element) {
		// TODO FIX
		// this.notAvailable = false;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.IVRPSimulationModelElement#
	 * getIVRPSimulationModelElementParameters()
	 */
	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

}
