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
import java.util.List;

public class TmpRequests {

	private final List<TmpRequest> requests;
	private final double dynamicDistance;
	
	public TmpRequests(List<TmpRequest> requests) {
		this.requests = requests;
		double sum = 0.0;
		for (TmpRequest request : this.requests) {
			sum += request.getTotalDistanceToOthers();
		}
		this.dynamicDistance = sum;
	}
	
	public double calculateLDOD(double totalDistance) {
		return (this.getDynamicDistance()/totalDistance);
	}

	public double getDynamicDistance() {
		return this.dynamicDistance;
	}

	public List<TmpRequest> getRequests() {
		return requests;
	}

	public boolean equals(List<TmpRequest> oRrequests) {

		if (this.requests.size() == oRrequests.size()) {

			boolean equals = true;
			for (TmpRequest r : oRrequests) {
				BigInteger id = r.getId();
				if (!isIdInList(id)) {
					equals = false;
					break;
				}
			}

			return equals;

		} else {
			return false;
		}

	}

	private boolean isIdInList(BigInteger id) {
		for (TmpRequest request : this.requests) {
			if (request.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		double sum = 0.0;
		String ids = "";
		for(TmpRequest request : this.requests) {
			sum += request.getTotalDistanceToOthers();
			ids += request.getId() + ",";
		}
		
		return "number inside = " + this.requests.size() + " ids = " +ids+ " dynamic distance = " + sum;
	}

}
