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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;
import org.xml.sax.SAXException;

@XmlRootElement(name = "dynamic-instance")
public class DynamicVRPREPModel implements Serializable {

	private static final long serialVersionUID = 1L;
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

	/**
	 * Returns the coordinates of the request. If flag onlyDynamic is true, only
	 * dynamic requests are considered.
	 * 
	 * The output form is: double[][] result = new double[numberCoords][2];
	 * 
	 * @param onlyDynamic
	 * @return
	 */
	@Deprecated
	public double[][] getCoordinatesOfRequests(boolean onlyDynamic) {
		Map<BigInteger, Node> nodes = new HashMap<>();
		for (Node node : this.getVRPREPInstance().getNetwork().getNodes().getNode()) {
			nodes.put(node.getId(), node);
		}

		int numberCoords = onlyDynamic ? this.getDynamicRequestInformation().size()
				: this.getVRPREPInstance().getRequests().getRequest().size();
		double[][] result = new double[numberCoords][2];
		int index = 0;
		for (int i = 0; i < this.getVRPREPInstance().getRequests().getRequest().size(); i++) {
			Request request = this.getVRPREPInstance().getRequests().getRequest().get(i);

			if (!onlyDynamic) {
				result[index][0] = nodes.get(request.getNode()).getCx();
				result[index][1] = nodes.get(request.getNode()).getCy();
				index++;
			} else {
				if (this.getDynamicRequestInformation().containsKey(request.getId())) {
					result[index][0] = nodes.get(request.getNode()).getCx();
					result[index][1] = nodes.get(request.getNode()).getCy();
					index++;
				}
			}
		}

		return result;
	}

	public double[][] getCoordinatesOfRequestsByType(RequestType type) {
		Map<BigInteger, Node> nodes = new HashMap<>();
		for (Node node : this.getVRPREPInstance().getNetwork().getNodes().getNode()) {
			nodes.put(node.getId(), node);
		}

		int numberCoords = 0;
		if (type.equals(RequestType.ONLY_DYNAMIC)) {
			numberCoords = this.getDynamicRequestInformation().size();
		} else if (type.equals(RequestType.ONLY_STATIC)) {
			numberCoords = this.getVRPREPInstance().getRequests().getRequest().size() - this.getDynamicRequestInformation().size();
		} else {
			numberCoords = this.getVRPREPInstance().getRequests().getRequest().size();
		}

		double[][] result = new double[numberCoords][2];
		int index = 0;
		for (int i = 0; i < this.getVRPREPInstance().getRequests().getRequest().size(); i++) {
			Request request = this.getVRPREPInstance().getRequests().getRequest().get(i);

			if (type.equals(RequestType.ONLY_DYNAMIC)) {
				if (this.getDynamicRequestInformation().containsKey(request.getId())) {
					result[index][0] = nodes.get(request.getNode()).getCx();
					result[index][1] = nodes.get(request.getNode()).getCy();
					index++;
				}
			} else if (type.equals(RequestType.ONLY_STATIC)) {
				if (!this.getDynamicRequestInformation().containsKey(request.getId())) {
					result[index][0] = nodes.get(request.getNode()).getCx();
					result[index][1] = nodes.get(request.getNode()).getCy();
					index++;
				}
			} else {
				result[index][0] = nodes.get(request.getNode()).getCx();
				result[index][1] = nodes.get(request.getNode()).getCy();
				index++;
			}
		}

		return result;
	}

	public String getModelAsXML() throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DynamicVRPREPModel.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this, sw);
		return sw.toString();
	}

	public static synchronized File write(DynamicVRPREPModel dynamicVRPREPModel, Path outputPath) throws JAXBException, SAXException {

		outputPath.getParent().toFile().mkdirs();
		JAXBContext context = JAXBContext.newInstance(DynamicVRPREPModel.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(dynamicVRPREPModel, outputPath.toFile());
		return outputPath.toFile();

		// InputStream stream = Instance.class.getResourceAsStream("/xsd/instance.xsd");
		// Source schemaSource = new StreamSource(stream);
		// SchemaFactory sf =
		// SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Schema schema = sf.newSchema(schemaSource);
		//
		// JAXBContext jc = JAXBContext.newInstance(DynamicVRPREPModel.class);
		// Marshaller marshaller = jc.createMarshaller();
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// marshaller.setSchema(schema);
		// marshaller.setEventHandler(new ValidationEventHandler() {
		// public boolean handleEvent(ValidationEvent event) {
		// System.err.println("MESSAGE: " + event.getMessage());
		// return true;
		// }
		// });
		// marshaller.marshal(dynamicVRPREPModel, outputPath.toFile());

	}

	public static synchronized DynamicVRPREPModel load(String instanceXML, String uniqueId)
			throws JAXBException, IOException, XMLStreamException, FactoryConfigurationError {
		InputStream stream = new ByteArrayInputStream(instanceXML.getBytes(StandardCharsets.UTF_8));
		XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		JAXBContext jc = JAXBContext.newInstance(DynamicVRPREPModel.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		DynamicVRPREPModel result = (DynamicVRPREPModel) unmarshaller.unmarshal(xmlStreamReader);
		return result;
	}

	public static synchronized DynamicVRPREPModel load(String folderName, String modelName, String uniqueId)
			throws IOException, JAXBException, XMLStreamException, FactoryConfigurationError, SAXException {
		try {
			BufferedReader bw = new BufferedReader(new FileReader(new File(folderName + File.separatorChar + modelName)));
			String content = "";
			String line = bw.readLine();
			while (line != null) {
				content += line;
				line = bw.readLine();
			}
			bw.close();
			DynamicVRPREPModel dynamicVRPREPModel = DynamicVRPREPModel.load(content, uniqueId);
			return dynamicVRPREPModel;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public enum RequestType {
		ONLY_DYNAMIC, ONLY_STATIC, ALL;
	}

}
