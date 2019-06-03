package vrpsim.resultdatastore.dbimpl;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import vrpsim.resultdatastore.api.PureResultData;

@Entity
public class DBPureResultData implements Serializable {

	@Transient
	private static final long serialVersionUID = -6281441600348253870L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long dbResultDataId;

	private String initialSolutionAlgorithm;

	private String dynamicSolutionAlgorithm;

	private Double dynamicResult;

	private Double dynamicResultReverse;

	private Date creationDate;

	private Time creationTime;

	private Long dynamicHandlerSeed;

	public Long getDynamicHandlerSeed() {
		return dynamicHandlerSeed;
	}

	public void setDynamicHandlerSeed(Long dynamicHandlerSeed) {
		this.dynamicHandlerSeed = dynamicHandlerSeed;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DBPureResultData() {
	}

	public DBPureResultData(PureResultData pureresultData) {
		this.setCreationDate(Date.valueOf(LocalDate.now()));
		this.setCreationTime(Time.valueOf(LocalTime.now()));
		this.setDbResultDataId(pureresultData.getDbResultDataId());
		this.setDynamicResult(pureresultData.getDynamicResult());
		this.setDynamicResultReverse(pureresultData.getDynamicResultReverse());
		this.setDynamicSolutionAlgorithm(pureresultData.getDynamicSolutionAlgorithm());
		this.setInitialSolutionAlgorithm(pureresultData.getInitialSolutionAlgorithm());
		this.setId(pureresultData.getId());
		this.setDynamicHandlerSeed(pureresultData.getSeed());
	}

	public PureResultData transformTo() {
		return new PureResultData(dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm, dynamicResult, dynamicResultReverse,
				this.dynamicHandlerSeed);
	}

}
