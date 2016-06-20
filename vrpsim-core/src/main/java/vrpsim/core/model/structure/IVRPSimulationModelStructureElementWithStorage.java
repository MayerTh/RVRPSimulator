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
package vrpsim.core.model.structure;

import java.util.List;

import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;

/**
 * @date 18.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IVRPSimulationModelStructureElementWithStorage extends IVRPSimulationModelStructureElement {

	/**
	 * Add one {@link IStorable} into the storage.
	 * 
	 * @param storable
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(IStorable storable) throws VRPArithmeticException, StorageException;

	/**
	 * Remove and return one {@link IStorable} with given {@link StorableType}
	 * from the storage.
	 * 
	 * @param storableType
	 * @return
	 * @throws StorageException
	 */
	public IStorable unload(StorableType storableType) throws StorageException;

	/**
	 * Returns true if the storage still has place for the given capacity.
	 * 
	 * @param storableType
	 * @param capacityToStore
	 * @return
	 * @throws VRPArithmeticException
	 */
	public boolean canStore(StorableType storableType, Capacity capacityToStore) throws VRPArithmeticException;

	/**
	 * Returns the current available amount as {@link Capacity} of the given
	 * {@link StorableType}.
	 * 
	 * @param storableType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getCurrentCapacity(StorableType storableType) throws VRPArithmeticException;

	/**
	 * Returns the unused {@link Capacity} of the {@link ICanStore} defined by
	 * given {@link CanStoreType}.
	 * 
	 * @param canStoreType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getFreeCapacity(CanStoreType canStoreType) throws VRPArithmeticException;

	/**
	 * Returns the current amount of {@link IStorable} in {@link ICanStore}
	 * defined by given {@link CanStoreType}.
	 * 
	 * Note, that the returned value is calculated independent from
	 * {@link StorableType}. Example: if in the {@link ICanStore} are one
	 * {@link IStorable} from type {@link StorableType} 'apple' and another
	 * {@link IStorable} from type {@link StorableType} 'cucumber', the method
	 * returns two.
	 * 
	 * @param canStoreType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getCurrentCapacity(CanStoreType canStoreType) throws VRPArithmeticException;

	/**
	 * Returns a list of all {@link CanStoreType} managed by the
	 * {@link IVRPSimulationModelStructureElementWithStorage}.
	 * 
	 * @return
	 */
	public List<CanStoreType> getAllCanStoreTypes();

}
