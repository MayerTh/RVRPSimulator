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
package vrpsim.core.model.network.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.simulator.IClock;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Node extends Observable implements INode {

	private final Location location;
	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;

	private List<IWay> ways;

	public Node(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final Location location) {
		this.location = location;
		this.ways = new ArrayList<IWay>();
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
	}
	
	public void reset() {
		// Do nothing.
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		// not relevant for Node.
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		this.setChanged();
		this.notifyObservers(element);
	}

	@Override
	public Location getLocation() {
		return this.location;
	}

	@Override
	public List<IWay> getWays() {
		return this.ways;
	}

	@Override
	public void setWays(List<IWay> ways) {
		this.ways = ways;
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
	}

	@Override
	public void addWay(IWay way) {
		this.ways.add(way);
	}

	@Override
	public IWay getWayTo(INode node) {
		IWay result = null;
		for (IWay way : this.ways) {
			if (way.getTarget().getVRPSimulationModelElementParameters().getId()
					.equals(node.getVRPSimulationModelElementParameters().getId())) {
				result = way;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean isAvailableForAllocation(IClock clock) {
		return true;
	}

	@Override
	public boolean isAvailableForInteractionInAllocation(IClock clock, IVRPSimulationBehaviourElementCanAllocate element) {
		return true;
	}

}
