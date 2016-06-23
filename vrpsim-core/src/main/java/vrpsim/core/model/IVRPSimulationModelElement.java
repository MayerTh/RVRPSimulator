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
package vrpsim.core.model;

import java.util.Observer;

import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

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
	 * Return if the {@link IVRPSimulationElement} is available for allocation
	 * at the current simulation time readable from the {@link IClock}.
	 * 
	 * @param clock
	 * @return
	 */
	public boolean isAvailable(IClock clock);

	/**
	 * To interact with an {@link IVRPSimulationElement}, the element gets
	 * allocated by the {@link IVRPSimulationElement} which would like to
	 * interact with it.
	 * 
	 * If an element gets allocated although it is not available, the element
	 * has to handle it.
	 * 
	 * @param element
	 */
	public void allocateBy(IVRPSimulationModelElement element);

	/**
	 * After interaction the {@link IVRPSimulationElement} frees itself from the
	 * {@link IVRPSimulationElement} interacted with.
	 * 
	 * @param element
	 */
	public void releaseFrom(IVRPSimulationModelElement element);

	/**
	 * Returns the service time depending on the {@link IJob} and the current
	 * simulation time.
	 * 
	 * @param job
	 * @param clock
	 * @return
	 */
	public ITime getServiceTime(IJob job, IClock clock);

	/**
	 * {@link Observer} getting notified when released from an allocation.
	 * 
	 * @param observer
	 */
	public void addReleaseFromListener(Observer observer);

}
