package vrpsim.resultdatastore.api;

import java.io.File;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Time;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlRootElement;

import org.xml.sax.SAXException;

@XmlRootElement(name = "pure-result-data")
public class PureResultData {

	private Long id;

	private Long dbResultDataId;

	private String initialSolutionAlgorithm;

	private String dynamicSolutionAlgorithm;

	private Double dynamicResult;

	private Double dynamicResultReverse;

	private Date creationDate;

	private Time creationTime;

	private Long seed;

	public PureResultData() {
	}

	public PureResultData(Long dbResultDataId, String initialSolutionAlgorithm, String dynamicSolutionAlgorithm, Double dynamicResult,
			Double dynamicResultReverse, long seed) {
		this.dbResultDataId = dbResultDataId;
		this.initialSolutionAlgorithm = initialSolutionAlgorithm;
		this.dynamicSolutionAlgorithm = dynamicSolutionAlgorithm;
		this.dynamicResult = dynamicResult;
		this.dynamicResultReverse = dynamicResultReverse;
		this.seed = seed;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDbResultDataId() {
		return dbResultDataId;
	}

	public void setDbResultDataId(Long dbResultDataId) {
		this.dbResultDataId = dbResultDataId;
	}

	public String getInitialSolutionAlgorithm() {
		return initialSolutionAlgorithm;
	}

	public void setInitialSolutionAlgorithm(String initialSolutionAlgorithm) {
		this.initialSolutionAlgorithm = initialSolutionAlgorithm;
	}

	public String getDynamicSolutionAlgorithm() {
		return dynamicSolutionAlgorithm;
	}

	public void setDynamicSolutionAlgorithm(String dynamicSolutionAlgorithm) {
		this.dynamicSolutionAlgorithm = dynamicSolutionAlgorithm;
	}

	public Double getDynamicResult() {
		return dynamicResult;
	}

	public void setDynamicResult(Double dynamicResult) {
		this.dynamicResult = dynamicResult;
	}

	public Double getDynamicResultReverse() {
		return dynamicResultReverse;
	}

	public void setDynamicResultReverse(Double dynamicResultReverse) {
		this.dynamicResultReverse = dynamicResultReverse;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Time getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Time creationTime) {
		this.creationTime = creationTime;
	}

	public File writeToFile(Path outputPath) throws JAXBException, SAXException {
		outputPath.getParent().toFile().mkdirs();
		JAXBContext jc = JAXBContext.newInstance(PureResultData.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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
