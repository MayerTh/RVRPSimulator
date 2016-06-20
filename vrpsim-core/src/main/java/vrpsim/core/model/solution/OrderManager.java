/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.solution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.simulator.IClock;

public class OrderManager extends AbstractOrderManager {

	Logger logger = LoggerFactory.getLogger(OrderManager.class);
	
	@Override
	public void handleOrderEvent(OrderEvent orderEvent, IClock simulationClock) {
		logger.debug("Start handling order event with order {}.", orderEvent.getOrder().getId());
		
		this.orderBord.publishOrder(orderEvent.getOrder());
		// TODO
		// 		* set source of storabel on order. 
	}

	@Override
	public void handleNotTakenOrder(Order order) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver) {
		// TODO Auto-generated method stub
		
	}

}
