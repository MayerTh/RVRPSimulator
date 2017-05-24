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

import java.util.List;

import org.vrprep.model.instance.Instance;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public interface IDynamicVRPREPModelGenerationPosibilities {

	public double getDegreeOfDynanism();

	public void setDegreeOfDynanism(double degreeOfDynanism);
	
	public int getNumberOfDynamicRequests();

	public void setNumberOfDynamicRequests(int numberOfDynamicRequests);

	public int getNumberOfAllRequests();

	public void setAllTmpRequests(List<TmpRequest> allRequests);

	public void setTotalDistance(double totalDistance);

	public void setDynamicCombinationsSorted(List<TmpRequests> combinations);

	public double getTotalDistance();

	public List<TmpRequests> getCombinations();

	public List<TmpRequest> getAllRequests();

	public TmpRequests getSmallest();

	public void setSmallest(TmpRequests smallest);

	public TmpRequests getBiggest();

	public void setBiggest(TmpRequests biggest);

	public Instance getStaticInstance();

	public void setStaticInstance(Instance staticInstance);

	public RunInfo getRunInfo();

	public void setRunInfo(RunInfo runInfo);

	public static class RunInfo {

		private final double dod;
		private final double edod;
		private final int numberInstances;
		private final String instanceName;
		private final DynamicVRPREPModel model;

		public RunInfo(double dod, double edod, int numberInstances, String instanceName, DynamicVRPREPModel model) {
			super();
			this.dod = dod;
			this.edod = edod;
			this.numberInstances = numberInstances;
			this.model = model;
			this.instanceName = instanceName;
		}

		public String getInstanceName() {
			return instanceName;
		}

		public double getDod() {
			return dod;
		}

		public double getEdod() {
			return edod;
		}

		public DynamicVRPREPModel getModel() {
			return model;
		}

		public int getNumberInstances() {
			return numberInstances;
		}

	}

}
