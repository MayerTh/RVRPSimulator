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
package vrpsim.vrprep.util.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.vrprep.model.instance.Instance;

public interface IVRPREPInstanceProviderAPI {

	/**
	 * Returns all kind of available instance. A possible result is for example
	 * "dynamicvrp", or "staticvrp".
	 * 
	 * @return
	 */
	public List<String> getAvailableInstanceKinds() throws IOException;

	/**
	 * Returns all instance providers for the given instance kind. A possible
	 * result is for example "uchoa", or "gambella".
	 * 
	 * @param kind,
	 *            see
	 *            {@code IVRPREPInstanceProvider#getAvailableInstanceKinds()}
	 * @return
	 */
	public List<String> getAvailableInstanceProvidersForKind(String kind) throws IOException;

	/**
	 * Returns the concrete instances for the given kind and provider. A
	 * possible result is for example "CMT01.xml", or "CMT02.xml" .
	 * 
	 * @param kind,
	 *            see
	 *            {@code IVRPREPInstanceProvider#getAvailableInstanceKinds()}
	 * @param provider,
	 *            see
	 *            {@code IVRPREPInstanceProvider#getAvailableInstanceProvidersForKind(String)}
	 * @return
	 */
	public List<String> getAvailableInstancesForKindAndProvider(String kind, String provider) throws IOException;

	/**
	 * Loads and returns the {@link Instance} by given kind, provider and
	 * instance.
	 * 
	 * @param kind
	 * @param provider
	 * @param instance
	 * @param correctInstance - removes duplicates in network (found in instance CMT04 from chris79)
	 * @param setMaximumVehicleanMinimumCustomerCapcity - sets vehicle capa to maximum and requested amount to 1.
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Instance getAvailableInstance(String kind, String provider, String instance, boolean correctInstance, boolean setMaximumVehicleanMinimumCustomerCapcity)
			throws JAXBException, IOException, URISyntaxException;

}
