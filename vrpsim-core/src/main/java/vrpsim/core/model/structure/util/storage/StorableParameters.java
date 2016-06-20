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

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class StorableParameters {

	private final StorableType storableType;
	private final Integer priority;
	private final Capacity capacity;

	public StorableParameters(Integer priority, Capacity capacity, StorableType storableType) {
		this.storableType = storableType;
		this.priority = priority;
		this.capacity = capacity;
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
	 * Returns the capacity of the {@link IStorable} which is owner of the
	 * {@link StorableParameters}.
	 * 
	 * @return
	 */
	public Capacity getCapacity() {
		return capacity;
	}

}
