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
package vrpsim.dynamicvrprep.model.generator.api.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.generator.api.model.util.GeneratorModelUtil;

public class GeneratorModelRequest {

	private final double x;
	private final double y;
	private final BigInteger id;
	
	private final double totalDistanceToOthers;
	private final Map<BigInteger, Double> distancesToOtherRequests;

	private int time = 0;

	public GeneratorModelRequest(BigInteger id, double x, double y, Instance instance, GeneratorModelUtil instanceGeneratorUtil) {

		this.id = id;
		this.x = x;
		this.y = y;

		double totalDistance = 0.0;
		Map<BigInteger, Double> distancesToOthers = new HashMap<>();
		for (Request r2 : instance.getRequests().getRequest()) {
			if (!r2.getId().equals(this.id)) {
				double distanceR1R2 = instanceGeneratorUtil.getDistance(this.x, this.y, r2);
				totalDistance += distanceR1R2;
				distancesToOthers.put(r2.getId(), new Double(distanceR1R2));
			}
		}

		this.totalDistanceToOthers = totalDistance;
		this.distancesToOtherRequests = distancesToOthers;
	}

	public GeneratorModelRequest(BigInteger id, double x, double y, Instance instance) {
		this(id, x, y, instance, new GeneratorModelUtil(instance));
	}

	/**
	 * Returns the distances to other requests. 
	 * 
	 * @return
	 */
	public Map<BigInteger, Double> getDistancesToOtherRequests() {
		return distancesToOtherRequests;
	}

	/**
	 * X coordinate of the location of the request.
	 * 
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * Y coordinate of the location of the request.
	 * 
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the total distance to all other requests. 
	 * 
	 * @return
	 */
	public double getTotalDistanceToOthers() {
		return totalDistanceToOthers;
	}

	/**
	 * Returns the request identifier. 
	 * 
	 * @return
	 */
	public BigInteger getId() {
		return id;
	}

	/**
	 * Returns the time the dynamic request occurs.
	 * 
	 * @return
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Sets the time the dynamic request occurs.
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof GeneratorModelRequest) {
			result = ((GeneratorModelRequest) obj).getId().equals(this.id);
		}
		return result;
	}

	@Override
	public String toString() {
		return "[id=" + this.id + ",distance=" + this.totalDistanceToOthers + ",time=" + this.time + "]";
	}

}
