package vrpsim.simulationmodel.dynamicbehaviour;

import java.util.List;

import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

public interface IDynamicBehaviourProviderHandler {

	/**
	 * {@link TourState} holds the current state of the tour (customer visits). A
	 * new customer visit {@link BringTo} newOrder has to be introduced
	 * into the existing {@link TourState#getCustomersStillToServe()}.
	 * 
	 * @param newOrder
	 */
	public void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException;

}
