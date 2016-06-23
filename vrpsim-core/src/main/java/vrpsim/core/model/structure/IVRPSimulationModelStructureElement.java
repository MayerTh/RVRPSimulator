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
package vrpsim.core.model.structure;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IVRPSimulationModelStructureElement extends IVRPSimulationModelElement {

	/**
	 * Returns the {@link VRPSimulationModelStructureElementParameters} defining
	 * the {@link IVRPSimulationModelStructureElement}.
	 * 
	 * @return
	 */
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters();

	/**
	 * Returns the current place of the element. For not movable
	 * {@link IVRPSimulationModelElement}, the current place should be equal to
	 * {@link VRPSimulationModelStructureElementParameters#getHome()}.
	 * 
	 * @return
	 */
	public IVRPSimulationModelNetworkElement getCurrentPlace();

}
