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

import java.util.List;
import java.util.Random;

import org.vrprep.model.instance.Instance;

public class GeneratorModelStatisticsInformation {

	private final Random random;

	private final Instance instance;
	private final List<GeneratorModelRequest> allRequests;
	private final double degreeOfDynanism;
	private final int numberOfDynamicRequests;

	@Deprecated
	private List<GeneratorModelRequests> possibleCombinations;
	@Deprecated
	private double totalDistance;
	@Deprecated
	private GeneratorModelRequests smallest;
	@Deprecated
	private GeneratorModelRequests biggest;

	public GeneratorModelStatisticsInformation(Instance instance, Random random, List<GeneratorModelRequest> allRequests, double degreeOfDynanism, int numberOfDynamicRequests) {
		this.instance = instance;
		this.random = random;
		this.allRequests = allRequests;
		this.degreeOfDynanism = degreeOfDynanism;
		this.numberOfDynamicRequests = numberOfDynamicRequests;
	}

	public double getDegreeOfDynanism() {
		return degreeOfDynanism;
	}

	public Random getRandom() {
		return random;
	}

	public int getNumberOfDynamicRequests() {
		return numberOfDynamicRequests;
	}

	public int getNumberOfAllRequests() {
		return allRequests == null ? 0 : allRequests.size();
	}

	public Instance getInstance() {
		return instance;
	}

	public List<GeneratorModelRequest> getAllRequests() {
		return allRequests;
	}

	@Deprecated
	public GeneratorModelRequests getSmallest() {
		return smallest;
	}

	@Deprecated
	public void setSmallest(GeneratorModelRequests smallest) {
		this.smallest = smallest;
	}

	@Deprecated
	public GeneratorModelRequests getBiggest() {
		return biggest;
	}

	@Deprecated
	public void setBiggest(GeneratorModelRequests biggest) {
		this.biggest = biggest;
	}

	@Deprecated
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	@Deprecated
	public void setCombinations(List<GeneratorModelRequests> combinations) {
		this.possibleCombinations = combinations;
	}

	@Deprecated
	public void setDynamicCombinationsSorted(List<GeneratorModelRequests> combinations) {
		this.possibleCombinations = combinations;
	}

	@Deprecated
	public double getTotalDistance() {
		return totalDistance;
	}

	@Deprecated
	public List<GeneratorModelRequests> getCombinations() {
		return possibleCombinations;
	}

}
