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
package vrpsim.util.model.generator.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.util.functions.Euclidean2DDistanceFunction;
import vrpsim.core.model.util.functions.IDistanceFunction;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.model.util.functions.LinearMotionTravelTimeFunction;
import vrpsim.util.model.generator.GeneratorConfigurationInitializationException;

public class RandomNetworkGeneratorConfiguration {

	private static Logger logger = LoggerFactory.getLogger(RandomNetworkGeneratorConfiguration.class);

	private Integer numberNodes;
	private IDistanceFunction distanceFunction;
	private ITimeFunction timeFunction;
	private Double maxWaySpeed;

	public void initialize() throws GeneratorConfigurationInitializationException {
		
		// Number nodes.
		logger.info((this.numberNodes == null) ? "No number of nodes set, default value is 10."
				: ("Number of nodes is set to " + this.numberNodes));
		this.numberNodes = (this.numberNodes == null) ? new Integer(10) : this.numberNodes;
		
		// Distance function.
		logger.info((this.distanceFunction == null) ? "No distance function set, default value is Euclidean2DDistanceFunction."
				: ("Distance funtcion set to " + this.distanceFunction.getClass().getSimpleName()));
		this.distanceFunction = (this.distanceFunction == null) ? new Euclidean2DDistanceFunction() : this.distanceFunction;
		
		// Time function
		logger.info((this.timeFunction == null) ? "No time function set, default value is LinearMotionTravelTimeFunction."
				: ("Time funtcion set to " + this.timeFunction.getClass().getSimpleName()));
		this.timeFunction = (this.timeFunction == null) ? new LinearMotionTravelTimeFunction() : this.timeFunction;
		
		// Max way speed
		logger.info((this.maxWaySpeed == null) ? "No max way speed set, default value is 50."
				: ("Max way speed set to " + this.maxWaySpeed.getClass().getSimpleName()));
		this.maxWaySpeed = (this.maxWaySpeed == null) ? new Double(50) : this.maxWaySpeed;
	}

	/**
	 * If not set default value is 10.
	 * 
	 * @param numberNodes
	 * @return
	 */
	public RandomNetworkGeneratorConfiguration setNumberNodes(Integer numberNodes) {
		this.numberNodes = numberNodes;
		return this;
	}

	/**
	 * If not set default value is {@link Euclidean2DDistanceFunction}.
	 * 
	 * @param distanceFunction
	 * @return
	 */
	public RandomNetworkGeneratorConfiguration setDistanceFunction(IDistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
		return this;
	}

	/**
	 * If not set default value is {@link LinearMotionTravelTimeFunction}.
	 * 
	 * @param timeFunction
	 * @return
	 */
	public RandomNetworkGeneratorConfiguration setTimeFunction(ITimeFunction timeFunction) {
		this.timeFunction = timeFunction;
		return this;
	}

	/**
	 * If not set default value is 50.
	 * 
	 * @param maxWaySpeed
	 * @return
	 */
	public RandomNetworkGeneratorConfiguration setMaxWaySpeed(Double maxWaySpeed) {
		this.maxWaySpeed = maxWaySpeed;
		return this;
	}

	public Integer getNumberNodes() {
		return numberNodes;
	}

	public IDistanceFunction getDistanceFunction() {
		return distanceFunction;
	}

	public ITimeFunction getTimeFunction() {
		return timeFunction;
	}

	public Double getMaxWaySpeed() {
		return maxWaySpeed;
	}
	
}
