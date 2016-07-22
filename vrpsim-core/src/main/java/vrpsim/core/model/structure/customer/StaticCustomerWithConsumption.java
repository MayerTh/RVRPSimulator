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

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.activities.util.TimeCalculationInformationContainer;
import vrpsim.core.model.events.ConsumptionEvent;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.model.util.uncertainty.UncertainParamters.UncertainParameterContainer;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

public class StaticCustomerWithConsumption extends AbstractVRPSimulationModelStructureElementWithStorage
		implements IStaticCustomer {

	private static Logger logger = LoggerFactory.getLogger(StaticCustomerWithConsumption.class);

	private final UncertainParamters consumptionParameters;
	private final ITimeFunction serviceTimeFunction;
	private final List<IEventType> eventTypes;
	private final List<Order> staticOrdersBeforeEventGeneration;
	private List<Order> createdOrders = new ArrayList<>();
	private final List<IEvent> initialEvents;

	public StaticCustomerWithConsumption(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final UncertainParamters consumptionParameters, final DefaultStorageManager storageManager,
			final ITimeFunction serviceTimeFunction) {

		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);
		this.consumptionParameters = consumptionParameters;
		this.serviceTimeFunction = serviceTimeFunction;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(() -> IEventType.CONSUMPTION_EVENT);

		this.createdOrders = new ArrayList<>();
		this.staticOrdersBeforeEventGeneration = new ArrayList<>();
		this.initialEvents = new ArrayList<>();
		for (UncertainParamters.UncertainParameterContainer container : this.consumptionParameters.getParameter()) {

			// Also latest due date
			ITime timeTillOccurence = new Clock.Time(container.getLatestDueDate());

			Order order = new Order("StaticCustomerWithConsumption",
					new Clock.Time(container.getEarliestDueDate()), timeTillOccurence,
					container.getStorableParameters().getStorableType(), container.getNumber().intValue(),
					(IVRPSimulationModelStructureElementWithStorage) this);
			ConsumptionEvent consumptionEvent = new ConsumptionEvent(this, () -> IEventType.CONSUMPTION_EVENT, 0,
					timeTillOccurence, order, container);

			this.createdOrders.add(order);
			this.staticOrdersBeforeEventGeneration.add(order);
			this.initialEvents.add(consumptionEvent);
		}
	}

	@Override
	public List<Order> getAllCreatedOrders() {
		return this.createdOrders;
	}

	@Override
	public List<Order> getStaticOrdersBeforeEventGeneration() {
		return this.staticOrdersBeforeEventGeneration;
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
		return this.initialEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.events.IEventOwner#processOwnEvent(vrpsim.core.model.
	 * events.IEvent)
	 */
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws EventException {

		List<IEvent> events = new ArrayList<>();
		if (event.getType().getType().equals(IEventType.CONSUMPTION_EVENT)) {

			ConsumptionEvent cEvent = (ConsumptionEvent) event;
			Order order = cEvent.getOrder();
			StorableType storableType = order.getStorableType();
			int numberToConsum = order.getAmount();

			try {

				logger.debug("{} with id {} will consume {} from type {}. Current capacity {}.",
						this.getClass().getSimpleName(), this.vrpSimulationModelElementParameters.getId(),
						numberToConsum, storableType.getId(), this.getCurrentCapacity(storableType).getValue());

			} catch (VRPArithmeticException vrpArithmeticException) {
				vrpArithmeticException.printStackTrace();
				String msg = vrpArithmeticException.getClass().getSimpleName() + ": "
						+ vrpArithmeticException.getMessage();
				logger.error(msg);
				throw new ErrorDuringEventProcessingException(msg);
			}

			for (int i = 0; i < numberToConsum; i++) {
				try {
					IStorable consumedStorable = this.unload(storableType);
					logger.debug("Following storable is consumed: {}", consumedStorable.getStorableId());
				} catch (StorageException storageException) {
					storageException.printStackTrace();
					String msg = storageException.getClass().getSimpleName() + ": " + storageException.getMessage();
					logger.error(msg);
					throw new ErrorDuringEventProcessingException(msg);
				}
			}

			this.storageManager.printDebugInformationForStorage(this.vrpSimulationModelElementParameters.getId());
			if (cEvent.getContainer().isCyclic()) {
				cEvent.getContainer().resetInstances();
				events.addAll(createORDER_AND_CONSUMPTION_EVENT(cEvent.getContainer(), clock));
			}
		}
		return events;
	}

	private List<IEvent> createORDER_AND_CONSUMPTION_EVENT(UncertainParameterContainer container, IClock clock)
			throws EventException {

		ITime earliestDueDate = null;
		if(container.getEarliestDueDate() != null) {
			earliestDueDate = container.isAdaptDueDatesToSimulationTime() 
				? clock.getCurrentSimulationTime().add(clock.getCurrentSimulationTime().createTimeFrom(container.getEarliestDueDate())) 
				: clock.getCurrentSimulationTime().createTimeFrom(container.getEarliestDueDate());
		}
		
		ITime latestDueDate = null;
		if(container.getLatestDueDate() != null) {
			latestDueDate = container.isAdaptDueDatesToSimulationTime() 
				? clock.getCurrentSimulationTime().add(clock.getCurrentSimulationTime().createTimeFrom(container.getLatestDueDate())) 
				: clock.getCurrentSimulationTime().createTimeFrom(container.getLatestDueDate());
		}
		
//		ITime earliestDueDate = container.getEarliestDueDate() != null
//				? clock.getCurrentSimulationTime().add(
//						clock.getCurrentSimulationTime().createTimeFrom(container.getEarliestDueDate()))
//				: null;
//		ITime latestDueDate = container.getLatestDueDate() != null
//				? clock.getCurrentSimulationTime()
//						.add(clock.getCurrentSimulationTime().createTimeFrom(container.getLatestDueDate()))
//				: null;

		Order order = new Order(createOrderId(clock.getCurrentSimulationTime()), earliestDueDate, latestDueDate,
				container.getStorableParameters().getStorableType(), container.getNumber().intValue(),
				this);

		// Save order in history.
		this.createdOrders.add(order);

		// An order event always occurs with no time delay, and is not
		// processed by the customer itself.
		OrderEvent orderEvent = new OrderEvent(this, () -> IEventType.ORDER_EVENT, 0,
				clock.getCurrentSimulationTime().createTimeFrom(0.0), order);

		// The consumption of the order is discribed with an ConcumptionEvent.
		// It is triggered for the latest DueDate of the order.
		ConsumptionEvent consumptionEvent = new ConsumptionEvent(this, () -> IEventType.CONSUMPTION_EVENT, 0,
				clock.getCurrentSimulationTime().createTimeFrom(container.getLatestDueDate()), order,
				container);

		List<IEvent> events = new ArrayList<>();
		events.add(orderEvent);
		events.add(consumptionEvent);

		return events;
	}

	private String createOrderId(ITime currentTime) {
		return "ORDER_FROM_" + this.getVRPSimulationModelElementParameters().getId() + "_AT" + currentTime.getValue();
	}

	@Override
	public ITime getServiceTime(TimeCalculationInformationContainer container, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(this.serviceTimeFunction.getTime(container, clock));
	}

	@Override
	public ITimeFunction getServiceTimeFunction() {
		return this.serviceTimeFunction;
	}

}
