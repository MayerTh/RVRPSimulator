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

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.solution.PublicOrderPlatform;
import vrpsim.core.model.structure.AbstractVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.simulator.EventListService;

public abstract class AbstractOccasionalDriver extends AbstractVRPSimulationModelStructureElementWithStorage implements IOccasionalDriver{

	protected Logger logger = LoggerFactory.getLogger(AbstractOccasionalDriver.class);

	protected EventListService eventListService;
	protected NetworkService networkService;
	protected StructureService structureService;

	public AbstractOccasionalDriver(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final DefaultStorageManager storageManager) {
		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);

	}

	@Override
	public void registerToObserve(PublicOrderPlatform orderBorad) {
		orderBorad.addObserver(this);
	}

	@Override
	public void registerServices(EventListService eventListInterface, StructureService structureService,
			NetworkService networkService) {
		this.eventListService = eventListInterface;
		this.networkService = networkService;
		this.structureService = structureService;
	}

}
