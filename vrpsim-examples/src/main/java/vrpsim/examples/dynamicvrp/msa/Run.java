package vrpsim.examples.dynamicvrp.msa;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.solution.AbstractOrderManager;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.solution.OrderState;
import vrpsim.core.model.solution.SolutionManager;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.util.model.instances.generator.bent.BentInstanceLoader;
import vrpsim.visualization.Visualisation;

public class Run extends Visualisation {

	public static void main(String[] args) throws IOException, URISyntaxException {

		BentInstanceLoader bil = new BentInstanceLoader();
		String path = bil.getAvailablePathsToBentInstances().get(0);
		VRPSimulationModel model = bil.loadBentInstance(path);

		IInitialBehaviourProvider stb = new IInitialBehaviourProvider() {
			
			@Override
			public Behaviour provideBehavior(NetworkService networkService, StructureService structureService) {
				List<ITour> tours = new ArrayList<>();
				return new Behaviour(tours);
			}
		};
		
		SolutionManager sm = new SolutionManager(stb);
		sm.setDynamicBehaviourProvider(new AbstractOrderManager() {

			@Override
			public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleNotTakenOrder(Order order) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock) {
				System.out.println("###########################");
				System.out.println("SIMU TIME = " + simulationClock.getCurrentSimulationTime().getDoubleValue());
				System.out.println("Order menge = " + orderEvent.getOrder().getAmount() + ", order kunde = "
						+ orderEvent.getOrder().getOwner().getVRPSimulationModelElementParameters().getId());
			}
		});
		model.setSolutionManager(sm);

		init(new MainProgramm(), model, new Clock.Time(1000.0));
		launch(args);

	}

}
