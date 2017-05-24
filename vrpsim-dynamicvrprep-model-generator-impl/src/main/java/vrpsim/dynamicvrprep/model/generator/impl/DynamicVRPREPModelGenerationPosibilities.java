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
package vrpsim.dynamicvrprep.model.generator.impl;

import java.util.List;

import org.vrprep.model.instance.Instance;

import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequest;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequests;

public class DynamicVRPREPModelGenerationPosibilities implements IDynamicVRPREPModelGenerationPosibilities {

	private double totalDistance;
	private List<TmpRequests> combinations;
	private List<TmpRequest> allRequests;
	private TmpRequests smallest;
	private TmpRequests biggest;
	private Instance staticInstance;
	private RunInfo runInfo;
	private int numberOfDynamicRequests;
	private double degreeOfDynanism;

	public double getDegreeOfDynanism() {
		return degreeOfDynanism;
	}

	public void setDegreeOfDynanism(double degreeOfDynanism) {
		this.degreeOfDynanism = degreeOfDynanism;
	}

	public int getNumberOfDynamicRequests() {
		return numberOfDynamicRequests;
	}

	public void setNumberOfDynamicRequests(int numberOfDynamicRequests) {
		this.numberOfDynamicRequests = numberOfDynamicRequests;
	}

	public int getNumberOfAllRequests() {
		return allRequests == null ? 0 : allRequests.size();
	}

	public RunInfo getRunInfo() {
		return runInfo;
	}

	public void setRunInfo(RunInfo runInfo) {
		this.runInfo = runInfo;
	}

	public Instance getStaticInstance() {
		return staticInstance;
	}

	public void setStaticInstance(Instance staticInstance) {
		this.staticInstance = staticInstance;
	}

	public TmpRequests getSmallest() {
		return smallest;
	}

	public void setSmallest(TmpRequests smallest) {
		this.smallest = smallest;
	}

	public TmpRequests getBiggest() {
		return biggest;
	}

	public void setBiggest(TmpRequests biggest) {
		this.biggest = biggest;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public List<TmpRequest> getAllRequests() {
		return allRequests;
	}

	public void setAllTmpRequests(List<TmpRequest> allRequests) {
		this.allRequests = allRequests;
	}

	public void setCombinations(List<TmpRequests> combinations) {
		this.combinations = combinations;
	}

	public void setDynamicCombinationsSorted(List<TmpRequests> combinations) {
		this.combinations = combinations;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public List<TmpRequests> getCombinations() {
		return combinations;
	}
}
