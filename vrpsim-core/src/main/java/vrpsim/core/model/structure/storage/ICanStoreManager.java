package vrpsim.core.model.structure.storage;

import java.util.List;

import vrpsim.core.model.structure.storage.impl.CanStoreType;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.structure.storage.impl.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;

public interface ICanStoreManager {

	/**
	 * Returns true if the storage still has place for the given capacity.
	 * 
	 * @param storableType
	 * @param number
	 * @return
	 * @throws VRPArithmeticException
	 */
	public boolean canLoad(StorableParameters storableParameters, int number);

	/**
	 * Returns true if the storage provides the given capacity.
	 * 
	 * @param storableType
	 * @param numberToStoreFromStorableType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public boolean canUnload(StorableParameters storableParameters, int number);

	/**
	 * Loads one {@link IStorable} into the storage.
	 * 
	 * @param storable
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(IStorable storable) throws StorageException;

	/**
	 * Loads the given {@link IStorable} into the storage.
	 * 
	 * @param storable
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(List<IStorable> storables) throws StorageException;

	/**
	 * Loads the given number into the storage.
	 * 
	 * @param storable
	 * @throws VRPArithmeticException
	 * @throws StorageException
	 */
	public void load(StorableParameters storableParameters, int number) throws StorageException;

	/**
	 * Remove and returns one {@link IStorable} with given
	 * {@link StorableParameters} from the storage.
	 * 
	 * @param storableType
	 * @return
	 * @throws StorageException
	 */
	public IStorable unload(StorableParameters storableParameters) throws StorageException;

	/**
	 * Remove and returns one {@link IStorable} with given
	 * {@link StorableParameters} from the storage.
	 * 
	 * @param storableType
	 * @return
	 * @throws StorageException
	 */
	public List<IStorable> unload(StorableParameters storableParameters, int number) throws StorageException;

	/**
	 * Returns the current available capacity of the given
	 * {@link StorableParameters}.
	 * 
	 * @param storableType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public double getCurrentCapacity(StorableParameters storableParameters);

	/**
	 * Returns the capacity left for the given {@link StorableParameters}.
	 * 
	 * @param storableParameters
	 * @return
	 */
	public double getFreeCapacity(StorableParameters storableParameters);

	/**
	 * Returns the unused capacity of the {@link ICanStore} defined by given
	 * {@link CanStoreType}.
	 * 
	 * @param canStoreType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public double getFreeCapacity(CanStoreType canStoreType, StorableParameters storableParameters);
	
	/**
	 * Returns the unused capacity of the {@link ICanStore} defined by given
	 * {@link CanStoreType} independent from {@link StorableParameters}.
	 * 
	 * @param canStoreType
	 * @return
	 */
	public double getFreeCapacity(CanStoreType canStoreType);
	
	/**
	 * Returns the unused capacity of the {@link ICanStore} defined by given
	 * {@link CanStoreType}.
	 * 
	 * @param canStoreType
	 * @return
	 * @throws VRPArithmeticException
	 */
	public double getMaxCapacity(CanStoreType canStoreType);

	/**
	 * Returns the current capacity of {@link IStorable} in {@link ICanStore} defined
	 * by given {@link CanStoreType} and the {@link StorableParameters}.
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
	public double getCurrentCapacity(CanStoreType canStoreType, StorableParameters storableParameters);
	
	/**
	 * Returns the current capacity of {@link IStorable} in {@link ICanStore} defined
	 * by given {@link CanStoreType} independent from {@link StorableParameters}.
	 * 
	 * @param canStoreType
	 * @return
	 */
	public double getCurrentCapacity(CanStoreType canStoreType);

	/**
	 * Returns a list of all {@link CanStoreType}.
	 * 
	 * @return
	 */
	public List<CanStoreType> getAllCanStoreTypes();

	public void reset();
	
}
