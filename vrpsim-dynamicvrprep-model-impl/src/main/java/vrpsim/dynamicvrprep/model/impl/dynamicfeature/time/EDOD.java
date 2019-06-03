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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.time;

import java.util.HashMap;
import java.util.Map;

import vrpsim.dynamicvrprep.model.api.DynamicRequestInformation;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;

/**
 * The Effective Degree of Dynanism from Larsen, A. (2000), The dynamic vehicle
 * routing problem, PhD thesis, Technical University of Denmark.
 * 
 * @author mayert
 */
public class EDOD extends ADynamicModelFeature {

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {
		Map<String, Double> result = new HashMap<>();
		double sum = 0;
		for (DynamicRequestInformation dri : model.getDynamicRequestInformation().values()) {
			if (dri.getArrivalTime() > 0) {
				double value = new Double(dri.getArrivalTime()) / model.getPlanningTimeHorizon();
				sum += value;
			}
		}

		double value = Double.NaN;
		if (sum > 0) {
			value = sum / model.getVRPREPInstance().getRequests().getRequest().size();
		}
		
		result.put(this.getIdentifier(), value);
		return result;
	}

}
