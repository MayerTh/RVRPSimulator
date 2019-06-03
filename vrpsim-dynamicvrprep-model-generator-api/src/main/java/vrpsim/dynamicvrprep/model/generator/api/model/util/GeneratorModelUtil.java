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
package vrpsim.dynamicvrprep.model.generator.api.model.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

public class GeneratorModelUtil {

	private Map<BigInteger, Node> nodes = new HashMap<BigInteger, Instance.Network.Nodes.Node>();

	public GeneratorModelUtil(Instance instance) {
		for (Node node : instance.getNetwork().getNodes().getNode()) {
			this.nodes.put(node.getId(), node);
		}
	}

	public double getDistance(Request r1, Request r2) {
		Node nr1 = this.nodes.get(r1.getNode());
		Node nr2 = this.nodes.get(r2.getNode());
		return calculateDistance(nr1.getCx(), nr1.getCy(), nr2.getCx(), nr2.getCy());
	}
	
	public double getDistance(double rx1, double ry1, Request r2) {
		Node nr2 = this.nodes.get(r2.getNode());
		return calculateDistance(rx1, ry1, nr2.getCx(), nr2.getCy());
	}

	private double calculateDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public Node getNode(Request r) {
		return this.nodes.get(r.getNode());
	}

}
