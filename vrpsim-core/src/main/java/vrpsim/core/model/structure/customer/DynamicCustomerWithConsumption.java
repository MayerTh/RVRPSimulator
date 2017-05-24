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
import vrpsim.core.model.events.UncertainEvent;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * {@link StaticCustomerWithConsumption} creates {@link OrderEvent} after
 * configuration in {@link UncertainParamters}.
 * 
 * At {@link UncertainParameterContainer#getNewRealizationFromStartDistributionFunction()} the first
 * {@link OrderEvent} is generated. The second {@link OrderEvent} is generated
 * at {@link UncertainParameterContainer#getNewRealizationFromStartDistributionFunction()} +
 * {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()} and so on. No consumption of
 * goods is triggered.
 * 
 * @author mayert
 */
public class DynamicCustomerWithConsumption extends AbstractVRPSimulationModelStructureElementWithStorage
		implements ICustomer {

	private static Logger logger = LoggerFactory.getLogger(DynamicCustomerWithConsumption.class);

	private final UncertainParamters orderParameters;
	private final ITimeFunction serviceTimeFunction;
	private List<IEventType> eventTypes = new ArrayList<IEventType>();
	private List<Order> createdOrders = new ArrayList<>();

	public DynamicCustomerWithConsumption(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final DefaultStorageManager storageManager, final UncertainParamters orderParameters,
			final ITimeFunction serviceTimeFunction) {
		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);

		this.orderParameters = orderParameters;
		this.serviceTimeFunction = serviceTimeFunction;

		/* The Order itself. */
		eventTypes.add(() -> IEventType.ORDER_EVENT);
		/* When to trigger new orders. */
		eventTypes.add(() -> IEventType.TRIGGERING_ORDER_EVENT);
		/* Consumption event. */
		eventTypes.add(() -> IEventType.CONSUMPTION_EVENT);
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> initialEvents = new ArrayList<>();
		for (UncertainParameterContainer container : this.orderParameters.getParameter()) {
			initialEvents.add(createTRIGGERING_ORDER_EVENT(container, clock, true));
		}
		return initialEvents;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws EventException {

		List<IEvent> events = null;
		if (event.getType().getType().equals(IEventType.TRIGGERING_ORDER_EVENT)) {
			UncertainEvent uncertainEvent = (UncertainEvent) event;

			// A new Order event and a new trigger order event has to be
			// created.
			events = new ArrayList<>();
			if (uncertainEvent.getContainer().isCyclic()) {
//				uncertainEvent.getContainer().resetInstances();
				events.add(createTRIGGERING_ORDER_EVENT(uncertainEvent.getContainer(), clock, false));
			}
			events.addAll(createORDER_AND_CONSUMPTION_EVENT(uncertainEvent.getContainer(), clock));

		} else if (event.getType().getType().equals(IEventType.CONSUMPTION_EVENT)) {

			Order order = ((ConsumptionEvent) event).getOrder();
			StorableType storableType = order.getStorableParameters().getStorableType();
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

		}

		return events;
	}

	@Override
	public ITime getServiceTime(TimeCalculationInformationContainer container, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(this.serviceTimeFunction.getTime(container, clock));
	}

	@Override
	public UncertainParamters getUncertainParameters() {
		return this.orderParameters;
	}

	private IEvent createTRIGGERING_ORDER_EVENT(UncertainParameterContainer container, IClock clock,
			boolean isInitialEvent) {
		double t = isInitialEvent ? container.getNewRealizationFromStartDistributionFunction() : container.getNewRealizationOfCycleDistributionFunction();
		return new UncertainEvent(this, () -> IEventType.TRIGGERING_ORDER_EVENT,
				clock.getCurrentSimulationTime().createTimeFrom(t), container);
	}

	private List<IEvent> createORDER_AND_CONSUMPTION_EVENT(UncertainParameterContainer container, IClock clock)
			throws EventException {

		ITime earliestDueDate = null;
		if(container.getNewRealizationFromEarliestDueDateDistributionFunction() != null) {
			earliestDueDate = container.isAdaptDueDatesToSimulationTime() 
				? clock.getCurrentSimulationTime().add(clock.getCurrentSimulationTime().createTimeFrom(container.getNewRealizationFromEarliestDueDateDistributionFunction())) 
				: clock.getCurrentSimulationTime().createTimeFrom(container.getNewRealizationFromEarliestDueDateDistributionFunction());
		}
		
		ITime latestDueDate = null;
		if(container.getNewRealizationFromLatestDueDateDistributionFunction() != null) {
			latestDueDate = container.isAdaptDueDatesToSimulationTime() 
				? clock.getCurrentSimulationTime().add(clock.getCurrentSimulationTime().createTimeFrom(container.getNewRealizationFromLatestDueDateDistributionFunction())) 
				: clock.getCurrentSimulationTime().createTimeFrom(container.getNewRealizationFromLatestDueDateDistributionFunction());
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
				container.getStorableParameters(), container.getNewRealizationFromNumberDistributionFunction().intValue(),
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
				clock.getCurrentSimulationTime().createTimeFrom(container.getNewRealizationFromLatestDueDateDistributionFunction()), order,
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
	public List<Order> getAllCreatedOrders() {
		return this.createdOrders;
	}

	@Override
	public ITimeFunction getServiceTimeFunction() {
		return this.serviceTimeFunction;
	}

}
