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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.centroid;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper.Point;
import vrpsim.feature.TSPFeatures;
import vrpsim.feature.model.TSPFeature;

/**
 * The Radius-Location based Degree of Dynanism from ? (Smith...)
 * 
 * Sum distance from all requests to the centroid to the sum distance of dynamic
 * requests to the centroid.
 * 
 * @author mayert
 *
 */
public class OWN_RatioSumDistanceToCentroid extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(OWN_RatioSumDistanceToCentroid.class);

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		TSPFeatures tspFeatures;
		Map<String, Double> result = new HashMap<>();
		try {
			tspFeatures = new TSPFeatures(TSPFeature.centroid_centroid_x, false);
			double x = tspFeatures.getFeature(model.getCoordinatesOfRequests(false))[0];
			double y = tspFeatures.getAdditionalFeature(TSPFeature.centroid_centroid_y)[0];

			DynamicModelDegreeCalculationHelper calcUtil = new DynamicModelDegreeCalculationHelper(model);
			Point mainCentroid = calcUtil.getPoint(x, y);

			double allDistance = 0.0;
			for (Request request : model.getVRPREPInstance().getRequests().getRequest()) {
				allDistance += calcUtil.getDistance(mainCentroid, request);
			}

			double dynamicDistance = 0.0;
			for (BigInteger requestId : model.getDynamicRequestInformation().keySet()) {
				dynamicDistance += calcUtil.getDistance(mainCentroid, requestId);
			}
			
			result.put(this.getIdentifier(), dynamicDistance / allDistance);
			return result;

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.error("Can not calculate feature {} due to {} {}.", this.getIdentifier(), e.getCause(), e.getMessage());
		}
		result.put(this.getIdentifier(), Double.NaN);
		return result;
	}

}
