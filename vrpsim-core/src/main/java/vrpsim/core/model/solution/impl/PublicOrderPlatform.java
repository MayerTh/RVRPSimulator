/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.solution.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.solution.IDynamicBehaviourProvider;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.occasionaldriver.impl.OccasionalDriver;

/**
 * The {@link PublicOrderPlatform} can be understood as board, where orders can be published for public
 * 
 * @author mayert
 */
public class PublicOrderPlatform extends Observable {

	private final IDynamicBehaviourProvider owner;
	private Set<Order> orders = new HashSet<>();

	public PublicOrderPlatform(IDynamicBehaviourProvider owner) {
		this.owner = owner;
	}

	/**
	 * Publish an order, all modeled {@link IOccasionalDriver} are informed
	 * about the published orders.
	 * 
	 * @param order
	 */
	public void publishOrder(Order order) {
		this.orders.add(order);

		this.setChanged();
		this.notifyObservers(order);

		if (this.orders.contains(order)) {
			// order is not removed from list, so no OD was interested into the
			// order.
			this.owner.handleNotTakenOrder(order);
			this.confirmOrder(order, null);
		}

	}

	/**
	 * An {@link IOccasionalDriver} should only serve {@link Order}s, which are
	 * confirmed. If the method returns false, than an other
	 * {@link OccasionalDriver} is serving the order already.
	 * 
	 * @param order
	 * @param occasionalDriver
	 * @return
	 */
	public boolean confirmOrder(Order order, IOccasionalDriver occasionalDriver) {
		boolean result = false;
		if (this.orders.contains(order)) {
			this.orders.remove(order);

			if (occasionalDriver != null) {
				this.owner.handleTakenOrder(order, occasionalDriver);
			}

			result = true;
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * Returns a set of all {@link Order} currently not confirmed (means not
	 * taken by any {@link IOccasionalDriver}).
	 * 
	 * @return
	 */
	public Set<Order> readAllUnconfirmedOrders() {
		return Collections.unmodifiableSet(this.orders);
	}

}
