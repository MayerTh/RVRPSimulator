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
package vrpsim.dynamicvrprep.model.impl.degree;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public class DynamicModelDegreeCalculationHelper {
	
	private Map<BigInteger, Request> requests = new HashMap<>();
	private Map<BigInteger, Node> nodes = new HashMap<>();

	public DynamicModelDegreeCalculationHelper(DynamicVRPREPModel dynamicModel) {
		for (Node node : dynamicModel.getVRPREPInstance().getNetwork().getNodes().getNode()) {
			this.nodes.put(node.getId(), node);
		}
		for(Request request : dynamicModel.getVRPREPInstance().getRequests().getRequest()) {
			this.requests.put(request.getId(), request);
		}
	}

	public double getDistance(BigInteger rId1, BigInteger rId2) {
		return getDistance(requests.get(rId1), requests.get(rId2));
	}
	
	private double getDistance(Request r1, Request r2) {
		Node nr1 = this.nodes.get(r1.getNode());
		Node nr2 = this.nodes.get(r2.getNode());
		return calculateDistance(nr1.getCx(), nr1.getCy(), nr2.getCx(), nr2.getCy());
	}

	private double calculateDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

}
