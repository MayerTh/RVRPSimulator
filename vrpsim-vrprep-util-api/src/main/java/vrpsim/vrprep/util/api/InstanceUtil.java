package vrpsim.vrprep.util.api;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

public class InstanceUtil {

	private Map<BigInteger, Request> requests = new HashMap<>();
	private Map<BigInteger, Node> nodes = new HashMap<>();

	public InstanceUtil(Instance instance) {
		for (Node node : instance.getNetwork().getNodes().getNode()) {
			this.nodes.put(node.getId(), node);
		}
		for(Request request : instance.getRequests().getRequest()) {
			this.requests.put(request.getId(), request);
		}
	}
	
	public Set<BigInteger> getRequestIds() {
		return this.requests.keySet();
	}
	
	public Node getNode(Request request) {
		return this.nodes.get(request.getNode());
	}
	
	public Node getNode(BigInteger nodeId) {
		return this.nodes.get(nodeId);
	}
	
	public Request getRequest(BigInteger requestId) {
		return this.requests.get(requestId);
	}

}
