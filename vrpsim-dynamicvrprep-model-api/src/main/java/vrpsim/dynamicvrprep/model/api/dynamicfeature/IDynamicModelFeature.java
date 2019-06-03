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
package vrpsim.dynamicvrprep.model.api.dynamicfeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public interface IDynamicModelFeature {

	public String getIdentifier();
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model);
	public double getFirstValue(Map<String, Double> features);

	public static List<IDynamicModelFeature> loadAll() throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections("vrpsim");
		Set<Class<? extends IDynamicModelFeature>> allClasses = reflections.getSubTypesOf(IDynamicModelFeature.class);
		List<IDynamicModelFeature> result = new ArrayList<>();
		if (allClasses.size() > 0) {
			for (Class<? extends IDynamicModelFeature> clazz : allClasses) {
				result.add(clazz.newInstance());
			}
		}
		return result;
	}

}
