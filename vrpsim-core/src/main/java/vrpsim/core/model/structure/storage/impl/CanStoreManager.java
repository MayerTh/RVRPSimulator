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
import vrpsim.core.model.util.exceptions.detail.StorageOutOfStockException;
import vrpsim.core.model.util.exceptions.detail.StorageOverflowException;

public class CanStoreManager implements ICanStoreManager {

	private final List<ICanStore> canStores;

	public CanStoreManager(final ICanStore canStore) {
		this.canStores = new ArrayList<ICanStore>();
		this.canStores.add(canStore);
	}

	public CanStoreManager(final List<ICanStore> canStore) {
		this.canStores = canStore;
	}
	
	public void reset() {
		canStores.stream().forEach(e -> e.reset());
	}

	private List<ICanStore> filter(StorableParameters storableParameters) {
		List<ICanStore> tmp = new ArrayList<>();
		for (ICanStore canStore : this.canStores) {
			if (storableParameters.canBeStoredInType(canStore.getCanStoreParameters().getCanStoreType())) {
				tmp.add(canStore);
			}
		}
		return tmp;
	}

	@Override
	public boolean canLoad(StorableParameters storableParameters, int number) {

		double capaFactor = storableParameters.getCapacityFactor();
		double toStore = capaFactor * number;
		List<ICanStore> tmp = filter(storableParameters);

		boolean result = false;
		double hasToStore = toStore;
		for (ICanStore canStore : tmp) {
			int maxCapa = canStore.getCanStoreParameters().getMaxCapacity();
			double capaInside = canStore.getCanStoreParameters().getLoadingPolicy().getCapacityInside();
			double left = new Double(maxCapa) - capaInside;
			hasToStore = -left;
			if (hasToStore <= 0) {
				result = true;
				break;
			}
		}

		return result;
	}

	@Override
	public boolean canUnload(StorableParameters storableParameters, int number) {
		List<ICanStore> tmp = filter(storableParameters);
		boolean result = false;
		int inside = 0;
		for (ICanStore canStore : tmp) {
			inside += canStore.getCanStoreParameters().getLoadingPolicy().getCurrentNumberOfStorablesInside();
			if (inside >= number) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public void load(IStorable storable) throws StorageException {
		if (!canLoad(storable.getStorableParameters(), 1)) {
			throw new StorageOverflowException("Can not load " + storable + ".");
		} else {
			List<ICanStore> tmp = filter(storable.getStorableParameters());
			for (ICanStore ics : tmp) {
				double capaInside = ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside();
				if (capaInside + (1 * storable.getStorableParameters().getCapacityFactor()) < ics.getCanStoreParameters()
						.getMaxCapacity()) {
					ics.getCanStoreParameters().getLoadingPolicy().load(storable);
					break;
				}
			}
		}
	}

	@Override
	public void load(List<IStorable> storables) throws StorageException {
		// storables.forEach(e -> load(e));
		for (IStorable storable : storables) {
			load(storable);
		}
	}

	@Override
	public void load(StorableParameters storableParameters, int number) throws StorageException {
		if (!canLoad(storableParameters, number)) {
			throw new StorageOverflowException("Can not load " + number + " from type " + storableParameters.getStorableType() + ".");
		} else {
			int numberStillToLoad = number;
			List<ICanStore> tmp = filter(storableParameters);
			for (ICanStore ics : tmp) {
				double capaInside = ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
				double maxCapa = ics.getCanStoreParameters().getMaxCapacity();
				double capaDiff = maxCapa - capaInside;
				if (capaDiff > 0) {
					int canFillInside = (int) (capaDiff / storableParameters.getCapacityFactor());
					if (canFillInside >= numberStillToLoad) {
						ics.getCanStoreParameters().getLoadingPolicy().load(storableParameters, numberStillToLoad);
						numberStillToLoad = 0;
						break;
					} else {
						ics.getCanStoreParameters().getLoadingPolicy().load(storableParameters, canFillInside);
						numberStillToLoad = -canFillInside;
					}
				}
			}

			if (numberStillToLoad > 0) {
				throw new StorageOverflowException("Can not load " + number + " from type " + storableParameters.getStorableType() + ".");
			}

		}

	}

	@Override
	public IStorable unload(StorableParameters storableParameters) throws StorageException {
		if (!canUnload(storableParameters, 1)) {
			throw new StorageOutOfStockException("Can not unload 1 from type " + storableParameters.getStorableType() + ".", 1.0);
		} else {
			List<ICanStore> tmp = filter(storableParameters);
			IStorable result = null;
			for (ICanStore ics : tmp) {
				result = ics.getCanStoreParameters().getLoadingPolicy().unload();
				if (result != null) {
					break;
				}
			}
			return result;
		}
	}

	@Override
	public List<IStorable> unload(StorableParameters storableParameters, int number) throws StorageException {
		if (!canUnload(storableParameters, number)) {
			throw new StorageOutOfStockException("Can not unload " + number + " from type " + storableParameters.getStorableType() + ".",
					Double.NaN);
		} else {
			List<IStorable> result = new ArrayList<>();
			for (int i = 0; i < number; i++) {
				result.add(unload(storableParameters));
			}
			return result;
		}
	}

	@Override
	public double getCurrentCapacity(StorableParameters storableParameters) {
		List<ICanStore> tmp = filter(storableParameters);
		double result = 0.0;
		for (ICanStore ics : tmp) {
			result += ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
		}

		return result;
	}

	@Override
	public double getFreeCapacity(StorableParameters storableParameters) {
		List<ICanStore> tmp = filter(storableParameters);
		double result = 0.0;
		for (ICanStore ics : tmp) {
			double free = ics.getCanStoreParameters().getMaxCapacity()
					- ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
			result += free;
		}
		return result;
	}

	@Override
	public double getFreeCapacity(CanStoreType canStoreType, StorableParameters storableParameters) {
		double result = 0.0;
		for (ICanStore ics : canStores) {
			if (ics.getCanStoreParameters().getCanStoreType().equals(canStoreType)) {
				result += (ics.getCanStoreParameters().getMaxCapacity()
						- ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters));
			}
		}
		return result;
	}

	@Override
	public double getFreeCapacity(CanStoreType canStoreType) {
		double result = 0.0;
		for (ICanStore ics : canStores) {
			if (ics.getCanStoreParameters().getCanStoreType().equals(canStoreType)) {
				result += (ics.getCanStoreParameters().getMaxCapacity()
						- ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside());
			}
		}
		return result;
	}

	@Override
	public double getCurrentCapacity(CanStoreType canStoreType, StorableParameters storableParameters) {
		double result = 0.0;
		for (ICanStore ics : canStores) {
			if (ics.getCanStoreParameters().getCanStoreType().equals(canStoreType)) {
				result += ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside(storableParameters);
			}
		}
		return result;
	}

	@Override
	public double getCurrentCapacity(CanStoreType canStoreType) {
		double result = 0.0;
		for (ICanStore ics : canStores) {
			if (ics.getCanStoreParameters().getCanStoreType().equals(canStoreType)) {
				result += ics.getCanStoreParameters().getLoadingPolicy().getCapacityInside();
			}
		}
		return result;
	}

	@Override
	public List<CanStoreType> getAllCanStoreTypes() {
		List<CanStoreType> result = new ArrayList<>();
		this.canStores.forEach(e -> result.add(e.getCanStoreParameters().getCanStoreType()));
		return result;
	}

	@Override
	public double getMaxCapacity(CanStoreType canStoreType) {
		double result = 0.0;
		for (ICanStore ics : canStores) {
			if (ics.getCanStoreParameters().getCanStoreType().equals(canStoreType)) {
				result += ics.getCanStoreParameters().getMaxCapacity();
			}
		}
		return result;
	}

}
