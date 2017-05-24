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
package vrpsim.core.model.structure.util.storage;

import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.policies.ILoadingPolicy;;

public interface ICanStore {

	/**
	 * Returns the {@link CanStoreParameters} defining the {@link ICanStore}.
	 * 
	 * @return
	 */
	public CanStoreParameters getCanStoreParameters();

	/**
	 * Load a {@link IStorable} of the {@link ICanStore} after the
	 * {@link ILoadingPolicy} defined within the {@link CanStoreParameters}.
	 * 
	 * @param storable
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(IStorable storable) throws VRPArithmeticException, StorageException;

	/**
	 * Unload a {@link IStorable} of the {@link ICanStore} after the
	 * {@link ILoadingPolicy} defined within the {@link CanStoreParameters}.
	 * 
	 * @return
	 * @throws StorageException
	 */
	public IStorable unload() throws StorageException;

	/**
	 * Returns the unused {@link Capacity} of this {@link ICanStore}.
	 * 
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getFreeCapacity() throws VRPArithmeticException;

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
	 * Returns the current available amount as {@link Capacity} independent of
	 * {@link StorableType}.
	 * 
	 * @param storableType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public Capacity getCurrentCapacity() throws VRPArithmeticException;

	/**
	 * Creates the defined amount of {@link IStorable} and loads them. They are
	 * generated with the help of {@link IStorableGenerator} and loaded into the
	 * {@link ILoadingPolicy}.
	 * 
	 * @param amountToLoad
	 * @param storableParameters
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void loadGenerated(int amountToLoad, StorableParameters storableParameters)
			throws VRPArithmeticException, StorageException;

	/**
	 * Creates the defined amount of default {@link IStorable} and loads them.
	 * {@link IStorableGenerator} holds a instance of default
	 * {@link StorableParameters}. They are generated with the help of
	 * {@link IStorableGenerator} and loaded into the {@link ILoadingPolicy}.
	 * 
	 * @param amountToLoad
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void loadGenerated(int amountToLoad) throws VRPArithmeticException, StorageException;

}
