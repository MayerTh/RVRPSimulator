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
package vrpsim.core.model.util.functions;

import vrpsim.core.model.behaviour.activities.util.TimeCalculationInformationContainer;
import vrpsim.core.simulator.IClock;

public class LinearMotionTravelTimeFunction implements ITimeFunction {

	private Double getTravelTime(Double distance, Double speed) {
		return speed / distance;
	}

	@Override
	public Double getTime(TimeCalculationInformationContainer container, IClock clock) {
		double distance = container.getDistanceFunction().getDistance(container.getSource(), container.getTarget());
		double speed = Math.min(container.getMaxWaySpeed(), container.getVehicle().getAverageSpeed());
		return this.getTravelTime(distance, speed);
	}

}
