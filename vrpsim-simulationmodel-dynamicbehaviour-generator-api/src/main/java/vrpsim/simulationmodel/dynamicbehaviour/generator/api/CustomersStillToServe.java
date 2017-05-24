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
package vrpsim.simulationmodel.dynamicbehaviour.generator.api;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.util.storage.StorableParameters;

public class CustomersStillToServe {

	final ICustomer customer;
	final StorableParameters storeableParameters;
	final Integer amount;

	public CustomersStillToServe(ICustomer customer, StorableParameters storeableParameters, Integer amount) {
		super();
		this.customer = customer;
		this.storeableParameters = storeableParameters;
		this.amount = amount;
	}

	public ICustomer getCustomer() {
		return customer;
	}

	public StorableParameters getStoreableParameters() {
		return storeableParameters;
	}

	public Integer getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return "[cust=" + customer.getVRPSimulationModelElementParameters().getId() + ", " + ((INode)customer.getVRPSimulationModelStructureElementParameters().getHome()).getLocation() + ",amount=" + amount + "]";
	}

}
