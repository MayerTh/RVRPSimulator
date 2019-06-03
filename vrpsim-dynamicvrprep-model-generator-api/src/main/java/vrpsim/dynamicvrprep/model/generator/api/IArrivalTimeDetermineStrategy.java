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
package vrpsim.dynamicvrprep.model.generator.api;

import java.util.Random;

import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequest;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequests;

public interface IArrivalTimeDetermineStrategy {

	/**
	 * Init the strategy.
	 * 
	 * @param edod
	 * @param timeHorizon
	 */
	public void init(double edod, int timeHorizon);

	/**
	 * Has to set arrival times for all dynamic requests.
	 * 
	 * Get dynamic requests: {@link GeneratorModelRequests#getDynamicRequests()} Set
	 * arrival time: {@link GeneratorModelRequest#setTime(int)}
	 * 
	 * @param random
	 * @param allRequests
	 */
	public void determineArrivalTimes(Random random, GeneratorModelRequests allRequests) throws NotInitilizedException;

	public double getEDOD();
	public int getTimeHorizon();
	
	/**
	 * Should be thrown if not initialized.
	 * 
	 * @author mayert
	 */
	public class NotInitilizedException extends Exception {

		private static final long serialVersionUID = 1L;

		public NotInitilizedException(String msg) {
			super(msg);
		}

	}
}
