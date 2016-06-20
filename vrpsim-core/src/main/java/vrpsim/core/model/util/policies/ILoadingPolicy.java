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

import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;

/**
 * Central element in the storage model. The {@link ILoadingPolicy} manages the
 * {@link IStorable} for an {@link ICanStore} (e.g. {@link Compartment}).
 * 
 * 
 * 
 * @author mayert
 */
public interface ILoadingPolicy {

	/**
	 * Load one {@link IStorable} into
	 * 
	 * @param storable
	 * @param maxCapacity
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(IStorable storable, Capacity maxCapacity) throws VRPArithmeticException, StorageException;

	/**
	 * Unloads and returns the next {@link IStorable}.
	 * 
	 * @return
	 * @throws StorageException
	 */
	public IStorable unload() throws StorageException;

	/**
	 * Returns the number of loaded {@link IStorable}.
	 * 
	 * @return
	 */
	public int getCurrentNumberOfStorable();

	/**
	 * Returns the current {@link Capacity} independent of the
	 * {@link StorableType}. Different types of {@link IStorable} defined by
	 * {@link StorableType} can be inside.
	 * 
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getCurrentCapacity() throws VRPArithmeticException;

	/**
	 * Returns the current {@link Capacity}. Different types of
	 * {@link IStorable} defined by {@link StorableType} can be inside. The
	 * method only returns the {@link Capacity} of the given
	 * {@link StorableType}.
	 * 
	 * @param storableType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getCurrentCapacityOf(StorableType storableType) throws VRPArithmeticException;

	/**
	 * Returns the available {@link Capacity}.
	 * 
	 * @param maxCapacity
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getFreeCapacity(Capacity maxCapacity) throws VRPArithmeticException;
	
}
