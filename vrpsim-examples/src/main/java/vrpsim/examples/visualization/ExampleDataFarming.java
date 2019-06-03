package vrpsim.examples.visualization;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.solution.IDynamicBehaviourProvider;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.solution.impl.BehaviourProvider;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.InitializationException;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.generator.api.IArrivalTimeDetermineStrategy.NotInitilizedException;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequests;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelStatisticsInformation;
import vrpsim.dynamicvrprep.model.generator.impl.DynamicVRPREPModelInstanceGenerator;
import vrpsim.dynamicvrprep.model.generator.impl.IndependentPeriodicDistributedArrivalTimeDetermineStrategy;
import vrpsim.simulationmodel.api.ISimulationModelAPI;
import vrpsim.simulationmodel.behaviourimpl.algorithms.MMASUSUS;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicBehaviourProvider;
import vrpsim.simulationmodel.impl.SimulationModelAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.SimpleInitialBehaviorProvider;
import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI;

public class ExampleDataFarming {

	private static Logger logger = LoggerFactory.getLogger(ExampleDataFarming.class);

	private static String instanceKind = "staticvrp";
	private static String instanceProvider = "chris1979";
	private static String instanceToEvolve = "CMT01";
	// private static String instanceProvider = "mayer2017";
	// private static String instanceToEvolve = "example_instance_01";

	private static Double dod = 0.2;
	private static int numberDynamicInsgtances = 1;
	private static long seed = 4321;
	private static int numberRuns = 10;

	public static void main(String[] args)
			throws InstanceGenerationException, NotInitilizedException, JAXBException, IOException, URISyntaxException,
			InstantiationException, IllegalAccessException, EventException, InterruptedException, InitializationException {

		logger.info("Get the VRPREP instance provider. See http://www.vrp-rep.org/");
		IVRPREPInstanceProviderAPI provider = new VRPREPInstanceProviderAPI();
		logger.info("Get the concrete instance. kind = {}, provider = {}, instance = {}", instanceKind, instanceProvider, instanceToEvolve);
		Instance vRPREPInstance = provider.getAvailableInstance(instanceKind, instanceProvider, instanceToEvolve, true, true);

		logger.info(
				"Calculate a time horizon by solving the static instance, needed for arrival time calculation for the dynamic requests.");
		double timeHorizon = new JspritVRPREPInstanceSolver().solve(vRPREPInstance, true).getCost();

		logger.info("Get the dynamic instance generator.");
		DynamicVRPREPModelInstanceGenerator dynamicInstancegenerator = new DynamicVRPREPModelInstanceGenerator();
		logger.info("Create {} dynamic model(s) information for VRPREP instance {} with dod of {}.", numberDynamicInsgtances,
				vRPREPInstance.getInfo().getName(), dod);
		GeneratorModelStatisticsInformation info = dynamicInstancegenerator.initialize(vRPREPInstance, new Random(seed), dod,
				numberDynamicInsgtances);

		logger.info("Generate dynamic model from VRPREP instance with instance information and time horizon {}", timeHorizon);
		GeneratorModelRequests gmr = info.getCombinations().get(0);
		DynamicVRPREPModel dynamicModel = dynamicInstancegenerator.generateDynamicVRPREPModelInstance(gmr, info,
				new IndependentPeriodicDistributedArrivalTimeDetermineStrategy(new Double(timeHorizon).intValue()));

		logger.info("Generate simulation model api and simulation model from dynamic model.");
		ISimulationModelAPI simulationModelApi = new SimulationModelAPI();
		VRPSimulationModel simulationModel = simulationModelApi.generateSimulationModel(dynamicModel);

		MMASUSUS behaviour = new MMASUSUS(true, 4);

		logger.info("Generate initial behaviour provider with initial behaviour generator provider.");
		IInitialBehaviourProvider initialBehaviourProvider1 = new SimpleInitialBehaviorProvider(behaviour);
		logger.info("Generate dynamic behaviour generator provider.");
		IDynamicBehaviourProvider dynamicBehaviourProvider1 = new DynamicBehaviourProvider(behaviour);
		logger.info("Generate behaviour provider and set initial and dynamic behaviour provider.");
		BehaviourProvider behaviourProvider = new BehaviourProvider(initialBehaviourProvider1);
		behaviourProvider.setDynamicBehaviourProvider(dynamicBehaviourProvider1);
		logger.info("Setting the behaviour provider.");
		simulationModel.setBehaviourProvider(behaviourProvider);

		simulate(simulationModel, numberRuns, new Random(seed));

	}

	private static void simulate(VRPSimulationModel model, int number, Random rnd)
			throws EventException, InterruptedException, InitializationException {
		double costs = 0.0;
		double costCounter = 0;
		boolean reversed = false;
		for (int i = 0; i < number * 2; i++) {
			model.reset();
			if (i >= number && !reversed) {
				logger.info("Reverse simulation model.");
				model.reverseBehaviour();
				reversed = true;
			}
			model.shuffelDynamicCustomerArrivals(rnd);

			try {
				MainProgramm mainProgramm = new MainProgramm();
				mainProgramm.run(model);
				costs +=  model.getBehaviourService().getTourCosts().getTravelDistance();
				costCounter++;
				logger.info("Tour costs after simulation for run {} are {}.", i, model.getBehaviourService().getTourCosts());
			} catch (Exception e) {
				logger.warn("Could not simulate model.");
			}
		}
		
		logger.info("Average tour cost is {}, {} tour costs calculated.", costs/costCounter, costCounter);
	}

}
