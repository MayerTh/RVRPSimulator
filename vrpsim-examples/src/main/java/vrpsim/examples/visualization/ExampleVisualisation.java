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
import vrpsim.core.simulator.MainProgramm;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.generator.api.IArrivalTimeDetermineStrategy.NotInitilizedException;
import vrpsim.dynamicvrprep.model.generator.api.exceptions.InstanceGenerationException;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelRequests;
import vrpsim.dynamicvrprep.model.generator.api.model.GeneratorModelStatisticsInformation;
import vrpsim.dynamicvrprep.model.generator.impl.DynamicVRPREPModelInstanceGenerator;
import vrpsim.dynamicvrprep.model.generator.impl.IndependentPeriodicDistributedArrivalTimeDetermineStrategy;
import vrpsim.simulationmodel.api.ISimulationModelAPI;
import vrpsim.simulationmodel.behaviourimpl.algorithms.AAlgorithm;
import vrpsim.simulationmodel.behaviourimpl.algorithms.MMASUSGreedy;
import vrpsim.simulationmodel.behaviourimpl.algorithms.SpiralGreedy;
import vrpsim.simulationmodel.behaviourimpl.algorithms.TwoOptTwoOpt;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicBehaviourProvider;
import vrpsim.simulationmodel.impl.SimulationModelAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.SimpleInitialBehaviorProvider;
import vrpsim.visualization.Visualisation;
import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI;

public class ExampleVisualisation extends Visualisation {

	private static Logger logger = LoggerFactory.getLogger(ExampleVisualisation.class);

	private static String instanceKind = "staticvrp";
	private static String instanceProvider = "solomon1987_RC1";
	private static String instanceToEvolve = "RC101_050";
//	private static String instanceProvider = "mayer2017";
//	private static String instanceToEvolve = "example_instance_01";


	private static Double dod = 0.3;
	private static int numberDynamicInsgtances = 10;
	private static long seed = 4321;

	public static void main(String[] args) throws InstanceGenerationException, NotInitilizedException, JAXBException, IOException,
			URISyntaxException, InstantiationException, IllegalAccessException {

		logger.info("Get the VRPREP instance provider. See http://www.vrp-rep.org/");
		IVRPREPInstanceProviderAPI provider = new VRPREPInstanceProviderAPI();
		logger.info("Get the concrete instance. kind = {}, provider = {}, instance = {}", instanceKind, instanceProvider, instanceToEvolve);
		
//		for(String p : provider.getAvailableInstanceProvidersForKind(instanceKind)) {
//			System.out.println(p);
//			for(String i : provider.getAvailableInstancesForKindAndProvider(instanceKind, p)) {
//				System.out.println(i);
//			}
//
//		}
		
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
		GeneratorModelRequests gmr = info.getCombinations().get(5);
		DynamicVRPREPModel dynamicModel = dynamicInstancegenerator.generateDynamicVRPREPModelInstance(gmr, info,
				new IndependentPeriodicDistributedArrivalTimeDetermineStrategy(new Double(timeHorizon).intValue()));

		logger.info("Generate simulation model api and simulation model from dynamic model.");
		ISimulationModelAPI simulationModelApi = new SimulationModelAPI();
		VRPSimulationModel simulationModel = simulationModelApi.generateSimulationModel(dynamicModel);

		// My TwoOpt
//		MMASUSGreedy behaviour = new MMASUSGreedy();
		// TwoOptGreedy behaviour = new TwoOptGreedy();
		// TwopOptUS behaviour = new TwopOptUS(4);
		// TwoOptTwoOpt behaviour = new TwoOptTwoOpt(seed);
		// SpiralGreedy behaviour = new SpiralGreedy(8);
		// SpiralUS behaviour = new SpiralUS(8, 4);
		// NNGreedy behaviour = new NNGreedy();
//		SpiralGreedy behaviour = new SpiralGreedy(0.1);
		AAlgorithm behaviour = new TwoOptTwoOpt(seed);

		logger.info("Generate initial behaviour provider with initial behaviour generator provider.");
		// IInitialBehaviourProviderHandler initialHandler = new TwoOptTSPMain();
		// IInitialBehaviourProviderHandler initialHandler = new TSPInitialHandler();
		// IInitialBehaviourProviderHandler initialHandler = new
		// StaubsaugerHandler_v3(7);
		IInitialBehaviourProvider initialBehaviourProvider = new SimpleInitialBehaviorProvider(behaviour);

		logger.info("Generate dynamic behaviour generator provider.");
		IDynamicBehaviourProvider dynamicBehaviourProvider = new DynamicBehaviourProvider(behaviour);
		// IDynamicBehaviourProvider dynamicBehaviourProvider = new
		// DynamicBehaviourProvider(new TSPHandler(TSPMetaSolvingMethod.TWO_OPT, 1234));

		logger.info("Generate behaviour provider and set initial and dynamic behaviour provider.");
		BehaviourProvider behaviourProvider = new BehaviourProvider(initialBehaviourProvider);
		behaviourProvider.setDynamicBehaviourProvider(dynamicBehaviourProvider);
		simulationModel.setBehaviourProvider(behaviourProvider);
		
		simulationModel.shuffelDynamicCustomerArrivals(new Random(seed));
//		simulationModel.shuffelDynamicCustomerArrivals(new Random(seed));
		
//		Behaviour b = behaviourProvider.getBehaviourBeforeInitialization(simulationModel.getStructureService(), simulationModel.getNetworkService());
//		simulationModel.getBehaviourService().reverse(b);

		logger.info("Create main program, init and launch the simulation.");
		MainProgramm mainProgramm = new MainProgramm();
		long simulationEndTime = 10000L;
		init(mainProgramm, simulationModel, simulationEndTime);
		launch(args);
	}

}
