/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.network;

import java.util.Observable;
import java.util.Observer;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.behaviour.activities.util.TimeCalculationInformationContainer;
import vrpsim.core.model.util.functions.IDistanceFunction;
import vrpsim.core.model.util.functions.ITimeFunction;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class DefaultWay extends Observable implements IWay {

	private final INode source;
	private final INode destination;

	private final ITimeFunction travelTimeFunction;
	private final IDistanceFunction distanceFunction;
	private final Double distance;
	private final Double maxSpeed;

	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;

	public DefaultWay(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final ITimeFunction travelTimeFunction, final INode source, final INode target,
			final IDistanceFunction distanceFunction, final Double maxSpeed) {

		this.travelTimeFunction = travelTimeFunction;
		this.source = source;
		this.destination = target;
		this.maxSpeed = maxSpeed;

		this.distanceFunction = distanceFunction;
		this.distance = this.distanceFunction.getDistance(this.source.getLocation(), this.destination.getLocation());

		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
	}

	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	@Override
	public boolean isAvailable(IClock clock) {
		return true;
	}

	@Override
	public void allocateBy(IVRPSimulationBehaviourElementCanAllocate element) {
		// not relevant for StaticDefaultWay.
	}

	@Override
	public void releaseFrom(IVRPSimulationBehaviourElementCanAllocate element) {
		// not relevant for StaticDefaultWay.
		this.setChanged();
		this.notifyObservers(element);
	}

	@Override
	public ITime getServiceTime(TimeCalculationInformationContainer container, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(this.travelTimeFunction.getTime(container, clock));
	}
	
	@Override
	public ITimeFunction getServiceTimeFunction() {
		return this.travelTimeFunction;
	}

	@Override
	public INode getSource() {
		return this.source;
	}

	@Override
	public INode getTarget() {
		return this.destination;
	}

	@Override
	public Double getDistance() {
		return this.distance;
	}

	@Override
	public IDistanceFunction getDistanceFunction() {
		return this.distanceFunction;
	}

	@Override
	public Double getMaxSpeed() {
		return this.maxSpeed;
	}

	@Override
	public ITimeFunction getTravelTimeFunction() {
		return this.travelTimeFunction;
	}

	@Override
	public void addReleaseFromListener(Observer observer) {
		this.addObserver(observer);
	}

}
