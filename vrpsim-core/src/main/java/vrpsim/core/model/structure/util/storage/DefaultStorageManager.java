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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.NoStorageForTypeException;
import vrpsim.core.model.util.exceptions.detail.StorageOutOfStockException;
import vrpsim.core.model.util.exceptions.detail.StorageOverflowException;

/**
 * @date 18.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class DefaultStorageManager {

	private static Logger logger = LoggerFactory.getLogger(DefaultStorageManager.class);

	private final DefaultStorage storage;

	public DefaultStorageManager(final DefaultStorage storage) {
		this.storage = storage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.storage.IHaveAStorage#load(vrpsim.core.model.storage.
	 * IStorable)
	 */
	public void load(IStorable storable) throws VRPArithmeticException, StorageException {

		int state = 0;
		String errorMsg = "";
		for (CanStoreType type : storable.getStorableParameters().getStorableType().getCanStoreTypes()) {
			if (this.storage.getCanStores().containsKey(type)) {
				try {
					this.storage.getCanStores().get(type).load(storable);
					state = 1;
					break;
				} catch (StorageOverflowException soe) {
					errorMsg = soe.getMessage();
					state = 2;
				}
			}
		}

		// If state is not changed, than no fitting compartment for the storable
		// could be found.
		if (state == 0) {
			throw new NoStorageForTypeException("There is no compartment in storage for type "
					+ storable.getStorableParameters().getStorableType().getCanStoreTypes() + ". Types available: "
					+ this.storage.getCanStores().keySet());
		}

		// If the state is 2 after the loop over all fitting compartment types,
		// than even the very last fitting compartment is full already and can
		// not take any more storables.
		if (state == 2) {
			throw new StorageOverflowException("There is not enough space in the compartments for type "
					+ storable.getStorableParameters().getStorableType().getCanStoreTypes() + ". I am "
					+ this.getClass().getSimpleName() + ". Original Msg: " + errorMsg);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.storage.IHaveAStorage#unload(vrpsim.core.model.storage.
	 * StoreableType)
	 */
	public IStorable unload(StorableType storableType) throws StorageException {

		List<CanStoreType> canStoreTypes = storableType.getCanStoreTypes();
		int state = 0;
		IStorable result = null;
		for (CanStoreType canStoreType : canStoreTypes) {
			if (this.storage.getCanStores().containsKey(canStoreType)) {
				try {
					result = this.storage.getCanStores().get(canStoreType).unload();
					state = 1;
					break;
				} catch (StorageOutOfStockException soos) {
					state = 2;
				}
			}
		}

		// If state is not changed, than no fitting compartment for the storable
		// could be found.
		if (state == 0) {
			throw new NoStorageForTypeException("There is no compartment in storage for any type in " + canStoreTypes
					+ ". Types available: " + this.storage.getCanStores().keySet());
		}

		// If the state is 2 after the loop over all fitting compartment types,
		// than even the very last fitting compartment is empty.
		if (state == 2) {
			throw new StorageOutOfStockException(
					"There is no storable in the following compartments anymore: " + canStoreTypes + ".", 1.0);
		}

		return result;
	}

	/**
	 * Prints the current state of the storage.
	 * 
	 * @param VRPSimModelEntityId
	 */
	public void printDebugInformationForStorage(String VRPSimModelEntityId) {
		for (CanStoreType type : this.storage.getCanStores().keySet()) {
			logger.debug(VRPSimModelEntityId + " compartment=" + type.getId() + " number=" + this.storage.getCanStores()
					.get(type).getCanStoreParameters().getLoadingPolicy().getCurrentNumberOfStorable());
		}
	}

	/**
	 * Generates and loads the given amount of {@link IStorable} in the given
	 * {@link ICanStore} defined by {@link CanStoreType}.
	 * 
	 * @param canStoreType
	 * @param amountToLoad
	 * @param storableParameters
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void loadGeneratedIn(CanStoreType canStoreType, int amountToLoad, StorableParameters storableParameters)
			throws VRPArithmeticException, StorageException {
		this.storage.getCanStores().get(canStoreType).loadGenerated(amountToLoad, storableParameters);
	}

	/**
	 * Generates and loads the given amount of {@link IStorable} in the given
	 * {@link ICanStore} defined by {@link CanStoreType}. Every instance of
	 * {@link ICanStore} holds an {@link IStorableGenerator} which implements a
	 * set of default {@link StorableParameters}. The {@link IStorable} will be
	 * generated with this parameters.
	 * 
	 * @param canStoreType
	 * @param amountToLoad
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void loadGeneratedIn(CanStoreType canStoreType, int amountToLoad)
			throws VRPArithmeticException, StorageException {
		this.storage.getCanStores().get(canStoreType).loadGenerated(amountToLoad);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.util.storage.IHaveAStorage#getAllStorableTypes()
	 */
	public List<CanStoreType> getAllCanStoreTypes() {
		return new ArrayList<CanStoreType>(this.storage.getCanStores().keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.structure.
	 * IVRPSimulationModelStructureElementWithStorage#canStore(vrpsim.core.model
	 * .structure.util.storage.StoreableType, int)
	 */
	public boolean canStore(StorableType storableType, Capacity capacityToStore) throws VRPArithmeticException {
		Capacity freeCapacity = new Capacity(capacityToStore.getUnit(), 0.0);
		for (CanStoreType type : storableType.getCanStoreTypes()) {
			freeCapacity = freeCapacity.add(this.storage.getCanStores().get(type).getFreeCapacity());
		}
		return capacityToStore.isSmallerOrEqual(freeCapacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.structure.
	 * IVRPSimulationModelStructureElementWithStorage#getAmountLeft(vrpsim.core.
	 * model.structure.util.storage.CanStoreType)
	 */
	public Capacity getCurrentCapacity(StorableType storableType) throws VRPArithmeticException {

		Capacity availableCapacity = new Capacity(Capacity.UNKNOWN_CAPACITY_UNIT, 0.0);
		for (CanStoreType type : storableType.getCanStoreTypes()) {
			availableCapacity.setUnit(this.storage.getCanStores().get(type).getCurrentCapacity(storableType).getUnit());
			availableCapacity = availableCapacity
					.add(this.storage.getCanStores().get(type).getCurrentCapacity(storableType));
		}

		return availableCapacity;
	}

	public Capacity getFreeCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.storage.getFreeCapacity(canStoreType);
	}

	public Capacity getCurrentCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.storage.getCurrentCapacity(canStoreType);
	}
}
