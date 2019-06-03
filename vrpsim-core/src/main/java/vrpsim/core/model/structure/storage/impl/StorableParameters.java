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
package vrpsim.core.model.structure.storage.impl;

import java.util.HashSet;
import java.util.Set;

import vrpsim.core.model.structure.storage.IStorable;

/**
 * Parameters of an {@link IStorable}.
 * 
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class StorableParameters {

	private final StorableType storableType;
	private final Set<CanStoreType> canStoreTypes;
	private final int priority;
	private final double capacityFactor;

	public StorableParameters(int priority, double capacityFactor, StorableType storableType, CanStoreType canStoreType) {
		this.storableType = storableType;
		this.priority = priority;
		this.capacityFactor = capacityFactor;
		this.canStoreTypes = new HashSet<>();
		this.canStoreTypes.add(canStoreType);
	}

	public StorableParameters(int priority, StorableType storableType, Set<CanStoreType> canStoreTypes) {
		this.storableType = storableType;
		this.priority = priority;
		this.capacityFactor = 1.0;
		this.canStoreTypes = canStoreTypes;
	}

	public StorableParameters(int priority, StorableType storableType) {
		this.storableType = storableType;
		this.priority = priority;
		this.capacityFactor = 1.0;
		this.canStoreTypes = new HashSet<>();
	}

	/**
	 * Returns true if the owner of the {@link StorableParameters} can be stored
	 * within the given {@link CanStoreType}. 
	 * 
	 * @param canStoreType
	 * @return
	 */
	public boolean canBeStoredInType(CanStoreType canStoreType) {
		return this.canStoreTypes.isEmpty() || this.canStoreTypes.contains(canStoreType);
	}

	/**
	 * Returns the type of the {@link IStorable}.
	 * 
	 * @return
	 */
	public StorableType getStorableType() {
		return this.storableType;
	}

	/**
	 * Returns the priority of the {@link IStorable} which is owner of the
	 * {@link StorableParameters}.
	 * 
	 * @return
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * Returns the capacity factor of the {@link IStorable} which is owner of the
	 * {@link StorableParameters}.
	 * 
	 * @return
	 */
	public double getCapacityFactor() {
		return capacityFactor;
	}

	@Override
	public String toString() {
		return "SP[type=" + this.getStorableType().getId() + "]";
	}
	
}
