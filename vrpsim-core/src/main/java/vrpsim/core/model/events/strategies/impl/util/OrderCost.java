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
package vrpsim.core.model.events.strategies.impl.util;

public class OrderCost {

	public static String DEFAULT_ORDER_COST_UNIT = "EURO";

	private final String unit;
	private final Double value;

	/**
	 * Creates {@link OrderCost} with default unit
	 * {@link OrderCost#DEFAULT_ORDER_COST_UNIT} and value 0.0.
	 */
	public OrderCost() {
		this.unit = OrderCost.DEFAULT_ORDER_COST_UNIT;
		this.value = 0.0;
	}

	/**
	 * Creates {@link OrderCost} with default unit
	 * {@link OrderCost#DEFAULT_ORDER_COST_UNIT}.
	 * 
	 * @param value
	 */
	public OrderCost(Double value) {
		this.value = value;
		this.unit = OrderCost.DEFAULT_ORDER_COST_UNIT;
	}

	/**
	 * Creates {@link OrderCost} with custom unit and value.
	 * 
	 * @param unit
	 * @param value
	 */
	public OrderCost(String unit, Double value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * Returns the unit.
	 * 
	 * @return
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Returns the value.
	 * 
	 * @return
	 */
	public Double getValue() {
		return value;
	}

}
