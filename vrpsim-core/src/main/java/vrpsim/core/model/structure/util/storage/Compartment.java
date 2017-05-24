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

public class Compartment implements ICanStore {

	private final CanStoreParameters canStoreParameters;

	public Compartment(final CanStoreParameters canStoreParameters) {
		this.canStoreParameters = canStoreParameters;
	}

	@Override
	public CanStoreParameters getCanStoreParameters() {
		return this.canStoreParameters;
	}

	@Override
	public void load(IStorable storable) throws VRPArithmeticException, StorageException {
		this.canStoreParameters.getLoadingPolicy().load(storable, this.canStoreParameters.getMaxCapacity());
	}

	@Override
	public IStorable unload() throws StorageException {
		return this.canStoreParameters.getLoadingPolicy().unload();
	}

	@Override
	public Capacity getFreeCapacity() throws VRPArithmeticException {
		return this.getCanStoreParameters().getLoadingPolicy()
				.getFreeCapacity(this.getCanStoreParameters().getMaxCapacity());
	}

	@Override
	public Capacity getCurrentCapacity(StorableType storableType) throws VRPArithmeticException {
		return this.canStoreParameters.getLoadingPolicy().getCurrentCapacityOf(storableType);
	}

	@Override
	public Capacity getCurrentCapacity() throws VRPArithmeticException {
		return this.canStoreParameters.getLoadingPolicy().getCurrentCapacity();
	}

	@Override
	public void loadGenerated(int amountToLoad, StorableParameters storableParameters)
			throws VRPArithmeticException, StorageException {
		
		// Throw exception if the type of the storable can not be stored within
		// this CanStoreType.
		if (!storableParameters.getStorableType().getCanStoreTypes()
				.contains(this.getCanStoreParameters().getCanStoreType())) {
			throw new StorageException("The given storable type does not define my CanStoreType as possible storrage."
					+ "I am from type " + storableParameters.getStorableType().getId() + ". The storable is from type "
					+ storableParameters.getStorableType().getId()
					+ ". The storable type defines following possible CanStoreTypes "
					+ storableParameters.getStorableType().getCanStoreTypes().toString() + ".");
		}

		for (int i = 0; i < amountToLoad; i++) {
			this.load(this.canStoreParameters.getStorableGenerator().generateStorable(storableParameters));
		}

	}

	@Override
	public void loadGenerated(int amountToLoad) throws VRPArithmeticException, StorageException {
		this.loadGenerated(amountToLoad, this.canStoreParameters.getStorableGenerator().getDefaultStorableparameters());
		
	}

}
