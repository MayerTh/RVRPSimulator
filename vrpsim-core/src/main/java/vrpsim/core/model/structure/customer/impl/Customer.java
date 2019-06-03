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
package vrpsim.core.model.structure.customer.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.impl.CloseEvent;
import vrpsim.core.model.events.impl.ConsumEvent;
import vrpsim.core.model.events.impl.OpenEvent;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.events.strategies.IConsumStrategy;
import vrpsim.core.model.events.strategies.IOpeningHoursStrategy;
import vrpsim.core.model.events.strategies.IOrderStrategy;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.storage.ICanStoreManager;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

public class Customer implements ICustomer {

	private static Logger logger = LoggerFactory.getLogger(Customer.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;
	private final ICanStoreManager canStoreManager;
	private final IOrderStrategy orderStrategy;
	private final IOpeningHoursStrategy openingHoursStrategy;
	private final IConsumStrategy consumStrategy;
	private final int serveInParallel;
	private boolean isClosed;

	private Set<IVRPSimulationBehaviourElementCanAllocate> allocatedFrom = new HashSet<>();

	public Customer(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			ICanStoreManager canStoreManager, IOrderStrategy orderStrategy) {

		this(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, canStoreManager, orderStrategy, null, null,
				Integer.MAX_VALUE);
	}

	public Customer(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			ICanStoreManager canStoreManager, IOrderStrategy orderStrategy, final IOpeningHoursStrategy openingHoursStrategy,
			final IConsumStrategy consumStrategy, final int serveInParallel) {
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
		this.canStoreManager = canStoreManager;
		this.serveInParallel = serveInParallel;
		this.orderStrategy = orderStrategy;
		this.openingHoursStrategy = openingHoursStrategy;
		this.consumStrategy = consumStrategy;
		this.isClosed = false;
	}
	
	@Override
	public IOrderStrategy getOrderStrategy() {
		return this.orderStrategy;
	}

	@Override
	public void reset() {
		if (this.orderStrategy != null) {
			this.orderStrategy.reset();
		}
		if (this.openingHoursStrategy != null) {
			this.openingHoursStrategy.reset();
		}
		if (consumStrategy != null) {
			this.consumStrategy.reset();
		}
		if (this.canStoreManager != null) {
			this.canStoreManager.reset();
		}
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		List<IEventType> eventTypes = new ArrayList<>();
		if (this.openingHoursStrategy != null) {
			eventTypes.addAll(this.openingHoursStrategy.getEventTypes());
		}
		if (this.consumStrategy != null) {
			eventTypes.addAll(this.consumStrategy.getEventTypes());
		}
		eventTypes.addAll(this.orderStrategy.getEventTypes());
		return eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<>();
		if (this.openingHoursStrategy != null) {
			events.add(this.openingHoursStrategy.getClose(this));
		}
		if (this.consumStrategy != null) {
			events.add(this.consumStrategy.getConsum(this));
		}
		OrderEvent orderEvent = this.orderStrategy.getNextDynamicOrder(this);
		if (orderEvent != null) {
			events.add(orderEvent);
		}
		return events;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException {
		List<IEvent> events = new ArrayList<>();
		if (this.openingHoursStrategy != null) {
			if (event instanceof CloseEvent) {
				this.isClosed = true;
				OpenEvent openEvent = this.openingHoursStrategy.getOpen(this);
				events.add(openEvent);
				logger.debug("CloseEvent processed, now isClosed = true.");
			} else if (event instanceof OpenEvent) {
				this.isClosed = false;
				CloseEvent closeEvent = this.openingHoursStrategy.getClose(this);
				events.add(closeEvent);
				logger.debug("OpenEvent processed, now isClosed = false.");
			}
		}
		if (this.consumStrategy != null) {
			if (event instanceof ConsumEvent) {
				processConsum((ConsumEvent) event);
				events.add(this.consumStrategy.getConsum(this));
				logger.debug("ConsumEvent processed, new ConsumEvent generated.");
			}
		}
		if (event instanceof OrderEvent) {
			OrderEvent orderEvent = this.orderStrategy.getNextDynamicOrder(this);
			if (orderEvent != null) {
				events.add(orderEvent);
			}
			logger.debug("OrderEvent processed, new OrderEvent generated.");
		}
		return events;
	}

	private void processConsum(ConsumEvent event) throws EventException {
		StorableParameters sp = event.getConsum().getStorableParameters();
		int number = event.getConsum().getNumber();
		try {
			this.canStoreManager.unload(sp, number);
			logger.debug("Consum {} of {} from {}.", number, sp, this);
		} catch (StorageException e) {
			throw new EventException("Can not consum " + number + " of " + sp + ". StorageException message: " + e.getMessage());
		}
	}

	@Override
	public ICanStoreManager getCanStoreManager() {
		return this.canStoreManager;
	}

	@Override
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public boolean isAvailableForAllocation(IClock clock) {
		return this.allocatedFrom.size() < this.serveInParallel;
	}

	@Override
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element) {
		return this.allocatedFrom.contains(element) && !this.isClosed;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		this.allocatedFrom.add(element);
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		this.allocatedFrom.remove(element);
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<Order> getStaticOrdersBeforeEventGeneration() {
		return this.orderStrategy.getStaticOrders(this);
	}

	@Override
	public boolean isHasDynamicEvents() {
		return this.orderStrategy.hasDynamicEvents();
	}

}
