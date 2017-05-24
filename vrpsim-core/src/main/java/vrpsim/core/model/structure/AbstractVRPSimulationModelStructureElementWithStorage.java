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
package vrpsim.core.model.structure;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;

public abstract class AbstractVRPSimulationModelStructureElementWithStorage extends Observable
		implements IVRPSimulationModelStructureElementWithStorage {

	protected final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;
	protected final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters;
	protected final DefaultStorageManager storageManager;

	protected IVRPSimulationModelNetworkElement currentPlace;
	protected boolean isAvailable = true;
	protected IVRPSimulationBehaviourElementCanAllocate isAllocatedBy;

	public AbstractVRPSimulationModelStructureElementWithStorage(
			final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final DefaultStorageManager storageManager) {

		this.storageManager = storageManager;
		this.currentPlace = vrpSimulationModelStructureElementParameters.getHome();
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.vrpSimulationModelStructureElementParameters = vrpSimulationModelStructureElementParameters;
	}

	@Override
	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return this.currentPlace;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public VRPSimulationModelStructureElementParameters getVRPSimulationModelStructureElementParameters() {
		return this.vrpSimulationModelStructureElementParameters;
	}

	@Override
	public boolean isAvailable(IClock clock) {
		return this.isAvailable;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
//		this.isAvailable = false;
//		this.isAllocatedBy = element;
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
//		this.isAvailable = true;
//		this.isAllocatedBy = null;
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
	}

	@Override
	public void load(IStorable storable) throws VRPArithmeticException, StorageException {
		this.storageManager.load(storable, this.vrpSimulationModelElementParameters.getId());
	}

	@Override
	public IStorable unload(StorableType storableType) throws StorageException {
		return this.storageManager.unload(storableType, this.vrpSimulationModelElementParameters.getId());
	}

	@Override
	public boolean canStore(StorableType storableType, Capacity capacityToStore) throws VRPArithmeticException {
		return this.storageManager.canStore(storableType, capacityToStore);
	}

	@Override
	public Capacity getCurrentCapacity(StorableType storableType) throws VRPArithmeticException {
		return this.storageManager.getCurrentCapacity(storableType);
	}

	@Override
	public Capacity getFreeCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.storageManager.getFreeCapacity(canStoreType);
	}

	@Override
	public Capacity getCurrentCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.storageManager.getCurrentCapacity(canStoreType);
	}

	@Override
	public List<CanStoreType> getAllCanStoreTypes() {
		return this.storageManager.getAllCanStoreTypes();
	}

}
