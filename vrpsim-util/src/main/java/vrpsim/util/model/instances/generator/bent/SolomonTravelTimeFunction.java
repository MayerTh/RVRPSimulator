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
package vrpsim.util.model.instances.generator.bent;

import vrpsim.core.model.behaviour.activities.util.TimeCalculationInformationContainer;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.simulator.IClock;

public class SolomonTravelTimeFunction implements ITimeFunction {

	@Override
	public Double getTime(TimeCalculationInformationContainer container, IClock clock) {
		
		/*
		 * Since Solomon 1987 (ALGORITHMS FOR THE VEHICLE ROUTING
		 * AND SCHEDULING PROBLEMS WITH TIME WINDOW CONSTRAINT),
		 * where the instances original coming from, travel time is
		 * equal to the distance between customers: "All the test
		 * problems are 100-customer euclidean problems. This
		 * problem size is not limiting for the methods presented,
		 * since much larger problems could be solved. Travel times
		 * between customers are taken to equal the corresponding
		 * distances."
		 */
		
		return container.getDistanceFunction().getDistance(container.getSource(), container.getTarget());
	}

}
