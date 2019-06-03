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
package vrpsim.resultdatastore.api;

import java.util.List;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public interface IResultDataStoreAPI {

	public void storePureResultData(String exportFolder, String fileName, Long dbResultDataId, String initialSolutionAlgorithm,
			String dynamicSolutionAlgorithm, double dynamicResult, double dynamicResultReverse, long seed, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException;

	public Long storeResultData(String exportFolder, String fileName, DynamicVRPREPModel model, String instanceIdentifier, String algorithm,
			String objective, boolean searchInNaturalOrder, Double dynamicResult, Double dynamicResultReverse, Double staticResult,
			Double dod, PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public List<PureResultData> getPureResultData(Long dbResultDataId, PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			Double dod, PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException;

	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, Double dod,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public List<ResultData> getResultDataSortedNotCalculated(String instanceIdentifier, String algorithm, String objective, Double dod,
			String algorithmToCalculate, PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public ResultData getResultDataByDBResultDataId(Long id, PersistenceUnit pUnit) throws ResultDataStoreAPIException;

	public ResultData getMinCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException;

	public ResultData getMaxCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException;

}
