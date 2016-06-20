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

public class LinearMotionTravelTimeFunction implements ITimeFunction {

	@Override
	public Double getTravelTime(Location source, Location target, IDistanceFunction distanceFunction, Double maxWaySpeed,
			IVRPSimulationModelStructureElementWithStorageMovable movable, IClock clock) {
		double distance = distanceFunction.getDistance(source, target);
		double speed = Math.min(maxWaySpeed, movable.getAverageSpeed());
		return this.getTravelTime(distance, speed);
	}

	private Double getTravelTime(Double distance, Double speed) {
		return speed / distance;
	}

}
