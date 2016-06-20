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
package vrpsim.core.model.behaviour.activities.util;

import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.util.exceptions.JobException;

/**
 * @date 22.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class StorableExchangeJob implements IJob {

	private final StorableParameters storeableParameters;
	private final Integer number;
	private final IVRPSimulationModelStructureElementWithStorage storableSource;
	private final IVRPSimulationModelStructureElementWithStorage storableTarget;

	public StorableExchangeJob(StorableParameters storeableParameters, Integer number,
			IVRPSimulationModelStructureElementWithStorage storableSource,
			IVRPSimulationModelStructureElementWithStorage storableTarget) throws JobException {
		super();
		this.storeableParameters = storeableParameters;
		this.number = number;
		this.storableSource = storableSource;
		this.storableTarget = storableTarget;
	}

	public StorableParameters getStoreableParameters() {
		return storeableParameters;
	}

	public IVRPSimulationModelStructureElementWithStorage getStorableSource() {
		return storableSource;
	}

	public IVRPSimulationModelStructureElementWithStorage getStorableTarget() {
		return storableTarget;
	}

	public Integer getNumber() {
		return number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.IJob#getPlaceOfStart()
	 */
	@Override
	public IVRPSimulationModelNetworkElement getPlaceOfJobExecution() {
		return this.storableTarget.getVRPSimulationModelStructureElementParameters().getHome();
	}

	@Override
	public IVRPSimulationModelStructureElementWithStorageMovable getInvolvedTransporter() {
		// TODO Auto-generated method stub
		return null;
	}

}
