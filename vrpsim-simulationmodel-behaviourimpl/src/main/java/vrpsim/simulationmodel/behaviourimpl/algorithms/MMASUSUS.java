package vrpsim.simulationmodel.behaviourimpl.algorithms;

import java.util.List;

import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.impl.US;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus.MMASUS;

public class MMASUSUS extends AAlgorithm {

	private final boolean applyLocalOptimizationForEveryBestAnt;
	private final int neighbourhoodSize;
	
	public MMASUSUS(boolean applyLocalOptimizationForEveryBestAnt, int neighbourhoodSize) {
		super();
		this.applyLocalOptimizationForEveryBestAnt = applyLocalOptimizationForEveryBestAnt;
		this.neighbourhoodSize = neighbourhoodSize;
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
		new US(neighbourhoodSize).hanldeOrder(currentTourStates, newOrder, clock, structureService, networkService);
	}

}
