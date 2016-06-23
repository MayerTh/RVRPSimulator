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
package vrpsim.core.model.structure.depot;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.IStorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.uncertainty.UncertainParamters;

/**
 * A {@link SourceDepot} runs never out of stock. If
 * {@link SourceDepot#unload(vrpsim.core.model.structure.util.storage.StorableType)}
 * is called a new {@link IStorable} is generated with
 * {@link IStorableGenerator} and returned.
 * 
 * @author mayert
 */
public class SourceDepot extends DefaultDepot {

	public SourceDepot(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
			UncertainParamters arrivalParameters, final DefaultStorageManager storageManager) {
		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, arrivalParameters,
				storageManager);
	}

	@Override
	public IStorable unload(StorableType storableType) throws StorageException {

		try {
			this.storageManager.loadGeneratedIn(storableType.getCanStoreTypes().get(0), 1);
		} catch (VRPArithmeticException e) {
			throw new StorageException(
					"Can not load a created storable. Original exception: VRPArithmeticException with message: "
							+ e.getMessage());
		}

		IStorable storable = super.unload(storableType);
		return storable;
	}

	@Override
	public Capacity getCurrentCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return new Capacity(Capacity.UNKNOWN_CAPACITY_UNIT, Double.MAX_VALUE);
	}

	@Override
	public Capacity getCurrentCapacity(StorableType storableType) throws VRPArithmeticException {
		return new Capacity(Capacity.UNKNOWN_CAPACITY_UNIT, Double.MAX_VALUE);
	}
}
