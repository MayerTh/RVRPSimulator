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
package vrpsim.dynamicvrprep.model.api;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

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

@XmlRootElement
public class DynamicVRPREPModel {

	private final long id;
	private long planningTimeHorizon;
	private Map<BigInteger, DynamicRequestInformation> dynamicRequestInformation;
	private Instance instance;

	public DynamicVRPREPModel() {
		this.id = UUID.randomUUID().getMostSignificantBits();
	}

	public long getPlanningTimeHorizon() {
		return this.planningTimeHorizon;
	}

	public void setPlanningTimeHorizon(long planningTimeHorizon) {
		this.planningTimeHorizon = planningTimeHorizon;
	}

	public Instance getVRPREPInstance() {
		return this.instance;
	}

	public void setVRPREPInstance(Instance instance) {
		this.instance = instance;
	}

	public Map<BigInteger, DynamicRequestInformation> getDynamicRequestInformation() {
		return this.dynamicRequestInformation;
	}

	public void setDynamicRequestInformation(Map<BigInteger, DynamicRequestInformation> dynamicRequestInformation) {
		this.dynamicRequestInformation = dynamicRequestInformation;
	}

	public long getId() {
		return this.id;
	}
	
	public static File write(DynamicVRPREPModel dynamicVRPREPModel, Path outputPath) throws JAXBException, SAXException {

		outputPath.getParent().toFile().mkdirs();

		InputStream stream = Instance.class.getResourceAsStream("/xsd/instance.xsd");
		Source schemaSource = new StreamSource(stream);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(schemaSource);

		JAXBContext jc = JAXBContext.newInstance(DynamicVRPREPModel.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setSchema(schema);
		marshaller.setEventHandler(new ValidationEventHandler() {
			public boolean handleEvent(ValidationEvent event) {
				System.err.println("MESSAGE:  " + event.getMessage());
				return true;
			}
		});
		marshaller.marshal(dynamicVRPREPModel, outputPath.toFile());

		return outputPath.toFile();

	}


}
