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

import vrpsim.core.model.util.policies.ILoadingPolicy;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class CanStoreParameters {

	private final CanStoreType canStoreType;
	private final Capacity maxCapacity;
	private final ILoadingPolicy loadingPolicy;
	private final IStorableGenerator storableGenerator;

	public CanStoreParameters(CanStoreType canStoreType, Capacity capacity, ILoadingPolicy loadingPolicy,
			IStorableGenerator storableGenerator) {
		super();
		this.canStoreType = canStoreType;
		this.maxCapacity = capacity;
		this.loadingPolicy = loadingPolicy;
		this.storableGenerator = storableGenerator;
	}

	/**
	 * Returns the {@link IStorableGenerator} for the {@link ICanStore} which is
	 * owner of the {@link CanStoreParameters}.
	 * 
	 * @return
	 */
	public IStorableGenerator getStorableGenerator() {
		return storableGenerator;
	}

	/**
	 * Return the type of the {@link ICanStore} which is owner of the
	 * {@link CanStoreParameters}.
	 * 
	 * @return
	 */
	public CanStoreType getCanStoreType() {
		return canStoreType;
	}

	/**
	 * Returns the maximum capacity of the {@link ICanStore} which is owner of
	 * the {@link CanStoreParameters}.
	 * 
	 * @return
	 */
	public Capacity getMaxCapacity() {
		return maxCapacity;
	}

	/**
	 * Returns the loading policy {@link ICanStore} which is owner of the
	 * {@link CanStoreParameters}.
	 * 
	 * @return
	 */
	public ILoadingPolicy getLoadingPolicy() {
		return loadingPolicy;
	}

}
