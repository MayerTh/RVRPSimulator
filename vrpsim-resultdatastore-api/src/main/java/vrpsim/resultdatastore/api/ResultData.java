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

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.vrprep.model.instance.Instance;
import org.xml.sax.SAXException;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

@XmlRootElement
public class ResultData {

	private String algorithm;
	private Double result;
	private String instanceIdentifier;
	private DynamicVRPREPModel model;

	public ResultData(DynamicVRPREPModel model, String instanceIdentifier, String algorithm, Double result) {
		super();
		this.model = model;
		this.algorithm = algorithm;
		this.result = result;
		this.instanceIdentifier = instanceIdentifier;
	}

	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(String instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public DynamicVRPREPModel getModel() {
		return model;
	}

	public void setModel(DynamicVRPREPModel model) {
		this.model = model;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public File writeToFile(Path outputPath) throws JAXBException, SAXException {
		outputPath.getParent().toFile().mkdirs();

		InputStream stream = Instance.class.getResourceAsStream("/xsd/instance.xsd");
		Source schemaSource = new StreamSource(stream);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(schemaSource);

		JAXBContext jc = JAXBContext.newInstance(ResultData.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setSchema(schema);
		marshaller.setEventHandler(new ValidationEventHandler() {
			public boolean handleEvent(ValidationEvent event) {
				System.err.println("MESSAGE:  " + event.getMessage());
				return true;
			}
		});
		marshaller.marshal(this, outputPath.toFile());
		return outputPath.toFile();
	}

}
