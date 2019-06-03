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
package vrpsim.resultdatastore.fileimpl;

import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.resultdatastore.api.IResultDataStoreAPI;
import vrpsim.resultdatastore.api.PersistenceUnit;
import vrpsim.resultdatastore.api.PureResultData;
import vrpsim.resultdatastore.api.ResultData;
import vrpsim.resultdatastore.api.ResultDataStoreAPIException;

public class ResultDataStoreFileImpl implements IResultDataStoreAPI {

	private static Logger logger = LoggerFactory.getLogger(ResultDataStoreFileImpl.class);

	@Override
	public Long storeResultData(String exportFolder, String fileName, DynamicVRPREPModel model, String instanceIdentifier, String algorithm,
			String objective, boolean searchInNaturalOrder, Double dynamicResult, Double dynamicResultReverse, Double staticResult,
			Double dod, PersistenceUnit pUnit) {

		ResultData resultData = new ResultData(model, instanceIdentifier, algorithm, objective, searchInNaturalOrder, dynamicResult,
				dynamicResultReverse, staticResult, dod);
		try {
			resultData.writeToFile(Paths.get(exportFolder, fileName));
		} catch (JAXBException | SAXException e) {
			logger.error("Can not write ResultData due to exception {}", e.getMessage());
			e.printStackTrace();
		}
		
		return 0L;
	}

	@Override
	public void storePureResultData(String exportFolder, String fileName, Long dbResultDataId, String initialSolutionAlgorithm,
			String dynamicSolutionAlgorithm, double dynamicResult, double dynamicResultReverse, long seed, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {

		PureResultData prd = new PureResultData(dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm, dynamicResult,
				dynamicResultReverse, seed);

		try {
			prd.writeToFile(Paths.get(exportFolder, fileName));
		} catch (JAXBException | SAXException e) {
			logger.error("Can not write PureResultData due to exception {}", e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, PersistenceUnit pUnit) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ResultData getMinCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ResultData getMaxCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ResultData getResultDataByDBResultDataId(Long id, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, Double dod,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<PureResultData> getPureResultData(Long dbResultDataId, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			Double dod, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<ResultData> getResultDataSortedNotCalculated(String instanceIdentifier, String algorithm, String objective, Double dod,
			String algorithmToCalculate, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		throw new RuntimeException("Not implemented.");
	}

}
