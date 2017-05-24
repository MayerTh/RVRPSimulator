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
package vrpsim.vrprep.util.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Fleet.VehicleProfile;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;
import org.vrprep.model.util.Instances;

import vrpsim.vrprep.util.api.IVRPREPInstanceProviderAPI;
import vrpsim.vrprep.util.impl.util.VRPREPInstanceProviderUtil;

public class VRPREPInstanceProviderAPI implements IVRPREPInstanceProviderAPI {

	private static Logger logger = LoggerFactory.getLogger(VRPREPInstanceProviderAPI.class);
	
	private final String INSTANCE_ROOT = "vrprepformat";
	private final VRPREPInstanceProviderUtil util = new VRPREPInstanceProviderUtil();

	@Override
	public List<String> getAvailableInstanceKinds() throws IOException {
		Path path = Paths.get(INSTANCE_ROOT);
		List<Path> paths = util.getAvailablePathsToInstances(path);
		return convert(paths);
	}

	@Override
	public List<String> getAvailableInstanceProvidersForKind(String kind) throws IOException {
		Path path = Paths.get(INSTANCE_ROOT, kind);
		List<Path> paths = util.getAvailablePathsToInstances(path);
		return convert(paths);
	}

	@Override
	public List<String> getAvailableInstancesForKindAndProvider(String kind, String provider) throws IOException {
		Path path = Paths.get(INSTANCE_ROOT, kind, provider);
		List<Path> paths = util.getAvailablePathsToInstances(path);
		return convert(paths);
	}

	@Override
	public Instance getAvailableInstance(String kind, String provider, String instance, boolean correctInstances, boolean setMaximumVehicleAndMinimumCustomerCapcity)
			throws JAXBException, IOException, URISyntaxException {
		Path path = Paths.get(INSTANCE_ROOT, kind, provider, instance);
		File fileInstance = util.loadInstance(path);
		Instance inst = Instances.read(Paths.get(fileInstance.getAbsolutePath()));
		if (setMaximumVehicleAndMinimumCustomerCapcity) {
			this.adaptInstanceTosetMaximumVehicleAndMinimumCustomerCapcity(inst);
		}
		if (correctInstances) {
			this.correctInstance(inst);
		}
		return inst;
	}

	private void adaptInstanceTosetMaximumVehicleAndMinimumCustomerCapcity(Instance i) {
		for (VehicleProfile vp : i.getFleet().getVehicleProfile()) {
			vp.setCapacity(Double.MAX_VALUE);
		}
		for (Request r : i.getRequests().getRequest()) {
			r.setQuantity(1.0);
		}
		String newDataset = i.getInfo().getDataset()
				+ " (vrpsim.vrprep.util.impl.VRPREPInstanceProviderAPI :: capacity vehicle set to Double.MAX_VALUE :: request quantity set to 1.0)";
		i.getInfo().setDataset(newDataset);
	}

	private List<String> convert(List<Path> paths) {
		List<String> result = new ArrayList<>();
		for (Path path : paths) {
			String str = path.getName(path.getNameCount() - 1).toString();
			if (!str.endsWith(".txt")) {
				result.add(path.getName(path.getNameCount() - 1).toString());
			}
		}
		return result;
	}

	private void correctInstance(Instance inst) {
		logger.info("Correcting instance is active.");
		List<Node> nodesToDelete = new ArrayList<>();
		List<Request> requestsToDelete = new ArrayList<>();
		Set<Point> testSet = new HashSet<>();
		for (Node node : inst.getNetwork().getNodes().getNode()) {
			Point testPoint = new Point(node.getCx(), node.getCy());
			if (testDuplicates(testSet, testPoint)) {
				logger.warn("Network node with existing location {} found: {}, will be marked for deletion", testPoint, node);
				nodesToDelete.add(node);
				for(Request request : inst.getRequests().getRequest()) {
					if(request.getNode().equals(node.getId())) {
						logger.warn("Request pointing to marked network node found: : {}, will be marked for deletion", request);
						requestsToDelete.add(request);
					}
				}
			} else {
				testSet.add(testPoint);
			}
		}

		for (Node node : nodesToDelete) {
			logger.warn("Delet: {}", node);
			inst.getNetwork().getNodes().getNode().remove(node);
		}
		
		for(Request request : requestsToDelete) {
			logger.warn("Delet: {}", request);
			inst.getRequests().getRequest().remove(request);
		}

	}

	private boolean testDuplicates(Set<Point> testSet, Point testPoint) {
		
		for(Point p : testSet) {
			if(p.equals(testPoint)) {
				return true;
			}
		}
		
		return false;
	}

	private class Point {
		public final double x;
		public final double y;

		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Point) {
				return ((Double.compare(((Point) obj).x, x) == 0) && (Double.compare(((Point) obj).y, y) == 0));
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
	}
}
