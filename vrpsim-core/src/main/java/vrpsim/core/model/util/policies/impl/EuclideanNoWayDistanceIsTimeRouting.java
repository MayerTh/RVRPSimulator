package vrpsim.core.model.util.policies.impl;

import java.util.ArrayList;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.Way;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;
import vrpsim.core.model.util.policies.IRoutingPolicy;
import vrpsim.core.simulator.IClock;

/**
 * Calculates the {@link EuclideanDistance} between the two {@link INode} by not
 * considering {@link Way}'a and returns the distance as time, so also speed of
 * the vehicles is not considered.
 * 
 * @author mayert
 *
 */
public class EuclideanNoWayDistanceIsTimeRouting implements IRoutingPolicy {

	@Override
	public Route getRouting(IVRPSimulationModelNetworkElement source, IVRPSimulationModelNetworkElement target,
			IVRPSimulationModelStructureElementMovable movable, IClock clock) {

		Location sourceLocation = getLocation(source);
		Location targetLocation = getLocation(target);

		DistanceMeasure dm = new EuclideanDistance();
		double distance = dm.compute(sourceLocation.toArray(), targetLocation.toArray());
		return new Route(distance, distance, new ArrayList<>());
	}
	
	private Location getLocation(IVRPSimulationModelNetworkElement element) {
		Location elementLocation = null;
		if (element instanceof INode) {
			elementLocation = ((INode) element).getLocation();
		}

		if (element instanceof IWay) {
			elementLocation = ((IWay) element).getTarget().getLocation();
		}
		return elementLocation;
	}
}
