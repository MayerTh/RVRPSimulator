package vrpsim.examples.dynamiccustomer.solution;

import vrpsim.core.model.behaviour.Tour;
import vrpsim.core.model.behaviour.TourContext;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.solution.AbstractOrderManager;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.simulator.IClock;

public class SimpleOrderManager extends AbstractOrderManager {

	@Override
	public void handleNotTakenOrder(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock) {
		
//		this.structureService.
//		TourContext tourContext = new TourContext(simulationClock.getCurrentSimulationTime()
//				, vehicle, driver);
//		
//		Tour tour = new Tour();
		
	}

}
