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

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.resultdatastore.api.IResultDataStoreAPI;
import vrpsim.resultdatastore.api.ResultData;

public class ResultDataStoreFileImpl implements IResultDataStoreAPI {

	private static Logger logger = LoggerFactory.getLogger(ResultDataStoreFileImpl.class);
	
	@Override
	public void toreResultData(String exportFolder, String fileName, DynamicVRPREPModel model, String instanceIdentifier, String algorithm, Double result) {
		ResultData resultData = new ResultData(model, instanceIdentifier, algorithm, result);
		try {
			resultData.writeToFile(Paths.get(exportFolder, fileName));
		} catch (JAXBException | SAXException e) {
			logger.error("Can not write ResultData due to exception {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
