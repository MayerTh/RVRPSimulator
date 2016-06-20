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
package vrpsim.core.model.util.policies;

import java.util.EmptyStackException;
import java.util.Stack;

import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.StorageOutOfStockException;
import vrpsim.core.model.util.exceptions.detail.StorageOverflowException;

/**
 * Implements a {@code Stack}.
 * 
 * @date 29.01.2016
 * @author thomas.mayer@unibw.de
 */
public class LIFOLoadingPolicy implements ILoadingPolicy {

	private Stack<IStorable> stack = new Stack<IStorable>();

	@Override
	public void load(IStorable storable, Capacity maxCapacity) throws VRPArithmeticException, StorageException {
		if (!this.getCurrentCapacityOf(storable.getStorableParameters().getStorableType())
				.add(storable.getStorableParameters().getCapacity()).isSmallerOrEqual(maxCapacity)) {
			throw new StorageOverflowException(
					"Not enough capacity to store " + storable.toString() + ". Current capacity: "
							+ this.getCurrentCapacityOf(storable.getStorableParameters().getStorableType()).getValue()
							+ " Maximum capacity: " + maxCapacity.getValue());
		}
		stack.push(storable);
	}

	@Override
	public Capacity getFreeCapacity(Capacity maxCapacity) throws VRPArithmeticException {
		Capacity all = this.getCurrentCapacity();
		if (all.getUnit().equals(Capacity.UNKNOWN_CAPACITY_UNIT)) {
			return maxCapacity;
		} else {
			return new Capacity(maxCapacity.getUnit(), maxCapacity.getValue() - all.getValue());
		}
	}

	@Override
	public IStorable unload() throws StorageOutOfStockException {
		IStorable storable = null;
		try {
			storable = stack.pop();
		} catch (EmptyStackException ese) {
			throw new StorageOutOfStockException("Storage is empty.", 1.0);
		}
		return storable;
	}

	@Override
	public Capacity getCurrentCapacityOf(StorableType storableType) throws VRPArithmeticException {
		Capacity currentCapacity;
		if (this.stack.isEmpty()) {
			currentCapacity = new Capacity(Capacity.UNKNOWN_CAPACITY_UNIT, 0.0);
		} else {
			currentCapacity = new Capacity(this.stack.peek().getStorableParameters().getCapacity().getUnit(), 0.0);
			for (IStorable storable : this.stack) {
				if (storable.getStorableParameters().getStorableType().equals(storableType)) {
					currentCapacity = currentCapacity.add(storable.getStorableParameters().getCapacity());
				}
			}

		}
		return currentCapacity;
	}

	@Override
	public Capacity getCurrentCapacity() throws VRPArithmeticException {
		Capacity currentCapacity;
		if (this.stack.isEmpty()) {
			currentCapacity = new Capacity(Capacity.UNKNOWN_CAPACITY_UNIT, 0.0);
		} else {
			currentCapacity = new Capacity(this.stack.peek().getStorableParameters().getCapacity().getUnit(), 0.0);
			for (IStorable storable : this.stack) {
				currentCapacity = currentCapacity.add(storable.getStorableParameters().getCapacity());
			}

		}
		return currentCapacity;
	}

	@Override
	public int getCurrentNumberOfStorable() {
		return this.stack.size();
	}

}
