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
package vrpsim.core.model.behaviour.activities.util;

import vrpsim.core.model.behaviour.activities.TransportActivity;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;

/**
 * Holds all information to execute an {@link TransportActivity}.
 * 
 * @date 22.02.2016
 * @author thomas.mayer@unibw.de
 */
public class TransportJob implements IJob {

	private final IVRPSimulationModelNetworkElement transportTarget;

	public TransportJob(IVRPSimulationModelNetworkElement transportTarget) {
		this.transportTarget = (INode) transportTarget;
	}

	public IVRPSimulationModelNetworkElement getTransportTarget() {
		return transportTarget;
	}

}
