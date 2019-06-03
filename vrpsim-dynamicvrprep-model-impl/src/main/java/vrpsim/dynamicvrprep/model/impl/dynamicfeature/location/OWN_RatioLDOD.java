/**
 * Copyright Â© 2016 Thomas Mayer (thomas.mayer@unibw.de)
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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.location;

import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;

/**
 * The Location based Degree of Dynanism from Mayer, T., Uhlig, T. & Rose, o.
 * (2017), ‘A Location Model for Dynamic Vehicle Routing Problems.’
 * 
 * @author mayert
 *
 */
public class OWN_RatioLDOD extends ADynamicModelFeature {

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		Map<String, Double> result = new HashMap<>();
		DynamicModelDegreeCalculationHelper calcUtil = new DynamicModelDegreeCalculationHelper(model);
		double totalDistance = 0.0;
		double dynamicDistance = 0.0;

		for (Request r1 : model.getVRPREPInstance().getRequests().getRequest()) {
			double distanceR1 = 0.0;
			for (Request r2 : model.getVRPREPInstance().getRequests().getRequest()) {
				distanceR1 += calcUtil.getDistance(r1.getId(), r2.getId());
			}
			totalDistance += distanceR1;
			if (model.getDynamicRequestInformation().containsKey(r1.getId())) {
				dynamicDistance += distanceR1;
			}
		}

		result.put(this.getIdentifier(), dynamicDistance / totalDistance);
		return result;
	}

}
