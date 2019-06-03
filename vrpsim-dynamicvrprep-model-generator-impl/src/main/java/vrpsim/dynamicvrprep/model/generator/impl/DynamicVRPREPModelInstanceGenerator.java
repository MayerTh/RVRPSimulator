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
import vrpsim.dynamicvrprep.model.generator.api.IArrivalTimeDetermineStrategy.NotInitilizedException;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelInstanceGenerator;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequest;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequests;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelStatisticsInformation;
import vrpsim.dynamicvrprep.model.generator.api.model.util.GeneratorModelUtil;

public class DynamicVRPREPModelInstanceGenerator implements IDynamicVRPREPModelInstanceGenerator {

	private static Logger logger = LoggerFactory.getLogger(DynamicVRPREPModelInstanceGenerator.class);

	private Instance instance;
	private Random random;

	@Override
	public GeneratorModelStatisticsInformation initialize(Instance instance, Random random, double degreeOfDynanism,
			int numberOfCombinationsToGenerate) throws InstanceGenerationException {
		this.instance = instance;
		this.random = random;

		return generateStatisticInformation(degreeOfDynanism, numberOfCombinationsToGenerate);
	}

	public GeneratorModelStatisticsInformation generateStatisticInformation(double degreeOfDynanism, int numberOfCombinationsToGenerate)
			throws InstanceGenerationException {

		GeneratorModelUtil instanceUtil = new GeneratorModelUtil(instance);

		List<GeneratorModelRequest> allTmpRequests = this.createTmpRequests(instance, instanceUtil);
		final double totalDistance = this.getTotalDistance(allTmpRequests);
		final int numberDynamicRequests = new Double(allTmpRequests.size() * degreeOfDynanism).intValue();


		List<GeneratorModelRequests> combinations = generateCombinationsSimple(random, allTmpRequests, numberDynamicRequests,
				numberOfCombinationsToGenerate);
		Collections.sort(combinations, new Comparator<GeneratorModelRequests>() {
			public int compare(GeneratorModelRequests o1, GeneratorModelRequests o2) {
				return Double.compare(o1.calculateLDOD(totalDistance), o2.calculateLDOD(totalDistance));
			}
		});

		logger.debug("DOD = {}, Number of dynamic requests = {}, Number combinations = {}, lowest LDOD = {}, highest LDOD = {}", degreeOfDynanism,
				combinations.get(0).getDynamicRequests().size(), combinations.size(), combinations.get(0).calculateLDOD(totalDistance),
				combinations.get(combinations.size() - 1).calculateLDOD(totalDistance));
		
		GeneratorModelStatisticsInformation collector = new GeneratorModelStatisticsInformation(this.instance, this.random, allTmpRequests, degreeOfDynanism, numberDynamicRequests);
		generateAndSetSmallestAndBiggestPossible(allTmpRequests, numberDynamicRequests, totalDistance, collector);
		collector.setTotalDistance(totalDistance);
		collector.setDynamicCombinationsSorted(combinations);
		
		return collector;
	}

	@Override
	public DynamicVRPREPModel generateDynamicVRPREPModelInstance(GeneratorModelRequests dynamicRequests, GeneratorModelStatisticsInformation posibilities,
			IArrivalTimeDetermineStrategy arrivalTimeDetermineStrategy) throws InstanceGenerationException, NotInitilizedException {

		if (dynamicRequests.getDynamicRequests().size() != posibilities.getNumberOfDynamicRequests()) {
			logger.error("Invalid number ({}) of dynamic requests for dod {}.", dynamicRequests.getDynamicRequests().size(), posibilities.getDegreeOfDynanism());
			throw new RuntimeException("Invalid number ({}) of dynamic requests for dod {}. See log file: " + logger.getName());
		}

		arrivalTimeDetermineStrategy.determineArrivalTimes(this.random, dynamicRequests);

		DynamicVRPREPModel dvrpm = new DynamicVRPREPModel();
		dvrpm.setVRPREPInstance(instance);
		dvrpm.setPlanningTimeHorizon(arrivalTimeDetermineStrategy.getTimeHorizon());
		Map<BigInteger, DynamicRequestInformation> dynamicRequestInformation = new HashMap<BigInteger, DynamicRequestInformation>();
		for (GeneratorModelRequest tmpRequest : dynamicRequests.getDynamicRequests()) {
			DynamicRequestInformation dri = new DynamicRequestInformation();
			dri.setArrivalTime(tmpRequest.getTime());
			dynamicRequestInformation.put(tmpRequest.getId(), dri);
		}
		dvrpm.setDynamicRequestInformation(dynamicRequestInformation);
		return dvrpm;
	}

	@Override
	public GeneratorModelRequests generateRandomValidGeneratorModelRequests(GeneratorModelStatisticsInformation posibilities) {
		List<GeneratorModelRequest> allTmpRequests = posibilities.getAllRequests();
		int numberDynamicRequests = posibilities.getNumberOfDynamicRequests();
		Collections.shuffle(allTmpRequests, posibilities.getRandom());
		return new GeneratorModelRequests(new ArrayList<>(allTmpRequests.subList(0, numberDynamicRequests)), allTmpRequests);
	}

	private void generateAndSetSmallestAndBiggestPossible(List<GeneratorModelRequest> allTmpRequests, int numberDynamicRequests, double totalDistance,
			GeneratorModelStatisticsInformation collector) {

		Collections.sort(allTmpRequests, new Comparator<GeneratorModelRequest>() {
			public int compare(GeneratorModelRequest o1, GeneratorModelRequest o2) {
				return Double.compare(o1.getTotalDistanceToOthers(), o2.getTotalDistanceToOthers());
			}
		});

		GeneratorModelRequests smallest = new GeneratorModelRequests(new ArrayList<>(allTmpRequests.subList(0, numberDynamicRequests)), allTmpRequests);
		collector.setSmallest(smallest);
		logger.debug("SmallestPossible = " + smallest.calculateLDOD(totalDistance) + " - " + smallest.toString());
		GeneratorModelRequests biggest = new GeneratorModelRequests(
				new ArrayList<>(allTmpRequests.subList(allTmpRequests.size() - numberDynamicRequests - 1, allTmpRequests.size() - 1)), allTmpRequests);
		collector.setBiggest(biggest);
		logger.debug("BiggestPossible = " + biggest.calculateLDOD(totalDistance) + " - " + biggest.toString());
	}

	private List<GeneratorModelRequest> createTmpRequests(Instance instance, GeneratorModelUtil instanceUtil) {
		List<GeneratorModelRequest> result = new ArrayList<GeneratorModelRequest>();
		for (Request r1 : instance.getRequests().getRequest()) {
			GeneratorModelRequest tmpRequest = new GeneratorModelRequest(r1.getId(), instanceUtil.getNode(r1).getCx(), instanceUtil.getNode(r1).getCy(),
					instance, instanceUtil);
			result.add(tmpRequest);
		}
		return result;
	}

	private double getTotalDistance(List<GeneratorModelRequest> requests) {
		double result = 0.0;
		for (GeneratorModelRequest request : requests) {
			result += request.getTotalDistanceToOthers();
		}
		return result;
	}

	private List<GeneratorModelRequests> generateCombinationsSimple(Random random, List<GeneratorModelRequest> requests, int numberDynamicRequests,
			int numberCombinations) {
		List<GeneratorModelRequests> result = new ArrayList<GeneratorModelRequests>();
		for (int i = 0; i < numberCombinations; i++) {
			Collections.shuffle(requests, random);
			result.add(new GeneratorModelRequests(new ArrayList<>(requests.subList(0, numberDynamicRequests)), requests));
		}
		return result;
	}

}
