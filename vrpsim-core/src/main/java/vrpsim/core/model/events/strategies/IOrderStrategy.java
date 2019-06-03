package vrpsim.core.model.events.strategies;

import java.util.List;

import vrpsim.core.model.events.IEventStrategy;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;

public interface IOrderStrategy extends IEventStrategy {

	/**
	 * Returns true if there are dynamic events.
	 * 
	 * @return
	 */
	public boolean hasDynamicEvents();
	
	/**
	 * Returns the static orders. Returns null if no static {@link Order} for the
	 * element exists.
	 * 
	 * @return
	 */
	public List<Order> getStaticOrders(IVRPSimulationModelStructureElementWithStorage withStorage);

	/**
	 * Returns the {@link OrderEvent} for the dynamic orders. Returns null if no
	 * dynamic {@link Order} for the element exists.
	 * 
	 * @param withStorage
	 * @return
	 */
	public OrderEvent getNextDynamicOrder(IVRPSimulationModelStructureElementWithStorage withStorage);
	
	public void reset();
	
	public void setStartTime(double startTime);
	
	public double getStartTime();

}
