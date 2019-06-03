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
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.spiral.StaubsaugerHandler_v3;

public class Spiral8x8Greedy extends AAlgorithm {

	private final StaubsaugerHandler_v3 handler;

	public Spiral8x8Greedy() {
		this.handler = new StaubsaugerHandler_v3(new Integer(8));
		this.handler.setRasterSize(8);
		this.handler.setMaximumNumberInRasterCellPercentage(-1);
	}

	@Override
	public void handleDynamicOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {
		// Dynamic Problem
		new GreedyHandler().hanldeOrder(currentTourStates, newOrder, clock, structureService, networkService);
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		// Static Problem
		return this.handler.handleOrder(vehicles, customers, depots, drivers);
	}

}
