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

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class StaticDefaultNode implements INode {

	private final Location location;
	private final VRPSimulationModelElementParameters vrpSimulationModelElementParameters;

	private List<IWay> ways;

	public StaticDefaultNode(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
			final Location location) {
		this.location = location;
		this.ways = new ArrayList<IWay>();
		this.vrpSimulationModelElementParameters = vrpSimulationModelElementParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.IVRPSimulationModelElement#
	 * getVRPSimulationModelElementParameters()
	 */
	@Override
	public VRPSimulationModelElementParameters getVRPSimulationModelElementParameters() {
		return this.vrpSimulationModelElementParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.IVRPSimulationModelElement#isNotAvailable(vrpsim.core.
	 * simulator.IClock)
	 */
	@Override
	public boolean isAvailable(IClock clock) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.IVRPSimulationModelElement#allocateBy(vrpsim.core.model
	 * .structure.IVRPSimulationModelStructureElement)
	 */
	@Override
	public void allocateBy(IVRPSimulationModelElement element) {
		// not relevant for StativDefaultNode.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.IVRPSimulationModelElement#freeFrom(vrpsim.core.model.
	 * structure.IVRPSimulationModelStructureElement)
	 */
	@Override
	public void freeFrom(IVRPSimulationModelElement element) {
		// not relevant for StativDefaultNode.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.IVRPSimulationModelElement#getServiceTime(vrpsim.core.
	 * model.behaviour.activities.StorableExchangeJob,
	 * vrpsim.core.simulator.IClock)
	 */
	@Override
	public ITime getServiceTime(IJob job, IClock clock) {
		return clock.getCurrentSimulationTime().createTimeFrom(0.0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.network.INode#getLocation()
	 */
	@Override
	public Location getLocation() {
		return this.location;
	}

	/* (non-Javadoc)
	 * @see vrpsim.core.model.network.INode#getWays()
	 */
	@Override
	public List<IWay> getWays() {
		return this.ways;
	}



	/* (non-Javadoc)
	 * @see vrpsim.core.model.network.INode#setWays(java.util.List)
	 */
	@Override
	public void setWays(List<IWay> ways) {
		this.ways = ways;
	}

}
