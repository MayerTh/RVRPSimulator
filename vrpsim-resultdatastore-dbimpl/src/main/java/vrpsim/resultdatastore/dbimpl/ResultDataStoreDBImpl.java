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
package vrpsim.resultdatastore.dbimpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.resultdatastore.api.IResultDataStoreAPI;
import vrpsim.resultdatastore.api.PersistenceUnit;
import vrpsim.resultdatastore.api.PureResultData;
import vrpsim.resultdatastore.api.ResultData;
import vrpsim.resultdatastore.api.ResultDataStoreAPIException;
import vrpsim.resultdatastore.fileimpl.ResultDataStoreFileImpl;

public class ResultDataStoreDBImpl implements IResultDataStoreAPI {

	private static Logger logger = LoggerFactory.getLogger(ResultDataStoreDBImpl.class);
	private int tryCounterDBResultData = 0;
	private int tryCounterDBPureResultResultData = 0;
	private final int maxTriesMain = 30;
	private final int maxTriesServer = 30;

	@Override
	public void storePureResultData(String exportFolder, String fileName, Long dbResultDataId, String initialSolutionAlgorithm,
			String dynamicSolutionAlgorithm, double dynamicResult, double dynamicResultReverse, long seed, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {

		PureResultData pureResultData = new PureResultData(dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm,
				dynamicResult, dynamicResultReverse, seed);
		DBPureResultData dbPureResultData = new DBPureResultData(pureResultData);

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();
			em.persist(dbPureResultData);
			em.getTransaction().commit();
			em.close();
			tryCounterDBPureResultResultData = 0;
		} catch (PersistenceException e) {

			try {
				logger.error("Could not write DBPureResultData to database, wait 30sek and try again.");
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				logger.error("Exception {} will be ignored.", e1.getMessage());
				e1.printStackTrace();
			}

			tryCounterDBPureResultResultData++;

			if (tryCounterDBPureResultResultData <= maxTriesMain) {
				logger.error("Can not write DBPureResultData to database {}, tryCounter = {} by {} maxTriesMain, due to exception {}",
						pUnit.toString(), tryCounterDBPureResultResultData, maxTriesMain, e.getMessage());
				storePureResultData(exportFolder, fileName, dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm,
						dynamicResult, dynamicResultReverse, seed, pUnit);
			} else if (tryCounterDBPureResultResultData <= (maxTriesMain + maxTriesServer)) {
				logger.error("Can not write DBPureResultData to database {}, tryCounter = {} by {} maxTriesServer, due to exception {}",
						PersistenceUnit.SERVER.toString(), tryCounterDBPureResultResultData, maxTriesServer, e.getMessage());
				storePureResultData(exportFolder, fileName, dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm,
						dynamicResult, dynamicResultReverse, seed, PersistenceUnit.SERVER);
			} else {

				tryCounterDBPureResultResultData = 0;
				logger.error(
						"Finaly will write DBPureResultData into file system {}, name is {}, due to exceptions during multiple tries. {} ",
						exportFolder, fileName, e.getMessage());
				IResultDataStoreAPI fileapi = new ResultDataStoreFileImpl();
				fileapi.storePureResultData(exportFolder, fileName, dbResultDataId, initialSolutionAlgorithm, dynamicSolutionAlgorithm,
						dynamicResult, dynamicResultReverse, seed, pUnit);
			}
		}

	}

	@Override
	public Long storeResultData(String exportFolder, String fileName, DynamicVRPREPModel model, String instanceIdentifier, String algorithm,
			String objective, boolean searchInNaturalOrder, Double dynamicResult, Double dynamicResultReverse, Double staticResult,
			Double dod, PersistenceUnit pUnit) throws ResultDataStoreAPIException {

		ResultData resultData = new ResultData(model, instanceIdentifier, algorithm, objective, searchInNaturalOrder, dynamicResult,
				dynamicResultReverse, staticResult, dod);
		DBResultData dbResultData = null;
		try {
			dbResultData = new DBResultData(resultData);
		} catch (SAXException | JAXBException | IOException e) {
			logger.error("Can not write ResultData to database due to exception {}", e.getMessage());
			e.printStackTrace();
			throw new ResultDataStoreAPIException("Can not write ResultData to database due to exception {}");
		}

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();
			em.persist(dbResultData);
			em.getTransaction().commit();
			em.close();
			tryCounterDBResultData = 0;
		} catch (PersistenceException e) {

			tryCounterDBResultData++;
			logger.error("Can not write ResultData to database, tryCounter = {} by {} max tries, due to exception {}",
					tryCounterDBResultData, maxTriesMain, e.getMessage());

			if (tryCounterDBResultData < maxTriesMain) {
				storeResultData(exportFolder, fileName, model, instanceIdentifier, algorithm, objective, searchInNaturalOrder,
						dynamicResult, dynamicResultReverse, staticResult, dod, pUnit);
			} else {

				IResultDataStoreAPI fileapi = new ResultDataStoreFileImpl();
				fileapi.storeResultData(exportFolder, fileName, model, instanceIdentifier, algorithm, objective, searchInNaturalOrder,
						dynamicResult, dynamicResultReverse, staticResult, dod, pUnit);
				logger.error("Finaly will write ResultData into file system {}, name is {}, due to exceptions during multiple tries. {} ",
						exportFolder, fileName, e.getMessage());
				e.printStackTrace();
				throw new ResultDataStoreAPIException(
						"Finaly will write ResultData into file system {}, name is {}, due to exceptions during multiple tries. {} ");
			}
		}
		
		Long id = 0L;
		if(dbResultData != null) {
			id = dbResultData.getId();
		}
		return id;
	}

	@Override
	public List<PureResultData> getPureResultData(Long dbResultDataId, PersistenceUnit pUnit) throws ResultDataStoreAPIException {

		Long start = System.currentTimeMillis();

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DBPureResultData> dbResultDataCQ = cb.createQuery(DBPureResultData.class);
		Root<DBPureResultData> dbPureResultData = dbResultDataCQ.from(DBPureResultData.class);
		ParameterExpression<Long> dbResultDataIdPE = cb.parameter(Long.class);
		dbResultDataCQ.select(dbPureResultData).where(cb.equal(dbPureResultData.get("dbResultDataId"), dbResultDataIdPE));
		TypedQuery<DBPureResultData> query = em.createQuery(dbResultDataCQ);
		query.setParameter(dbResultDataIdPE, dbResultDataId);
		List<DBPureResultData> results = query.getResultList();
		em.close();

		Long durration = System.currentTimeMillis() - start;
		logger.info("Build query and fetch {} objects from type DBPureResultData took {}.", results.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		start = System.currentTimeMillis();
		List<PureResultData> pureResultDatas = new ArrayList<>();

		for (DBPureResultData dbPRD : results) {
			PureResultData data = dbPRD.transformTo();
			pureResultDatas.add(data);
		}

		durration = System.currentTimeMillis() - start;
		logger.info("Transform {} DBResultData into {} ResultData took {}.", results.size(), pureResultDatas.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		return pureResultDatas;
	}

	@Override
	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			Double dod, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		return this.getResultDataSorted(instanceIdentifier, algorithm, objective, dod, false, pUnit);
	}

	@Override
	public List<ResultData> getResultDataSortedWithoutDynamicVRPREPModel(String instanceIdentifier, String algorithm, String objective,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		return this.getResultDataSorted(instanceIdentifier, algorithm, objective, null, false, pUnit);
	}

	@Override
	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {
		return getResultDataSorted(instanceIdentifier, algorithm, objective, null, pUnit);
	}

	@Override
	public List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, Double dod,
			PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		return this.getResultDataSorted(instanceIdentifier, algorithm, objective, dod, true, pUnit);
	}

	private List<ResultData> getResultDataSorted(String instanceIdentifier, String algorithm, String objective, Double dod,
			boolean loadDynamicVRPREPModel, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		Long start = System.currentTimeMillis();

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DBResultData> dbResultDataCQ = cb.createQuery(DBResultData.class);
		Root<DBResultData> dbResultData = dbResultDataCQ.from(DBResultData.class);
		ParameterExpression<String> iiPE = cb.parameter(String.class);
		ParameterExpression<String> alPE = cb.parameter(String.class);
		ParameterExpression<String> obPE = cb.parameter(String.class);
		ParameterExpression<Double> dodMinPE = cb.parameter(Double.class);
		ParameterExpression<Double> dodMaxPE = cb.parameter(Double.class);

		if (dod != null) {
			dbResultDataCQ.select(dbResultData).where(cb.equal(dbResultData.get("instanceIdentifier"), iiPE),
					cb.equal(dbResultData.get("algorithm"), alPE), cb.equal(dbResultData.get("objective"), obPE),
					cb.greaterThan(dbResultData.get("dod"), dodMinPE), cb.lessThan(dbResultData.get("dod"), dodMaxPE));
		} else {
			dbResultDataCQ.select(dbResultData).where(cb.equal(dbResultData.get("instanceIdentifier"), iiPE),
					cb.equal(dbResultData.get("algorithm"), alPE), cb.equal(dbResultData.get("objective"), obPE));
		}
		dbResultDataCQ.orderBy(cb.asc(dbResultData.get("dynamicResult")));

		TypedQuery<DBResultData> query = em.createQuery(dbResultDataCQ);
		query.setParameter(iiPE, instanceIdentifier);
		query.setParameter(alPE, algorithm);
		query.setParameter(obPE, objective);

		if (dod != null) {
			double dodMin = dod - 0.03;
			double dodMax = dod + 0.03;
			query.setParameter(dodMinPE, dodMin);
			query.setParameter(dodMaxPE, dodMax);
		}

		List<DBResultData> results = query.getResultList();
		// em.getTransaction().begin();
		// em.getTransaction().commit();
		em.close();

		Long durration = System.currentTimeMillis() - start;
		logger.info("Build query and fetch {} objects from type DBResultData took {}.", results.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		start = System.currentTimeMillis();
		List<ResultData> resultDatas = new ArrayList<>();

		for (DBResultData dbRD : results) {
			try {
				ResultData data = dbRD.transformTo(loadDynamicVRPREPModel);
				resultDatas.add(data);
			} catch (JAXBException | IOException | XMLStreamException | FactoryConfigurationError e) {
				e.printStackTrace();
				logger.error("Not able to load DynamicVRPModel from database XML.");
				throw new ResultDataStoreAPIException("Not able to load DynamicVRPModel from database XML.");
			}
		}
		durration = System.currentTimeMillis() - start;
		logger.info("Transform {} DBResultData into {} ResultData took {}.", results.size(), resultDatas.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		return resultDatas;
	}

	@Override
	public ResultData getMinCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DBResultData> query = cb.createQuery(DBResultData.class);
		Root<DBResultData> from = query.from(DBResultData.class);
		query.select(from);

		Subquery<Double> subQuery = query.subquery(Double.class);
		Root<DBResultData> subFrom = subQuery.from(DBResultData.class);
		subQuery.select(cb.min(subFrom.get("dynamicResult")));
		ParameterExpression<String> iiPE = cb.parameter(String.class);
		ParameterExpression<String> alPE = cb.parameter(String.class);
		ParameterExpression<String> obPE = cb.parameter(String.class);
		ParameterExpression<Double> dodMinPE = cb.parameter(Double.class);
		ParameterExpression<Double> dodMaxPE = cb.parameter(Double.class);

		if (dod != null) {
			subQuery.where(cb.equal(subFrom.get("instanceIdentifier"), iiPE), cb.equal(subFrom.get("algorithm"), alPE),
					cb.equal(subFrom.get("objective"), obPE), cb.greaterThan(subFrom.get("dod"), dodMinPE),
					cb.lessThan(subFrom.get("dod"), dodMaxPE));
		} else {
			subQuery.where(cb.equal(subFrom.get("instanceIdentifier"), iiPE), cb.equal(subFrom.get("algorithm"), alPE),
					cb.equal(subFrom.get("objective"), obPE));
		}

		query.where(cb.equal(subQuery, from.get("dynamicResult")));

		TypedQuery<DBResultData> result = em.createQuery(query);
		result.setParameter(iiPE, instanceIdentifier);
		result.setParameter(alPE, algorithm);
		result.setParameter(obPE, objective);

		if (dod != null) {
			double dodMin = dod - 0.03;
			double dodMax = dod + 0.03;
			result.setParameter(dodMinPE, dodMin);
			result.setParameter(dodMaxPE, dodMax);
		}

		ResultData rd = null;
		try {
			List<DBResultData> results = result.getResultList();
			if (results.size() > 0) {
				rd = results.get(0).transformTo(true);
			}
		} catch (JAXBException | IOException | XMLStreamException | FactoryConfigurationError e) {
			e.printStackTrace();
			logger.error("Could not transform DBResultData into ResultData. {} ", e.getMessage());
			throw new ResultDataStoreAPIException("Could not transform DBResultData into ResultData. {} ");
		}
		em.close();
		return rd;

	}

	@Override
	public ResultData getMaxCostResultData(String instanceIdentifier, String algorithm, String objective, Double dod, PersistenceUnit pUnit)
			throws ResultDataStoreAPIException {

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DBResultData> query = cb.createQuery(DBResultData.class);
		Root<DBResultData> from = query.from(DBResultData.class);
		query.select(from);

		Subquery<Double> subQuery = query.subquery(Double.class);
		Root<DBResultData> subFrom = subQuery.from(DBResultData.class);
		subQuery.select(cb.max(subFrom.get("dynamicResult")));
		ParameterExpression<String> iiPE = cb.parameter(String.class);
		ParameterExpression<String> alPE = cb.parameter(String.class);
		ParameterExpression<String> obPE = cb.parameter(String.class);
		ParameterExpression<Double> dodMinPE = cb.parameter(Double.class);
		ParameterExpression<Double> dodMaxPE = cb.parameter(Double.class);

		if (dod != null) {
			subQuery.where(cb.equal(subFrom.get("instanceIdentifier"), iiPE), cb.equal(subFrom.get("algorithm"), alPE),
					cb.equal(subFrom.get("objective"), obPE), cb.greaterThan(subFrom.get("dod"), dodMinPE),
					cb.lessThan(subFrom.get("dod"), dodMaxPE));
		} else {
			subQuery.where(cb.equal(subFrom.get("instanceIdentifier"), iiPE), cb.equal(subFrom.get("algorithm"), alPE),
					cb.equal(subFrom.get("objective"), obPE));
		}

		query.where(cb.equal(subQuery, from.get("dynamicResult")));

		TypedQuery<DBResultData> result = em.createQuery(query);
		result.setParameter(iiPE, instanceIdentifier);
		result.setParameter(alPE, algorithm);
		result.setParameter(obPE, objective);

		if (dod != null) {
			double dodMin = dod - 0.03;
			double dodMax = dod + 0.03;
			result.setParameter(dodMinPE, dodMin);
			result.setParameter(dodMaxPE, dodMax);
		}

		ResultData rd = null;
		try {
			List<DBResultData> results = result.getResultList();
			if (results.size() > 0) {
				rd = results.get(0).transformTo(true);
			}
		} catch (JAXBException | IOException | XMLStreamException | FactoryConfigurationError e) {
			e.printStackTrace();
			logger.error("Could not transform DBResultData into ResultData. {} ", e.getMessage());
			throw new ResultDataStoreAPIException("Could not transform DBResultData into ResultData. {} ");
		}
		em.close();
		return rd;

	}

	@Override
	public ResultData getResultDataByDBResultDataId(Long id, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DBResultData> dbResultDataCQ = cb.createQuery(DBResultData.class);
		Root<DBResultData> dbResultData = dbResultDataCQ.from(DBResultData.class);
		ParameterExpression<Long> id_e = cb.parameter(Long.class);

		dbResultDataCQ.select(dbResultData).where(cb.equal(dbResultData.get("id"), id_e));

		TypedQuery<DBResultData> query = em.createQuery(dbResultDataCQ);
		query.setParameter(id_e, id);

		List<DBResultData> results = query.getResultList();
		em.close();

		ResultData resultData = null;
		if (results.size() > 0) {
			try {
				resultData = results.get(0).transformTo(true);
			} catch (JAXBException | IOException | XMLStreamException | FactoryConfigurationError e) {
				e.printStackTrace();
				logger.error("Could not transform DBResultData into ResultData. {} ", e.getMessage());
				throw new ResultDataStoreAPIException("Could not transform DBResultData into ResultData. {} ");
			}
		}

		return resultData;
	}

	@Override
	public List<ResultData> getResultDataSortedNotCalculated(String instanceIdentifier, String algorithm, String objective, Double dod,
			String dynamicAlgorithmToCalculate, PersistenceUnit pUnit) throws ResultDataStoreAPIException {
		Long start = System.currentTimeMillis();

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(pUnit.toString());
		EntityManager em = factory.createEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DBResultData> dbResultDataCQ = cb.createQuery(DBResultData.class);
		Root<DBResultData> dbResultData = dbResultDataCQ.from(DBResultData.class);
		ParameterExpression<String> iiPE = cb.parameter(String.class);
		ParameterExpression<String> alPE = cb.parameter(String.class);
		ParameterExpression<String> obPE = cb.parameter(String.class);
		ParameterExpression<Double> dodMinPE = cb.parameter(Double.class);
		ParameterExpression<Double> dodMaxPE = cb.parameter(Double.class);

		if (dod != null) {
			dbResultDataCQ.select(dbResultData).where(cb.equal(dbResultData.get("instanceIdentifier"), iiPE),
					cb.equal(dbResultData.get("algorithm"), alPE), cb.equal(dbResultData.get("objective"), obPE),
					cb.greaterThan(dbResultData.get("dod"), dodMinPE), cb.lessThan(dbResultData.get("dod"), dodMaxPE));
		} else {
			dbResultDataCQ.select(dbResultData).where(cb.equal(dbResultData.get("instanceIdentifier"), iiPE),
					cb.equal(dbResultData.get("algorithm"), alPE), cb.equal(dbResultData.get("objective"), obPE));
		}
		dbResultDataCQ.orderBy(cb.asc(dbResultData.get("dynamicResult")));

		TypedQuery<DBResultData> query = em.createQuery(dbResultDataCQ);
		query.setParameter(iiPE, instanceIdentifier);
		query.setParameter(alPE, algorithm);
		query.setParameter(obPE, objective);

		if (dod != null) {
			double dodMin = dod - 0.03;
			double dodMax = dod + 0.03;
			query.setParameter(dodMinPE, dodMin);
			query.setParameter(dodMaxPE, dodMax);
		}

		List<DBResultData> results_old = query.getResultList();
		// em.getTransaction().begin();
		// em.getTransaction().commit();
		em.close();
		
		List<DBResultData> results = new ArrayList<>();
		for(DBResultData dbData : results_old) {
			Long dbResultDataId = dbData.getId();
			List<PureResultData> pureResultData = this.getPureResultData(dbResultDataId, pUnit);
			logger.debug("Found {} pure result data for data id {}.", pureResultData.size(), dbResultDataId);
			
			boolean insert = true;
			for(PureResultData dbPureData : pureResultData) {
				if(dbPureData.getDynamicSolutionAlgorithm().equals(dynamicAlgorithmToCalculate)) {
					insert = false;
					break;
				}
			}
			
			logger.debug("Insert DBResultData into result list? {}", insert);
			if(insert) {
				results.add(dbData);
			}
		}

		Long durration = System.currentTimeMillis() - start;
		logger.info("Build query and fetch {} objects from type DBResultData took {}.", results.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		start = System.currentTimeMillis();
		List<ResultData> resultDatas = new ArrayList<>();

		for (DBResultData dbRD : results) {
			try {
				ResultData data = dbRD.transformTo(true);
				resultDatas.add(data);
			} catch (JAXBException | IOException | XMLStreamException | FactoryConfigurationError e) {
				e.printStackTrace();
				logger.error("Not able to load DynamicVRPModel from database XML.");
				throw new ResultDataStoreAPIException("Not able to load DynamicVRPModel from database XML.");
			}
		}
		durration = System.currentTimeMillis() - start;
		logger.info("Transform {} DBResultData into {} ResultData took {}.", results.size(), resultDatas.size(),
				(new SimpleDateFormat("mm:ss.SSS")).format(new Date(durration)));

		return resultDatas;
	}

}
