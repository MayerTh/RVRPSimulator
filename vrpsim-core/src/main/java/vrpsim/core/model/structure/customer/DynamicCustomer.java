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

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.events.UncertainEvent;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.model.util.uncertainty.UncertainParamters.UncertainParameterContainer;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * {@link DefaultNonDynamicCustomer} creates periodical orders.
 * 
 * @author mayert
 */
public class DynamicCustomer extends AbstractVRPSimulationModelStructureElementWithStorage implements ICustomer {

	private final UncertainParamters orderParameters;
	private List<IEventType> eventTypes = new ArrayList<IEventType>();
	private List<Order> createdOrders = new ArrayList<>();

	public DynamicCustomer(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final DefaultStorageManager storageManager, final UncertainParamters orderParameters) {
		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);

		this.orderParameters = orderParameters;

		/* The Order itself. */
		eventTypes.add(() -> IEventType.ORDER_EVENT);
		/* When to trigger new orders. */
		eventTypes.add(() -> IEventType.TRIGGERING_ORDER_EVENT);
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> initialEvents = new ArrayList<>();
		for (UncertainParamters.UncertainParameterContainer container : this.orderParameters.getParameter()) {
			initialEvents.add(createTRIGGERING_ORDER_EVENT(container, clock));
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
			events.add(createTRIGGERING_ORDER_EVENT(uncertainEvent.getContainer(), clock));
			events.add(createORDER_EVENT(uncertainEvent.getContainer(), clock));
		}

		return events;
	}

	@Override
	public ITime getServiceTime(IJob job, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	@Override
	public UncertainParamters getUncertainParameters() {
		return this.orderParameters;
	}

	private IEvent createTRIGGERING_ORDER_EVENT(UncertainParamters.UncertainParameterContainer container,
			IClock clock) {
		return new UncertainEvent(this, () -> IEventType.TRIGGERING_ORDER_EVENT,
				clock.getCurrentSimulationTime().createTimeFrom(container.getCycle().getNumber()), container);
	}

	private IEvent createORDER_EVENT(UncertainParameterContainer container, IClock clock) throws EventException {

		// if(container.getEarliestDueDate() == null ||
		// container.getLatestDueDate() == null) {
		// throw new EventException("Can not create Order event with no
		// earliestDueDate or latestDueDate.");
		// }

		ITime earliestDueDate = container.getEarliestDueDate() != null
				? clock.getCurrentSimulationTime().add(
						clock.getCurrentSimulationTime().createTimeFrom(container.getEarliestDueDate().getNumber()))
				: null;
		ITime latestDueDate = container.getLatestDueDate() != null
				? clock.getCurrentSimulationTime()
						.add(clock.getCurrentSimulationTime().createTimeFrom(container.getLatestDueDate().getNumber()))
				: null;

		Order order = new Order(createOrderId(clock.getCurrentSimulationTime()), earliestDueDate, latestDueDate,
				container.getStorableParameters().getStorableType(), container.getNumber().getNumber().intValue(),
				this);

		// Save order in history.
		this.createdOrders.add(order);

		// An order event always occurrs with no time delay.
		return new OrderEvent(this, () -> IEventType.ORDER_EVENT, 0,
				clock.getCurrentSimulationTime().createTimeFrom(0.0), order);
	}

	private String createOrderId(ITime currentTime) {
		return "ORDER_FROM_" + this.getVRPSimulationModelElementParameters().getId() + "_AT" + currentTime.getValue();
	}

}
