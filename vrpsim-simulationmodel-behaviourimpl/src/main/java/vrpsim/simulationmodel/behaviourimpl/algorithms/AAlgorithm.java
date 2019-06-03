package vrpsim.simulationmodel.behaviourimpl.algorithms;

import java.util.List;

import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.IDynamicBehaviourProviderHandler;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;

public abstract class AAlgorithm implements IDynamicBehaviourProviderHandler, IInitialBehaviourProviderHandler {

	private boolean dynamicBehavior = true;

	public String getId() {
		return this.getClass().getSimpleName();
	}

	public void deactivateDynamicBehavior() {
		this.dynamicBehavior = false;
	}

	public void activateDynamicBehavior() {
		this.dynamicBehavior = true;
	}

	@Override
	public final void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {
		if(this.dynamicBehavior) {
			this.handleDynamicOrder(currentTourStates, newOrder, clock, structureService, networkService);
		}
	}

	public abstract void handleDynamicOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException;

}
