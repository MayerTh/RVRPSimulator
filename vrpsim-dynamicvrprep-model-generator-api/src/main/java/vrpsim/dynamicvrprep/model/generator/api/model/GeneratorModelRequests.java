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
import java.util.List;

public class GeneratorModelRequests {

	private final List<GeneratorModelRequest> dynamicRequests;
	private final List<GeneratorModelRequest> allRequests;
	
	private final double dynamicDistance;
	
	public GeneratorModelRequests(List<GeneratorModelRequest> dynamicRequests, List<GeneratorModelRequest> allRequests) {
		this.dynamicRequests = dynamicRequests;
		this.allRequests = allRequests;
		double sum = 0.0;
		for (GeneratorModelRequest request : this.dynamicRequests) {
			sum += request.getTotalDistanceToOthers();
		}
		this.dynamicDistance = sum;
	}
	
	public List<GeneratorModelRequest> getAllRequests() {
		return allRequests;
	}

	public double calculateLDOD(double totalDistance) {
		return (this.getDynamicDistance()/totalDistance);
	}

	private double getDynamicDistance() {
		return this.dynamicDistance;
	}

	public List<GeneratorModelRequest> getDynamicRequests() {
		return dynamicRequests;
	}

	public boolean equals(List<GeneratorModelRequest> oRrequests) {

		if (this.dynamicRequests.size() == oRrequests.size()) {

			boolean equals = true;
			for (GeneratorModelRequest r : oRrequests) {
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
		for (GeneratorModelRequest request : this.dynamicRequests) {
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
		for(GeneratorModelRequest request : this.dynamicRequests) {
			sum += request.getTotalDistanceToOthers();
			ids += request.getId() + ",";
		}
		
		return "number inside = " + this.dynamicRequests.size() + " ids = " +ids+ " dynamic distance = " + sum;
	}

}
