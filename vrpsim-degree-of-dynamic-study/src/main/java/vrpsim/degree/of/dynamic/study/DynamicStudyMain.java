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
package vrpsim.degree.of.dynamic.study;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.xml.sax.SAXException;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.solution.IDynamicBehaviourProvider;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.solution.SolutionManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.InitializationException;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.degree.of.dynamic.study.r.RExporter;
import vrpsim.degree.of.dynamic.study.r.RExporterSummary;
import vrpsim.degree.of.dynamic.study.r.RNormalizedCosts;
import vrpsim.degree.of.dynamic.study.r.SimulationResultExporter;
import vrpsim.degree.of.dynamic.study.statistics.RunStatistics;
import vrpsim.degree.of.dynamic.study.util.JspritVRPREPInstanceSolver;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelInstanceGenerator;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;
import vrpsim.dynamicvrprep.model.generator.impl.DynamicVRPREPModelInstanceGenerator;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.util.RModelTransformator;
import vrpsim.r.util.impl.RExporterImpl;
import vrpsim.simulationmodel.api.ISimulationModelAPI;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.DynamicBehaviourProviderGenerator;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.IDynamicBehaviourProviderGenerator;
import vrpsim.simulationmodel.dynamicbehaviour.generator.kernighanlinimpl.CKLCustomerStillToServeSorter;
import vrpsim.simulationmodel.impl.SimulationModelAPI;
import vrpsim.simulationmodel.initialbehaviour.generator.api.IInitialBehaviourProviderGenerator;
import vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl.JspritInitialBehaviorProviderGenerator;
import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI;

public class DynamicStudyMain {

	private static Logger logger = LoggerFactory.getLogger(DynamicStudyMain.class);

	private static String rootStat = "statistics";
	private static String instanceKindStr = "staticvrp";
	private static String instanceProviderStr = "solomon1987";

	private static int sampleSize = 48;
	private static double recordedDOD = 0.2;
	private static int numberPlanningTimeHorzionFactors = 1;
	private static double planningTimeHorzionFactorStart = 1;

	private static double startDOD = 0.1;
	private static double edod = 0.2;
	private static Random random = new Random(12345);

	private static int numberCombinationsGenerationPosibilitiesAreBasedOn = 10000;

	public static void main(String[] args)
			throws IOException, JAXBException, URISyntaxException, InstanceGenerationException, EventException, InterruptedException, InitializationException, SAXException {

		HashMap<Double, List<RunStatistics.Point>> normalizedCostsOverLDOD = new HashMap<>();
		IVRPREPInstanceProviderAPI instanceProvider = new VRPREPInstanceProviderAPI();
		for (String instanceNameReal : instanceProvider.getAvailableInstancesForKindAndProvider(instanceKindStr, instanceProviderStr)) {

			for (int i = 0; i < numberPlanningTimeHorzionFactors; i++) {

				double planningTimeHorzionFactor = planningTimeHorzionFactorStart + (i * 0.5);

				// This will be the statstics output folder.
				String statisticsRootFolder = rootStat + "/" + instanceKindStr + "/" + instanceProviderStr + "/" + instanceNameReal.replace(".xml", "") + "(F="
						+ planningTimeHorzionFactor + ",ss=" + (sampleSize + 2) + ")/";
				new File(statisticsRootFolder).mkdirs();

				// Get the VRP instance (this instance is static)
				Instance instance = instanceProvider.getAvailableInstance(instanceKindStr, instanceProviderStr, instanceNameReal, true, true);
				String instanceName = instanceNameReal.replace(".xml", "");

				VehicleRoutingProblemSolution bestSolutionForStaticInstance = new JspritVRPREPInstanceSolver().solve(instance,
						DynamicStudyMain.create(statisticsRootFolder + "00.InitialInstance"), true);

				// This will be a collection for following statistics.
				RunStatistics runStatistics = new RunStatistics(instanceName, planningTimeHorzionFactor, instance, bestSolutionForStaticInstance, statisticsRootFolder);

				List<IDynamicVRPREPModelGenerationPosibilities> infos = new ArrayList<>();

				double currentDOD = DynamicStudyMain.startDOD;
				while (currentDOD < 1.0) {

					IDynamicVRPREPModelInstanceGenerator dynamicInstanceGenerator = new DynamicVRPREPModelInstanceGenerator();
					dynamicInstanceGenerator.initialize(instance, random);
					IDynamicVRPREPModelGenerationPosibilities posibilities = dynamicInstanceGenerator.generateStatisticInformation(currentDOD,
							numberCombinationsGenerationPosibilitiesAreBasedOn);
					infos.add(posibilities);

					IDynamicVRPREPModelGenerationPosibilities.RunInfo runInfo = new IDynamicVRPREPModelGenerationPosibilities.RunInfo(currentDOD, DynamicStudyMain.edod,
							DynamicStudyMain.numberCombinationsGenerationPosibilitiesAreBasedOn, instanceName, null);

					// only create dynamic models for a dod of 0.5
					if (currentDOD >= recordedDOD && currentDOD < recordedDOD + 0.1) {
						int timeHorizon = (int) (bestSolutionForStaticInstance.getCost() * planningTimeHorzionFactor);
						DynamicVRPREPModel dynamicModelSmallestLOD = dynamicInstanceGenerator.generateInstance(posibilities.getSmallest(), posibilities, null, edod, timeHorizon);
						DynamicVRPREPModel dynamicModelBiggestLOD = dynamicInstanceGenerator.generateInstance(posibilities.getBiggest(), posibilities, null, edod, timeHorizon);
						runStatistics.addDynamicModels(dynamicModelSmallestLOD);
						runStatistics.addDynamicModels(dynamicModelBiggestLOD);

						for (int ss = 0; ss < sampleSize; ss++) {
							DynamicVRPREPModel dynamicModelRandomLDOD1 = dynamicInstanceGenerator
									.generateInstance(dynamicInstanceGenerator.generateRandomValidTmpRequests(posibilities, random), posibilities, null, edod, timeHorizon);

							runStatistics.addDynamicModels(dynamicModelRandomLDOD1);
						}
					}

					new RExporter().exportTo(DynamicStudyMain.create(statisticsRootFolder + "01.StaticModelAnalyses"), posibilities, runInfo);
					currentDOD += 0.1;
					currentDOD = (currentDOD * 100) / 100;
				}

				new RExporterSummary().exportTo(DynamicStudyMain.create(statisticsRootFolder + "01.StaticModelAnalyses"), infos);

				int mo = 1;
				for (DynamicVRPREPModel dynamicModelToSimulate : runStatistics.getDynamicModels().values()) {

					// Generate a simulation model from the dynamic model.
					ISimulationModelAPI simulationModelApi = new SimulationModelAPI();
					VRPSimulationModel simulationModel = simulationModelApi.generateSimulationModel(dynamicModelToSimulate);
					runStatistics.addSimulationModel(dynamicModelToSimulate, simulationModel);

					double dod = runStatistics.getDOD(dynamicModelToSimulate);
					double ldod = runStatistics.getLDOD(dynamicModelToSimulate);
					double edod = runStatistics.getEDOD(dynamicModelToSimulate);

					String subfolder = "02.DynamicModelAnalyses" + "/";
					String folderName = subfolder + "model" + mo + "_dod=" + round(dod, 2) + "_edod=" + round(edod, 2) + "_ldod=" + round(ldod, 2);
					String outputPath = DynamicStudyMain.create(statisticsRootFolder + folderName);

					DynamicVRPREPModel.write(dynamicModelToSimulate, Paths.get(outputPath, "dynamicmodel.xml"));

					// Generate behaviours for the simulation model.
					IInitialBehaviourProviderGenerator initialBehaviourProviderGenerator = new JspritInitialBehaviorProviderGenerator();
					IInitialBehaviourProvider initialBehaviourProvider = initialBehaviourProviderGenerator.generateInitialBehaviourProvider(instanceName + "_InitialBehaviour",
							simulationModel, outputPath);

					String dynamicOutputPath = DynamicStudyMain.create(outputPath + "/DynamicOrderProcessing");
					IDynamicBehaviourProviderGenerator dynamicBehaviourProviderGenerator = new DynamicBehaviourProviderGenerator();
					IDynamicBehaviourProvider dynamicBehaviourProvider = dynamicBehaviourProviderGenerator.generateDynamicBehaviourProvider(instanceName + "_DynamicBehaviour",
							dynamicOutputPath, new CKLCustomerStillToServeSorter());

					RConfig rconfig = new RConfig("dynamicmodel", "Dynamic Model", "y", "x");
					RModel rmodel = RModelTransformator.transformDynamicVRPREPModel(dynamicModelToSimulate);
					new RExporterImpl().export(outputPath, rconfig, rmodel);

					double costs = simulate(simulationModel, initialBehaviourProvider, dynamicBehaviourProvider);
					runStatistics.setCostSimulatedVRPSimulationModel(simulationModel, costs);
					new SimulationResultExporter().exportSimulationResults(simulationModel, outputPath);
					logger.info("Model simulated and simple report generated, costs evaluated by the simulator: {}.", costs);
					mo++;
				}

				runStatistics.exportToR();

				List<RunStatistics.Point> workWith = new ArrayList<>();
				if (normalizedCostsOverLDOD.containsKey(planningTimeHorzionFactor)) {
					workWith = normalizedCostsOverLDOD.get(planningTimeHorzionFactor);
				}
				workWith.addAll(runStatistics.getNormalizedCostsXandLDODY());
				normalizedCostsOverLDOD.put(planningTimeHorzionFactor, workWith);
			}
		}

		new RNormalizedCosts().exportTo(rootStat + "/" + instanceKindStr + "/" + instanceProviderStr + "/", normalizedCostsOverLDOD, recordedDOD);

	}

	public static double round(double wert, int stellen) {
		BigDecimal b = new BigDecimal(wert);
		return b.setScale(stellen, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	private static double simulate(VRPSimulationModel model, IInitialBehaviourProvider initialBehaviourProvider, IDynamicBehaviourProvider dynamicBehaviourProvider)
			throws EventException, InterruptedException, InitializationException {
		SolutionManager solutionManager = new SolutionManager(initialBehaviourProvider);
		solutionManager.setDynamicBehaviourProvider(dynamicBehaviourProvider);
		model.setSolutionManager(solutionManager);
		MainProgramm mp = new MainProgramm();
		mp.run(model);
		return model.getBehaviourService().getTourCosts();
	}

	private static String create(String folder) {
		new File(folder).mkdirs();
		return folder;
	}

}
