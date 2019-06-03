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
package vrpsim.core.model;

import java.util.Observer;

import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElement;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.simulator.IClock;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IVRPSimulationModelElement extends IVRPSimulationElement {

	/**
	 * Returns the {@link VRPSimulationModelElementParameters} defining the
	 * {@link IVRPSimulationElement}.
	 * 
	 * @return
	 */
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters();

	/**
	 * Return if the {@link IVRPSimulationElement} is available for allocation at
	 * the current simulation time readable from the {@link IClock}.
	 * 
	 * @param clock
	 * @return
	 */
	public boolean isAvailableForAllocation(IClock clock);

	/**
	 * Return if the element is available for interaction. An
	 * {@link IVRPSimulationModelElement} should be only available for interaction
	 * if it is allocated by the corresponding
	 * {@link IVRPSimulationBehaviourElementCanAllocate}.
	 * 
	 * @param clock
	 * @return
	 */
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element);

	/**
	 * To interact with an {@link IVRPSimulationElement}, the element gets allocated
	 * by the {@link IVRPSimulationBehaviourElement} which would like to interact
	 * with it.
	 * 
	 * If an element gets allocated although it is not available, the element has to
	 * handle it.
	 * 
	 * @param element
	 */
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element);

	/**
	 * After interaction the {@link IVRPSimulationElement} frees itself from the
	 * {@link IVRPSimulationBehaviourElement} interacted with.
	 * 
	 * @param element
	 */
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element);

	/**
	 * {@link Observer} getting notified when released from an allocation.
	 * 
	 * @param observer
	 */
	public void addReleaseFromListener(Observer observer);

}
