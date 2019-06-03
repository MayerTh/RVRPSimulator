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
package vrpsim.core.model.util.policies;

import java.util.List;

import vrpsim.core.model.structure.storage.ICanStore;
import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.structure.storage.impl.Compartment;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;

/**
 * Central element in the storage model. The {@link ILoadingPolicy} manages the
 * {@link IStorable} for an {@link ICanStore} (e.g. {@link Compartment}).
 * 
 * @author mayert
 */
public interface ILoadingPolicy {

	/**
	 * Load one {@link IStorable}.
	 * 
	 * @param storable
	 * @param maxCapacity
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(IStorable storable);

	/**
	 * Loads {@link IStorable}'s.
	 * 
	 * @param storable
	 * @param maxCapacity
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(List<IStorable> storables);
	
	/**
	 * Loads the given number of storables.
	 * 
	 * @param number
	 */
	public void load(StorableParameters storableParameters, int number);

	/**
	 * Unloads and returns the next {@link IStorable}. If empty it returns null.
	 * 
	 * @return
	 * @throws StorageException
	 */
	public IStorable unload();

	/**
	 * Unloads and returns the number of {@link IStorable}. If not enough inside,
	 * null will returned.
	 * 
	 * @return
	 * @throws StorageException
	 */
	public List<IStorable> unload(StorableParameters storableParameters, int number);

	/**
	 * Returns the number of loaded {@link IStorable}.
	 * 
	 * @return
	 */
	public int getCurrentNumberOfStorablesInside();

	/**
	 * Returns the value of the capacity inside. Every {@link IStorable} has a
	 * capacity factor which is considered.
	 * 
	 * @return
	 */
	public double getCapacityInside();
	
	/**
	 * Returns the value of the capacity inside. Every {@link IStorable} has a
	 * capacity factor which is considered.
	 * 
	 * @return
	 */
	public double getCapacityInside(StorableParameters storableParameters);

	public void reset();

}
