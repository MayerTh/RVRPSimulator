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
package vrpsim.core.model.util.distances;

import vrpsim.core.model.network.Location;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.simulator.IClock;

/**
 * Calculates the travel time depending on a distance and on a speed.
 * 
 * @date 02.06.2016
 * @author thomas.mayer@unibw.de
 */
public interface ITimeFunction {

	/**
	 * Returns a travel time depending on following parameters:
	 * 
	 * @param source
	 * @param destination
	 * @param distanceFunction
	 * @param maxWaySpeed
	 * @param movable
	 * @return
	 */
	public Double getTravelTime(Location source, Location destination, IDistanceFunction distanceFunction, Double maxWaySpeed,
			IVRPSimulationModelStructureElementWithStorageMovable movable, IClock clock);

}
