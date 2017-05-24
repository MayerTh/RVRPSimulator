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

import java.math.BigInteger;
import java.util.Map;

public class TmpRequest {

	private final double x;
	private final double y;
	private final BigInteger id;
	private final double totalDistanceToOthers;
	private int time = 0;

	private final Map<BigInteger, Double> distancesToOtherRequests;

	public TmpRequest(BigInteger id, double totalDistance, double x, double y, Map<BigInteger, Double> distancesToOtherRequests) {
		super();
		this.id = id;
		this.totalDistanceToOthers = totalDistance;
		this.x = x;
		this.y = y;
		this.distancesToOtherRequests = distancesToOtherRequests;
	}

	public Map<BigInteger, Double> getDistancesToOtherRequests() {
		return distancesToOtherRequests;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getTotalDistanceToOthers() {
		return totalDistanceToOthers;
	}

	public BigInteger getId() {
		return id;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof TmpRequest) {
			result = ((TmpRequest) obj).getId().equals(this.id);
		}
		return result;
	}

	@Override
	public String toString() {
		return "[id=" + this.id + ",distance=" + this.totalDistanceToOthers + ",time=" + this.time + "]";
	}

}
