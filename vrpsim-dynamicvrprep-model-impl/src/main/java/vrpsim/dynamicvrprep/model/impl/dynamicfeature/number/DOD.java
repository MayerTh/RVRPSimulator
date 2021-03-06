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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.number;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;

/**
 * The Degree of Dynanism from Lund, K., Madsen, O. B. & Rygaard, J. M. (1996),
 * �Vehicle routing with va- rying degree of dynamism.�
 * 
 * @author mayert
 */
public class DOD extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(DOD.class);

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {
		Map<String, Double> result = new HashMap<>();
		logger.trace("Dynamic requests number = {}, request number = {}", model.getDynamicRequestInformation().size(),
				model.getVRPREPInstance().getRequests().getRequest().size());
		double value =  new Double(model.getDynamicRequestInformation().size())
				/ new Double(model.getVRPREPInstance().getRequests().getRequest().size());
		result.put(this.getIdentifier(), value);
		return result;
	}

}
