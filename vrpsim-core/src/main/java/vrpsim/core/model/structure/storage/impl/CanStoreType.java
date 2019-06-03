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

import java.util.UUID;

import vrpsim.core.model.structure.storage.ICanStore;
import vrpsim.core.model.structure.storage.IStorable;

/**
 * Defines the type of a {@link ICanStore}. Is referenced by
 * {@link StorableType} in {@link StorableParameters} for an {@link IStorable},
 * to define the possible containers a {@link IStorable} can be stored in.
 * 
 * @author mayert
 */
public class CanStoreType {

	private final String id;
	private final Long lId;

	public CanStoreType(String id) {
		super();
		this.id = id;
		this.lId = UUID.randomUUID().getMostSignificantBits();
	}

	public String getId() {
		return id;
	}

	public Long getlId() {
		return lId;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof CanStoreType) {
			equal = ((CanStoreType) obj).getId().equals(this.id);
		}
		return equal;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return this.id;
	}

}
