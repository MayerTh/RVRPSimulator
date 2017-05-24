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

import java.util.HashMap;

import vrpsim.core.model.util.exceptions.VRPArithmeticException;

public class DefaultStorage {

	private final HashMap<CanStoreType, ICanStore> canStores;

	public DefaultStorage(final HashMap<CanStoreType, ICanStore> canStores) {
		this.canStores = canStores;
	}

	public DefaultStorage(ICanStore canStore) {
		this.canStores = new HashMap<CanStoreType, ICanStore>();
		this.canStores.put(canStore.getCanStoreParameters().getCanStoreType(), canStore);
	}

	public HashMap<CanStoreType, ICanStore> getCanStores() {
		return canStores;
	}
	
	public Capacity getFreeCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.canStores.get(canStoreType).getFreeCapacity();
	}
	
	public Capacity getCurrentCapacity(CanStoreType canStoreType, StorableType storableType) throws VRPArithmeticException {
		return this.canStores.get(canStoreType).getCurrentCapacity(storableType);
	}
	
	public Capacity getCurrentCapacity(CanStoreType canStoreType) throws VRPArithmeticException {
		return this.canStores.get(canStoreType).getCurrentCapacity();
	}

}
