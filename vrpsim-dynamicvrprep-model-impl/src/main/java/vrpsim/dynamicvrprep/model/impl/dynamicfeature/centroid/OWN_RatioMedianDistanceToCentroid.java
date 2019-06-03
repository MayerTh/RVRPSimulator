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

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel.RequestType;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper.Point;
import vrpsim.feature.TSPFeatures;
import vrpsim.feature.model.TSPFeature;

/**
 * The Radius-Location based Degree of Dynanism from ? (Smith...)
 * 
 * Average distance from all requests to the centroid to the average distance of
 * dynamic requests to the centroid.
 * 
 * @author mayert
 *
 */
public class OWN_RatioMedianDistanceToCentroid extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(OWN_RatioMedianDistanceToCentroid.class);

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		TSPFeatures tspFeatures;
		Map<String, Double> result = new HashMap<>();
		try {
			tspFeatures = new TSPFeatures(TSPFeature.centroid_centroid_x, false);
			double x = tspFeatures.getFeature(model.getCoordinatesOfRequestsByType(RequestType.ALL))[0];
			double y = tspFeatures.getAdditionalFeature(TSPFeature.centroid_centroid_y)[0];

			DynamicModelDegreeCalculationHelper calcUtil = new DynamicModelDegreeCalculationHelper(model);
			Point mainCentroid = calcUtil.getPoint(x, y);

			double[] allDistance = new double[model.getVRPREPInstance().getRequests().getRequest().size()];
			for (int i = 0; i < model.getVRPREPInstance().getRequests().getRequest().size(); i++) {
				Request request = model.getVRPREPInstance().getRequests().getRequest().get(i);
				allDistance[i] = calcUtil.getDistance(mainCentroid, request);
			}

			double[] dynamicDistance = new double[model.getDynamicRequestInformation().keySet().size()];
			int insert = 0;
			for (BigInteger requestId : model.getDynamicRequestInformation().keySet()) {
				dynamicDistance[insert++] = calcUtil.getDistance(mainCentroid, requestId);
			}

			result.put(this.getIdentifier(), new Median().evaluate(dynamicDistance) / new Median().evaluate(allDistance));
			return result;

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.error("Can not calculate feature {} due to {} {}.", this.getIdentifier(), e.getCause(), e.getMessage());
		}
		result.put(this.getIdentifier(), Double.NaN);
		return result;
	}

}
