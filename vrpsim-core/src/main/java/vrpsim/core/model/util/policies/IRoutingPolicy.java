package vrpsim.core.model.util.policies;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;
import vrpsim.core.model.util.exceptions.detail.NoRoutingPossibleException;
import vrpsim.core.model.util.policies.impl.Route;
import vrpsim.core.simulator.IClock;

public interface IRoutingPolicy {

	/**
	 * Returns the routing time and distance from {@link INode} source to
	 * {@link INode} target with the
	 * {@link IVRPSimulationModelStructureElementMovable} vehicle. It also returns
	 * the used ways. Only available ways shoul be returned.
	 * 
	 * @param source
	 * @param target
	 * @param vehicle
	 * @return time - simulation time (minutes)
	 */
	public Route getRouting(IVRPSimulationModelNetworkElement source, IVRPSimulationModelNetworkElement target,
			IVRPSimulationModelStructureElementMovable movable, IClock clock) throws NoRoutingPossibleException;

}
