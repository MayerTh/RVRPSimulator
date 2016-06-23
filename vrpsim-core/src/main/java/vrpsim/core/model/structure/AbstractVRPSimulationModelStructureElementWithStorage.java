package vrpsim.core.model.structure;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModelElementParameters;
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
	protected IVRPSimulationModelElement isAllocatedBy;

	public AbstractVRPSimulationModelStructureElementWithStorage(
			final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			final DefaultStorageManager storageManager) {

		this.storageManager = storageManager;

		// super(storage);
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
	public void allocateBy(IVRPSimulationModelElement element) {
		this.isAvailable = false;
		this.isAllocatedBy = element;
	}

	@Override
	public void releaseFrom(IVRPSimulationModelElement element) {
		this.isAvailable = true;
		this.isAllocatedBy = null;
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
	}

	@Override
	public void load(IStorable storable) throws VRPArithmeticException, StorageException {
		this.storageManager.load(storable);
	}

	@Override
	public IStorable unload(StorableType storableType) throws StorageException {
		return this.storageManager.unload(storableType);
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
