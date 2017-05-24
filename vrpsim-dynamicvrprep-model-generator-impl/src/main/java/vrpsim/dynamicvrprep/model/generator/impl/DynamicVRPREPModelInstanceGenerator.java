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
package vrpsim.dynamicvrprep.model.generator.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicRequestInformation;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.generator.api.IArrivalTimeDetermineStrategy;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelInstanceGenerator;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequest;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequests;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;

public class DynamicVRPREPModelInstanceGenerator implements IDynamicVRPREPModelInstanceGenerator {

	private static Logger logger = LoggerFactory.getLogger(DynamicVRPREPModelInstanceGenerator.class);

	private Instance instance;
	private Random random;

	@Override
	public void initialize(Instance instance, Random random) {
		this.instance = instance;
		this.random = random;
	}

	@Override
	public IDynamicVRPREPModelGenerationPosibilities generateStatisticInformation(double degreeOfDynanism,
			int numberCombinationsGenerationPosibilitiesAreBasedOn) throws InstanceGenerationException {

		IDynamicVRPREPModelGenerationPosibilities collector = new DynamicVRPREPModelGenerationPosibilities();
		DynamicVRPREPModelInstanceGeneratorUtil instanceUtil = new DynamicVRPREPModelInstanceGeneratorUtil(instance);

		List<TmpRequest> allTmpRequests = this.createTmpRequests(instance, instanceUtil);
		collector.setStaticInstance(this.instance);
		collector.setAllTmpRequests(allTmpRequests);
		final double totalDistance = this.getTotalDistance(allTmpRequests);
		collector.setTotalDistance(totalDistance);
		final int numberDynamicRequests = new Double(allTmpRequests.size() * degreeOfDynanism).intValue();

		generateSmallestAndBiggestPossible(allTmpRequests, numberDynamicRequests, totalDistance, collector);
		collector.setNumberOfDynamicRequests(numberDynamicRequests);
		collector.setDegreeOfDynanism(degreeOfDynanism);

		List<TmpRequests> combinations = generateCombinationsSimple(random, allTmpRequests, numberDynamicRequests,
				numberCombinationsGenerationPosibilitiesAreBasedOn);
		Collections.sort(combinations, new Comparator<TmpRequests>() {
			public int compare(TmpRequests o1, TmpRequests o2) {
				return Double.compare(o1.calculateLDOD(totalDistance), o2.calculateLDOD(totalDistance));
			}
		});
		collector.setDynamicCombinationsSorted(combinations);

		logger.debug(
				"DOD = {}, Number of dynamic requests = {}, Number combinations = {}, lowest LDOD = {}, highest LDOD = {}",
				degreeOfDynanism, combinations.get(0).getRequests().size(), combinations.size(),
				combinations.get(0).calculateLDOD(totalDistance),
				combinations.get(combinations.size() - 1).calculateLDOD(totalDistance));

		return collector;
	}

	@Override
	public DynamicVRPREPModel generateInstance(TmpRequests dynamicRequests, IDynamicVRPREPModelGenerationPosibilities posibilities,
			IArrivalTimeDetermineStrategy arrivalTimeDetermineStrategy, double edod, int timeHorizon)
			throws InstanceGenerationException {
		
		if(dynamicRequests.getRequests().size() != posibilities.getNumberOfDynamicRequests()) {
			logger.error("Invalid number ({}) of dynamic requests for dod {}.", dynamicRequests.getRequests().size(), posibilities.getDegreeOfDynanism());
			throw new RuntimeException("Invalid number ({}) of dynamic requests for dod {}. See log file: " + logger.getName());
		}
		
		if(arrivalTimeDetermineStrategy == null) {
			arrivalTimeDetermineStrategy = new NormalDistributedArrivalTimeDetermineStrategy();
		}
		arrivalTimeDetermineStrategy.determineArrivalTimes(this.random, dynamicRequests, edod, timeHorizon, posibilities.getAllRequests().size());

		DynamicVRPREPModel dvrpm = new DynamicVRPREPModel();
		dvrpm.setVRPREPInstance(instance);
		dvrpm.setPlanningTimeHorizon(timeHorizon);
		Map<BigInteger, DynamicRequestInformation> dynamicRequestInformation = new HashMap<BigInteger, DynamicRequestInformation>();
		for (TmpRequest tmpRequest : dynamicRequests.getRequests()) {
			DynamicRequestInformation dri = new DynamicRequestInformation();
			dri.setArrivalTime(tmpRequest.getTime());
			dynamicRequestInformation.put(tmpRequest.getId(), dri);
		}
		dvrpm.setDynamicRequestInformation(dynamicRequestInformation);
		return dvrpm;
	}
	
	@Override
	public TmpRequests generateRandomValidTmpRequests(IDynamicVRPREPModelGenerationPosibilities posibilities,Random random) {
		List<TmpRequest> allTmpRequests = posibilities.getAllRequests();
		int numberDynamicRequests = posibilities.getNumberOfDynamicRequests();
		Collections.shuffle(allTmpRequests, random);
		return new TmpRequests(new ArrayList<>(allTmpRequests.subList(0, numberDynamicRequests)));
	}

	private void generateSmallestAndBiggestPossible(List<TmpRequest> allTmpRequests, int numberDynamicRequests,
			double totalDistance, IDynamicVRPREPModelGenerationPosibilities collector) {
		
		Collections.sort(allTmpRequests, new Comparator<TmpRequest>() {
			public int compare(TmpRequest o1, TmpRequest o2) {
				return Double.compare(o1.getTotalDistanceToOthers(), o2.getTotalDistanceToOthers());
			}
		});

		TmpRequests smallest = new TmpRequests(new ArrayList<>(allTmpRequests.subList(0, numberDynamicRequests)));
		collector.setSmallest(smallest);
		logger.debug("SmallestPossible = " + smallest.calculateLDOD(totalDistance) + " - " + smallest.toString());
		TmpRequests biggest = new TmpRequests(new ArrayList<>(
				allTmpRequests.subList(allTmpRequests.size() - numberDynamicRequests - 1, allTmpRequests.size() - 1)));
		collector.setBiggest(biggest);
		logger.debug("BiggestPossible = " + biggest.calculateLDOD(totalDistance) + " - " + biggest.toString());
	}

	private List<TmpRequest> createTmpRequests(Instance instance, DynamicVRPREPModelInstanceGeneratorUtil instanceUtil) {
		List<TmpRequest> result = new ArrayList<TmpRequest>();
		for (Request r1 : instance.getRequests().getRequest()) {
			double totalRequestDistance = 0.0;
			Map<BigInteger, Double> distancesToOthers = new HashMap<>();
			for (Request r2 : instance.getRequests().getRequest()) {
				double distanceR1R2 = instanceUtil.getDistance(r1, r2);
				totalRequestDistance += distanceR1R2;
				if (!r1.getId().equals(r2.getId())) {
					distancesToOthers.put(r2.getId(), new Double(distanceR1R2));
				}
			}
			TmpRequest tmpRequest = new TmpRequest(r1.getId(), totalRequestDistance, instanceUtil.getNode(r1).getCx(),
					instanceUtil.getNode(r1).getCy(), distancesToOthers);
			result.add(tmpRequest);
		}
		return result;
	}

	private double getTotalDistance(List<TmpRequest> requests) {
		double result = 0.0;
		for (TmpRequest request : requests) {
			result += request.getTotalDistanceToOthers();
		}
		return result;
	}

	private List<TmpRequests> generateCombinationsSimple(Random random, List<TmpRequest> requests, int numberDynamicRequests, int numberCombinations) {
		List<TmpRequests> result = new ArrayList<TmpRequests>();
		for (int i = 0; i < numberCombinations; i++) {
			Collections.shuffle(requests, random);
			result.add(new TmpRequests(new ArrayList<>(requests.subList(0, numberDynamicRequests))));
		}
		return result;
	}

}
