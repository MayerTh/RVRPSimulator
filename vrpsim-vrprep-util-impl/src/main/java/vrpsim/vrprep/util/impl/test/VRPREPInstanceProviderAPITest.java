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
package vrpsim.vrprep.util.impl.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.vrprep.model.instance.Instance;

import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI;

public class VRPREPInstanceProviderAPITest {

	public static void main(String[] args) throws IOException, JAXBException, URISyntaxException {

		IVRPREPInstanceProviderAPI api = new VRPREPInstanceProviderAPI();
		List<String> kinds = api.getAvailableInstanceKinds();
		System.out.println("Instances kinds: " + kinds);

		List<String> providers = api.getAvailableInstanceProvidersForKind(kinds.get(0));
		System.out.println("Instances providers for kind " + kinds.get(0) + ": " + providers);

		List<String> instances = api.getAvailableInstancesForKindAndProvider(kinds.get(0), providers.get(0));
		System.out
				.println("Instances for kind " + kinds.get(0) + " for provider " + providers.get(0) + ": " + instances);
		
		Instance instance = api.getAvailableInstance(kinds.get(0), providers.get(0), instances.get(0), true, false);
		System.out.println("load instance: " + instances.get(0) + " - " + instance.toString());

	}

}
