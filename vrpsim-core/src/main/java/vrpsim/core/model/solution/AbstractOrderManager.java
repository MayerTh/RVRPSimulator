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
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

public abstract class AbstractOrderManager implements IDynamicBehaviourProvider {

	protected final static Logger logger = LoggerFactory.getLogger(AbstractOrderManager.class);
	protected final OrderBord orderBord;
	
	protected StructureService structureService;
	protected NetworkService networkService;
	protected BehaviourService behaviourService;
	protected EventListService eventListService;
	
	public AbstractOrderManager() {
		this.orderBord = new OrderBord(this);
	}
	
	@Override
	public OrderBord getOrderBord() {
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
	public void initialize(EventListService eventListService, StructureService structureService, NetworkService networkService,
			BehaviourService behaviourService) {
		this.eventListService = eventListService;
		this.structureService = structureService;
		this.networkService = networkService;
		this.behaviourService = behaviourService;
	}

	/**
	 * Handles the {@link OrderEvent}. An implementation of
	 * {@link AbstractOrderManager} has to offer all {@link Order} within
	 * {@link OrderEvent} to all occasional drivers.
	 * 
	 * If no occasional driver is interested, the implementation can adjust the
	 * costs offered for the order. See
	 * {@link Order#setAdditionalCost(OrderCost)}. If still no occasional driver
	 * is interested, the implementation has to satisfy the {@link Order}
	 * through an own {@link IVehicle}.
	 * 
	 * Note: consider the simulation time {@link ITime}, an occasional driver
	 * may not be interested in the order at current simulation.
	 * 
	 * @param orderEvent
	 * @param simulationClock
	 */
	public abstract void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock);

}
