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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.area;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;

/**
 * The area covered from all dynamic requests to the area covered by all
 * requests.
 * 
 * @author mayert
 *
 */
public class OWN_RatioCoveredArea extends ADynamicModelFeature {

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {
		Map<String, Double> result = new HashMap<>();
		
		double minX_dynamic = Double.MAX_VALUE;
		double minY_dynamic = Double.MAX_VALUE;
		double maxX_dynamic = Double.MIN_VALUE;
		double maxY_dynamic = Double.MIN_VALUE;

		double minX_all = Double.MAX_VALUE;
		double minY_all = Double.MAX_VALUE;
		double maxX_all = Double.MIN_VALUE;
		double maxY_all = Double.MIN_VALUE;

		DynamicModelDegreeCalculationHelper calcUtil = new DynamicModelDegreeCalculationHelper(model);
		for (BigInteger idR : model.getDynamicRequestInformation().keySet()) {
			double x = calcUtil.getNode(calcUtil.getRequest(idR).getNode()).getCx();
			double y = calcUtil.getNode(calcUtil.getRequest(idR).getNode()).getCy();
			minX_dynamic = Double.min(minX_dynamic, x);
			maxX_dynamic = Double.max(maxX_dynamic, x);
			minY_dynamic = Double.min(minY_dynamic, y);
			maxY_dynamic = Double.max(maxY_dynamic, y);
		}

		for (Request request : model.getVRPREPInstance().getRequests().getRequest()) {
			BigInteger idR = request.getId();
			double x = calcUtil.getNode(calcUtil.getRequest(idR).getNode()).getCx();
			double y = calcUtil.getNode(calcUtil.getRequest(idR).getNode()).getCy();
			minX_all = Double.min(minX_all, x);
			maxX_all = Double.max(maxX_all, x);
			minY_all = Double.min(minY_all, y);
			maxY_all = Double.max(maxY_all, y);
		}

		double a_dynamic = Math.abs(maxX_dynamic - minX_dynamic);
		double b_dynamic = Math.abs(maxY_dynamic - minY_dynamic);
		double area_dynamic = a_dynamic * b_dynamic;

		double a_all = Math.abs(maxX_all - minX_all);
		double b_all = Math.abs(maxY_all - minY_all);
		double area_all = a_all * b_all;
		
		result.put(this.getIdentifier(), area_dynamic / area_all);
		return result;
	}

}
