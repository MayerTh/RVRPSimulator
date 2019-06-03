package vrpsim.examples.visualization;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.xml.sax.SAXException;

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
import vrpsim.simulationmodel.behaviourimpl.algorithms.SpiralGreedy;
import vrpsim.simulationmodel.behaviourimpl.algorithms.TwoOptGreedy;
import vrpsim.simulationmodel.behaviourimpl.algorithms.TwoOptTwoOpt;
import vrpsim.simulationmodel.behaviourimpl.algorithms.TwopOptUS;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicBehaviourProvider;
import vrpsim.simulationmodel.impl.SimulationModelAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.SimpleInitialBehaviorProvider;
import vrpsim.visualization.Visualisation;
import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI;

public class ExampleModelVisualisation extends Visualisation {

	private static Logger logger = LoggerFactory.getLogger(ExampleModelVisualisation.class);

	private static long seed = 4321;

	public static void main(String[] args)
			throws InstanceGenerationException, NotInitilizedException, JAXBException, IOException, URISyntaxException,
			InstantiationException, IllegalAccessException, XMLStreamException, FactoryConfigurationError, SAXException {

		// DynamicVRPREPModel dynamicModel = DynamicVRPREPModel.load("models",
		// "4321_CMT02_TwoOptGreedy_0.2.xml", "Test");
		DynamicVRPREPModel dynamicModel = DynamicVRPREPModel.load("models", "error_model.xml", "Test");

		logger.info("Generate simulation model api and simulation model from dynamic model.");
		ISimulationModelAPI simulationModelApi = new SimulationModelAPI();
		VRPSimulationModel simulationModel = simulationModelApi.generateSimulationModel(dynamicModel);

		// My TwoOpt
		// MMASUSGreedy behaviour = new MMASUSGreedy();
		// TwoOptGreedy behaviour = new TwoOptGreedy();
		// TwopOptUS behaviour = new TwopOptUS(4);
		// TwoOptTwoOpt behaviour = new TwoOptTwoOpt(seed);
		// SpiralGreedy behaviour = new SpiralGreedy(8);
		// SpiralUS behaviour = new SpiralUS(8, 4);
		// NNGreedy behaviour = new NNGreedy();
		TwoOptTwoOpt behaviour = new TwoOptTwoOpt(seed);

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

		Random rnd = new Random(seed);

		simulationModel.reset();
		simulationModel.reverseBehaviour();
		simulationModel.shuffelDynamicCustomerArrivals(rnd);
		// simulationModel.reset();
		// simulationModel.shuffelDynamicCustomerArrivals(rnd);

		// Behaviour b =
		// behaviourProvider.getBehaviourBeforeInitialization(simulationModel.getStructureService(),
		// simulationModel.getNetworkService());
		// simulationModel.getBehaviourService().reverse(b);

		logger.info("Create main program, init and launch the simulation.");
		MainProgramm mainProgramm = new MainProgramm();
		long simulationEndTime = 10000L;
		init(mainProgramm, simulationModel, simulationEndTime);
		launch(args);
	}

}
