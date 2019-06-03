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

import java.util.Observable;
import java.util.Observer;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.simulator.IClock;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Way extends Observable implements IWay {

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;

	private final INode target;
	private final Double maxSpeed;

	public Way(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters, final INode target, final Double maxSpeed) {
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
		this.target = target;
		this.maxSpeed = maxSpeed;
	}
	
	public void reset() {
		// Do nothing.
	}

	public Way(String id, Integer priority, final INode target, final Double maxSpeed) {
		this.vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(id, priority);
		this.target = target;
		this.maxSpeed = maxSpeed;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		// not relevant for Way.
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		// not relevant for Way.
		this.setChanged();
		this.notifyObservers(element);
	}

	@Override
	public INode getTarget() {
		return this.target;
	}

	@Override
	public Double getMaxSpeed() {
		return this.maxSpeed;
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
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
