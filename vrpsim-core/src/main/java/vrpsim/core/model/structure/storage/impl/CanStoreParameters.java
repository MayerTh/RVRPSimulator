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

import vrpsim.core.model.structure.storage.ICanStore;
import vrpsim.core.model.util.policies.ILoadingPolicy;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class CanStoreParameters {

	private final CanStoreType canStoreType;
	private final int maxCapacity;
	private final ILoadingPolicy loadingPolicy;

	public CanStoreParameters(CanStoreType canStoreType, int maxCapacity, ILoadingPolicy loadingPolicy) {
		this.canStoreType = canStoreType;
		this.maxCapacity = maxCapacity;
		this.loadingPolicy = loadingPolicy;
	}
	


	public void reset() {
		this.loadingPolicy.reset();
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
	public int getMaxCapacity() {
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
