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
package vrpsim.core.model.structure.util.storage;

import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.InvalidOperationForCapacity;

public class Capacity {

	/**
	 * The {@link UNKNOWN_CAPACITY_UNIT} is compatible with all other
	 * {@link Capacity#getUnit()}.
	 */
	public static final String UNKNOWN_CAPACITY_UNIT = "unknown unit";

	private String unit;
	private final Double value;

	public Capacity(String unit, Double value) {
		this.unit = unit;
		this.value = value;
	}

	public Double getValue() {
		return this.value;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Capacity add(Capacity capacity) throws VRPArithmeticException {
		String newUnit = checkCapacityType(capacity);
		return new Capacity(newUnit, new Double(this.value + capacity.getValue()));
	}

	public Capacity sub(Capacity capacity) throws VRPArithmeticException {
		String newUnit = checkCapacityType(capacity);
		return new Capacity(newUnit, new Double(this.value - capacity.getValue()));
	}

	public Capacity mul(Double number) {
		return new Capacity(unit, new Double(this.value * number));
	}

	public boolean isSmaller(Capacity capacity) throws VRPArithmeticException {
		checkCapacityType(capacity);
		return this.value < capacity.getValue();
	}

	public boolean isSmallerOrEqual(Capacity capacity) throws VRPArithmeticException {
		checkCapacityType(capacity);
		return this.value <= capacity.getValue();
	}

	/*
	 * If the type of the capacity is Capacity.UNKNOWN_CAPACITY_UNIT, the unit
	 * can be ignored.
	 */
	private String checkCapacityType(Capacity capacity) throws InvalidOperationForCapacity {
		if (!capacity.getUnit().equals(this.unit)) {
			if (!this.unit.equals(Capacity.UNKNOWN_CAPACITY_UNIT)) {
				if (!capacity.getUnit().equals(UNKNOWN_CAPACITY_UNIT)) {
					throw new InvalidOperationForCapacity("Can not add capacity for different types {"
							+ capacity.getUnit() + ", " + this.unit + "}.");
				}
			}
		}
		return !this.unit.equals(Capacity.UNKNOWN_CAPACITY_UNIT) ? this.unit : capacity.getUnit();
	}

}
