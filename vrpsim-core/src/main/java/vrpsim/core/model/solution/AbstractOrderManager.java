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
package vrpsim.core.model.solution;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.behaviour.BehaviourService;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.customer.DynamicCustomer;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

public abstract class AbstractOrderManager implements IDynamicBehaviourProvider {

	protected final static Logger logger = LoggerFactory.getLogger(AbstractOrderManager.class);

	protected final PublicOrderPlatform orderBord;

	protected StructureService structureService;
	protected NetworkService networkService;
	protected BehaviourService behaviourService;
	protected EventListService eventListService;

	public AbstractOrderManager() {
		this.orderBord = new PublicOrderPlatform(this);
	}

	@Override
	public PublicOrderPlatform getOrderBord() {
		return this.orderBord;
	}

	@Override
	public Set<IEventType> registerForEventTypes(Set<IEventType> availableEventTypes) {
		Set<IEventType> eventTypes = new HashSet<>();
		eventTypes.add(() -> IEventType.ORDER_EVENT);
		return eventTypes;
	}

	@Override
	public void notify(IEvent event, IClock simulationClock) throws RejectEventException {
		logger.debug("Order event registered: " + event.toString());
		if (event instanceof OrderEvent) {
			this.handleOrderEvent((OrderEvent) event, simulationClock);
		} else {
			throw new RejectEventException(
					this.getClass().getSimpleName() + " can not handle event from type: " + event.getType());
		}
	}

	@Override
	public void initialize(EventListService eventListService, StructureService structureService,
			NetworkService networkService, BehaviourService behaviourService) {
		this.eventListService = eventListService;
		this.structureService = structureService;
		this.networkService = networkService;
		this.behaviourService = behaviourService;
	}

	/**
	 * Handles the {@link OrderEvent} created from dynamic parts of the vehicle
	 * routing problem. Currently {@link DynamicCustomer}, an implementation of
	 * {@link ICustomer} creates {@link OrderEvent}. An implementation of
	 * {@link AbstractOrderManager} functions as dispatcher within the system.
	 * An implementation of {@link AbstractOrderManager} has the following
	 * possibilities to handle and {@link OrderEvent}:
	 * 
	 * - Simply ignore it.
	 * 
	 * - Publishing it at the {@link PublicOrderPlatform}, after adding the following
	 * parameters to the {@link Order}:
	 * {@link Order#setProvider(vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage)}
	 * (where to pick up the ordered stuff?)
	 * {@link Order#setAdditionalCost(OrderCost)} (What the Dispatcher (
	 * {@link AbstractOrderManager}) is willing to pay for delivering the
	 * order.) The {@link AbstractOrderManager} gets informed through
	 * {@link IDynamicBehaviourProvider#handleNotTakenOrder(Order)}, if an
	 * published {@link Order} will not be handled from an
	 * {@link IOccasionalDriver} (or any thing else who can access the
	 * {@link PublicOrderPlatform}). -> the parameters can now be readjusted, and the
	 * {@link Order} can be published again for example, or the own fleet can be
	 * used for delivery (see the next point).
	 * 
	 * - Use the own fleet to deliver the order. Therefore access to the
	 * network, structure and event list is provided by
	 * {@link AbstractOrderManager#networkService},
	 * {@link AbstractOrderManager#structureService},
	 * {@link AbstractOrderManager#behaviourService}, and
	 * {@link AbstractOrderManager#eventListService}. Different other
	 * conveniences are implemented, see for example
	 * {@link IVRPSimulationModelStructureElement#addReleaseFromListener(java.util.Observer)}
	 * , {@link IVRPSimulationModelStructureElement#isAvailable(IClock)}, ...
	 * 
	 * @param orderEvent
	 * @param simulationClock
	 */
	public abstract void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock);

}
