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
package vrpsim.core.model.structure.depot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.impl.CloseEvent;
import vrpsim.core.model.events.impl.OpenEvent;
import vrpsim.core.model.events.strategies.IOpeningHoursStrategy;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.storage.ICanStoreManager;
import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.structure.storage.IStorableGenerator;
import vrpsim.core.model.structure.storage.impl.SimpleNoStateCanStoreManager;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

/**
 * A {@link SourceDepot} runs never out of stock. If
 * {@link SourceDepot#unload(vrpsim.core.model.structure.storage.impl.StorableType)}
 * is called a new {@link IStorable} is generated with
 * {@link IStorableGenerator} and returned.
 * 
 * @author mayert
 */
public class SourceDepot implements IDepot {

	private static Logger logger = LoggerFactory.getLogger(SourceDepot.class);

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	private final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;
	private final ICanStoreManager canStoreManager;

	private final IOpeningHoursStrategy openingHoursStrategy;
	private boolean isClosed;

	public SourceDepot(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final StorableParameters storableParameters) {
		this(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storableParameters, null);
	}

	public SourceDepot(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final StorableParameters storableParameters, final IOpeningHoursStrategy openingHoursStrategy) {
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
		this.canStoreManager = new SimpleNoStateCanStoreManager(storableParameters);
		this.openingHoursStrategy = openingHoursStrategy;
		this.isClosed = false;
	}

	public void reset() {
		if (this.canStoreManager != null) {
			this.canStoreManager.reset();
		}
		if (this.openingHoursStrategy != null) {
			this.openingHoursStrategy.reset();
		}
		this.isClosed = false;
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		List<IEventType> eventTypes = new ArrayList<>();
		if (this.openingHoursStrategy != null) {
			eventTypes.addAll(this.openingHoursStrategy.getEventTypes());
		}
		return eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<>();
		if (this.openingHoursStrategy != null) {
			events.add(this.openingHoursStrategy.getClose(this));
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
		return events;
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
		return true;
	}

	@Override
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element) {
		return !isClosed;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		// empty by purpose

	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		// empty by purpose

	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		throw new RuntimeException("Not implemented.");
	}
}
