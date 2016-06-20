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
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;

/**
 * @date 22.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class StorableTransportJob implements IJob {

	private final INode transportTarget;
	private final IVRPSimulationModelStructureElementWithStorageMovable transporter;

	public StorableTransportJob(INode transportTarget, IVRPSimulationModelStructureElementWithStorageMovable transporter) {
		super();
		this.transportTarget = transportTarget;
		this.transporter = transporter;
	}

	public StorableTransportJob(IVRPSimulationModelNetworkElement transportTarget,
			IVRPSimulationModelStructureElementWithStorageMovable transporter) {
		super();
		this.transportTarget = (INode) transportTarget;
		this.transporter = transporter;
	}

	public INode getTransportTarget() {
		return transportTarget;
	}

	public IVRPSimulationModelStructureElementWithStorage getTransporter() {
		return transporter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.IJob#getPlaceOfJobExecution()
	 */
	@Override
	public IVRPSimulationModelNetworkElement getPlaceOfJobExecution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRPSimulationModelStructureElementWithStorageMovable getInvolvedTransporter() {
		return this.transporter;
	}

}
