package vrpsim.simulationmodel.behaviourimpl.algorithms;

import java.util.List;

import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.impl.GreedyHandler;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus.MMASUS;

public class MMASUSGreedy extends AAlgorithm {

	private final boolean applyLocalOptimizationForEveryBestAnt;
	
	public MMASUSGreedy(boolean applyLocalOptimizationForEveryBestAnt) {
		this.applyLocalOptimizationForEveryBestAnt = applyLocalOptimizationForEveryBestAnt;
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		// Static problem
		return new MMASUS(this.applyLocalOptimizationForEveryBestAnt).handleOrder(vehicles, customers, depots, drivers);
	}

	@Override
	public void handleDynamicOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {
		// Dynamic problem
		new GreedyHandler().hanldeOrder(currentTourStates, newOrder, clock, structureService, networkService);
	}

}
