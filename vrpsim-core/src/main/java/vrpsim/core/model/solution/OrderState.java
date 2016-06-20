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
package vrpsim.core.model.solution;

import vrpsim.core.model.structure.util.storage.IStorable;

public enum OrderState {

	/**
	 * After order creation. Provider, pickup and additional costs are not set
	 * yet.
	 */
	CREATED,

	/**
	 * Approved through provider. Provider, pickup and additional costs are set.
	 */
	APPROVED,

	/**
	 * The order is ready to get served through an OD. 
	 */
	PENDING,
	
	/**
	 * The order is served through an OD, or an normal driver
	 */
	IN_PROCESSING,
	
	/**
	 * The amount of {@link IStorable} are successfully delivered to the {@link Order#getOwner()}
	 */
	CONFIRMED,
	
	/**
	 * The order is canceled.
	 */
	CANCELED,

}
