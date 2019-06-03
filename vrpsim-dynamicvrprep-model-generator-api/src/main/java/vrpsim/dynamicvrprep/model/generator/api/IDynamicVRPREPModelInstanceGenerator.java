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

import java.util.Random;

import org.vrprep.model.instance.Instance;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.generator.api.IArrivalTimeDetermineStrategy.NotInitilizedException;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequests;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelStatisticsInformation;

public interface IDynamicVRPREPModelInstanceGenerator {

	public GeneratorModelStatisticsInformation initialize(Instance instance, Random random, double degreeOfDynanism,
			int numberOfCombinationsToGenerate) throws InstanceGenerationException;

	public DynamicVRPREPModel generateDynamicVRPREPModelInstance(GeneratorModelRequests requests, GeneratorModelStatisticsInformation statisticsInformation,
			IArrivalTimeDetermineStrategy arrivalTimeDetermineStrategy) throws InstanceGenerationException, NotInitilizedException;

	public GeneratorModelRequests generateRandomValidGeneratorModelRequests(GeneratorModelStatisticsInformation statisticsInformation);

}
