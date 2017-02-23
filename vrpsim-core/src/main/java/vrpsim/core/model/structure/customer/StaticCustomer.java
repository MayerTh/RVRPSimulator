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
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

public class StaticCustomer extends AbstractVRPSimulationModelStructureElementWithStorage implements IStaticCustomer {

	private static Logger logger = LoggerFactory.getLogger(StaticCustomer.class);

	private final UncertainParamters consumptionParameters;
	private final ITimeFunction serviceTimeFunction;
	private final List<IEventType> eventTypes;
	private final List<Order> staticOrdersBeforeEventGeneration;

	public StaticCustomer(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters, final UncertainParamters consumptionParameters,
			final DefaultStorageManager storageManager, final ITimeFunction serviceTimeFunction) {

		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);
		this.consumptionParameters = consumptionParameters;
		this.serviceTimeFunction = serviceTimeFunction;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(() -> IEventType.CONSUMPTION_EVENT);

		this.staticOrdersBeforeEventGeneration = new ArrayList<>();
		for (UncertainParameterContainer container : this.consumptionParameters.getParameter()) {

			// Also latest due date
			ITime timeTillOccurence = new Clock.Time(container.getNewRealizationFromLatestDueDateDistributionFunction());

			Order order = new Order(createOrderId(), new Clock.Time(container.getNewRealizationFromEarliestDueDateDistributionFunction()), timeTillOccurence, container.getStorableParameters().getStorableType(),
					container.getNewRealizationFromNumberDistributionFunction().intValue(), (IVRPSimulationModelStructureElementWithStorage) this);

			logger.debug("Static order created {}.", order.getId());

			this.staticOrdersBeforeEventGeneration.add(order);
		}
	}

	private String createOrderId() {
		return "static_order_created_from_" + this.getVRPSimulationModelElementParameters().getId() + "_at_0.";
	}

	@Override
	public List<Order> getAllCreatedOrders() {
		return this.staticOrdersBeforeEventGeneration;
	}

	@Override
	public List<Order> getStaticOrdersBeforeEventGeneration() {
		return this.staticOrdersBeforeEventGeneration;
	}

	@Override
	public UncertainParamters getUncertainParameters() {
		return this.consumptionParameters;
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		return new ArrayList<>();
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException {
		return new ArrayList<>();
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
