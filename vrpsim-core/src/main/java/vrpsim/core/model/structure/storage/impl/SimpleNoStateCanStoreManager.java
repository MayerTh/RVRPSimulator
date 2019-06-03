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

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.structure.storage.ICanStore;
import vrpsim.core.model.structure.storage.ICanStoreManager;
import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.policies.impl.EndlessNoStateLoadingPolicy;

public class SimpleNoStateCanStoreManager implements ICanStoreManager {

	private final ICanStore canStore;

	public SimpleNoStateCanStoreManager(StorableParameters storableParameters) {
		canStore = new ICanStore() {
			@Override
			public CanStoreParameters getCanStoreParameters() {
				CanStoreParameters csp = new CanStoreParameters(new CanStoreType("SimpleNoStateCanStoreManagerCanStoreType"), -1,
						new EndlessNoStateLoadingPolicy(new StorableGenerator(storableParameters)));
				return csp;
			}

			@Override
			public void reset() {
				// DO nothing.
			}
		};
	}
	public void reset() {
		// Do nothing.
	}

	@Override
	public boolean canLoad(StorableParameters storableParameters, int number) {
		return true;
	}

	@Override
	public boolean canUnload(StorableParameters storableParameters, int number) {
		return true;
	}

	@Override
	public void load(IStorable storable) throws StorageException {
		this.canStore.getCanStoreParameters().getLoadingPolicy().load(storable);
	}

	@Override
	public void load(List<IStorable> storables) throws StorageException {
		this.canStore.getCanStoreParameters().getLoadingPolicy().load(storables);
	}

	@Override
	public void load(StorableParameters storableParameters, int number) throws StorageException {
		this.canStore.getCanStoreParameters().getLoadingPolicy().load(storableParameters, number);
	}

	@Override
	public IStorable unload(StorableParameters storableParameters) throws StorageException {
		return this.canStore.getCanStoreParameters().getLoadingPolicy().unload();
	}

	@Override
	public List<IStorable> unload(StorableParameters storableParameters, int number) throws StorageException {
		return this.canStore.getCanStoreParameters().getLoadingPolicy().unload(storableParameters, number);
	}

	@Override
	public double getCurrentCapacity(StorableParameters storableParameters) {
		return this.canStore.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
	}

	@Override
	public double getFreeCapacity(StorableParameters storableParameters) {
		return Double.MAX_VALUE;
	}

	@Override
	public double getFreeCapacity(CanStoreType canStoreType, StorableParameters storableParameters) {
		return Double.MAX_VALUE;
	}

	@Override
	public double getCurrentCapacity(CanStoreType canStoreType, StorableParameters storableParameters) {
		return this.canStore.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
	}

	@Override
	public List<CanStoreType> getAllCanStoreTypes() {
		List<CanStoreType> result = new ArrayList<>();
		result.add(this.canStore.getCanStoreParameters().getCanStoreType());
		return result;
	}

	@Override
	public double getMaxCapacity(CanStoreType canStoreType) {
		return Double.MAX_VALUE;
	}

	@Override
	public double getCurrentCapacity(CanStoreType canStoreType) {
		return this.canStore.getCanStoreParameters().getLoadingPolicy().getCapacityInside();
	}

	@Override
	public double getFreeCapacity(CanStoreType canStoreType) {
		return Double.MAX_VALUE;
	}

}