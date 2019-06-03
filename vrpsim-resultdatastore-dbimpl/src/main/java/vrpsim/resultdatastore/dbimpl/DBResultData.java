package vrpsim.resultdatastore.dbimpl;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.resultdatastore.api.ResultData;

@Entity
public class DBResultData implements Serializable {

	@Transient
	private static final long serialVersionUID = -7005637795958559782L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String instanceIdentifier;
	private String algorithm;
	private String objective;
	private Double dynamicResult;
	private Double dynamicResultReverse;
	private Double staticResult;
	private Double dod;
	private boolean searchInNaturalOrder;
	private Date creationDate;
	private Time creationTime;

	@Lob
	private String dynamicVRPREPModelXML;

	public Double getDod() {
		return dod;
	}

	public Double getDynamicResultReverse() {
		return dynamicResultReverse;
	}

	public void setDynamicResultReverse(Double dynamicResultReverse) {
		this.dynamicResultReverse = dynamicResultReverse;
	}

	public void setDod(Double dod) {
		this.dod = dod;
	}

	public Double getDynamicResult() {
		return dynamicResult;
	}

	public void setDynamicResult(Double dynamicResult) {
		this.dynamicResult = dynamicResult;
	}

	public Double getStaticResult() {
		return staticResult;
	}

	public void setStaticResult(Double staticResult) {
		this.staticResult = staticResult;
	}

	public Time getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Time creationTime) {
		this.creationTime = creationTime;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public boolean isSearchInNaturalOrder() {
		return searchInNaturalOrder;
	}

	public void setSearchInNaturalOrder(boolean searchInNaturalOrder) {
		this.searchInNaturalOrder = searchInNaturalOrder;
	}

	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(String instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public String getDynamicVRPREPModelXML() {
		return dynamicVRPREPModelXML;
	}

	public void setDynamicVRPREPModelXML(String dynamicVRPREPModelXML) {
		this.dynamicVRPREPModelXML = dynamicVRPREPModelXML;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DBResultData() {
	}

	public DBResultData(ResultData resultData) throws SAXException, JAXBException, IOException {
		String dynamicInstanceXML = resultData.getModel().getModelAsXML();
		this.setAlgorithm(resultData.getAlgorithm());
		this.setInstanceIdentifier(resultData.getInstanceIdentifier());
		this.setDynamicResult(resultData.getDynamicResult());
		this.setStaticResult(resultData.getStaticResult());
		this.setObjective(resultData.getObjective());
		this.setDynamicVRPREPModelXML(dynamicInstanceXML.toString());
		this.setSearchInNaturalOrder(resultData.isSearchInNaturalOrder());
		this.setCreationDate(Date.valueOf(LocalDate.now()));
		this.setCreationTime(Time.valueOf(LocalTime.now()));
		this.setDod(resultData.getDod());
		this.setDynamicResultReverse(resultData.getDynamicResultReverse());
	}

	public ResultData transformTo(boolean loadDynamicVRPREPModel)
			throws JAXBException, IOException, XMLStreamException, FactoryConfigurationError {
		
		DynamicVRPREPModel model = null;
		if (loadDynamicVRPREPModel) {
			model = DynamicVRPREPModel.load(this.dynamicVRPREPModelXML,
					getClass().getName() + '@' + Integer.toHexString(hashCode()));
		}
		
		ResultData resultData = new ResultData(model, this.instanceIdentifier, this.algorithm, this.objective, this.searchInNaturalOrder,
				this.dynamicResult, this.dynamicResultReverse, this.staticResult, this.dod);
		resultData.setId(this.getId());
		return resultData;
	}

}
