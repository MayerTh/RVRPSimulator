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
package vrpsim.core.model.structure;

import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;

public interface IVRPSimulationModelStructureElementMovable extends IVRPSimulationModelStructureElement {

	/**
	 * Returns the average speed of a
	 * {@link IVRPSimulationModelStructureElementMovable}.
	 * 
	 * @return
	 */
	public double getAverageSpeed();

	/**
	 * Adds cost.
	 * 
	 * @param cost
	 */
	public void addCost(Cost cost);
	
	/**
	 * Resets the IVRPSimulationModelStructureElementMovable;
	 */
	public void reset();

	/**
	 * Sets the current place of a movable simulation element with storage.
	 * 
	 * @param networkElement
	 */
	public void setCurrentPlace(IVRPSimulationModelNetworkElement networkElement);

	/**
	 * returns the current place of a movable simulation element with storage.
	 * 
	 * @return
	 */
	public IVRPSimulationModelNetworkElement getCurrentPlace();

}
