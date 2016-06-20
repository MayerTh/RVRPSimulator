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
package vrpsim.core.model.structure.occasionaldriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.solution.OrderBord;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

public abstract class AbstractOccasionalDriver extends DefaultStorageManager implements IOccasionalDriver {

	protected Logger logger = LoggerFactory.getLogger(AbstractOccasionalDriver.class);

	protected VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	protected VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;
	protected EventListService eventListService;
	protected NetworkService networkService;
	protected StructureService structureService;

	public AbstractOccasionalDriver(VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			DefaultStorage storage) {
		super(storage);

		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
	}

	@Override
	public void registerToObserve(OrderBord orderBorad) {
		orderBorad.addObserver(this);
	}

	@Override
	public void registerServices(EventListService eventListInterface, StructureService structureService,
			NetworkService networkService) {
		this.eventListService = eventListInterface;
		this.networkService = networkService;
		this.structureService = structureService;
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
	public boolean isAvailable(IClock clock) {
		return true;
	}

	@Override
	public ITime getServiceTime(IJob job, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	@Override
	public void allocateBy(IVRPSimulationModelElement element) {
	}

	@Override
	public void freeFrom(IVRPSimulationModelElement element) {
	}
}
