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
package vrpsim.core.model.behaviour.activities;

import java.util.List;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.IVRPSimulationBehaviourElementCanAllocate;
import vrpsim.core.model.behaviour.activities.impl.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.detail.NoRoutingPossibleException;
import vrpsim.core.simulator.IClock;

/**
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IActivity extends IVRPSimulationBehaviourElementCanAllocate {

	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context) throws BehaviourException, NoRoutingPossibleException ;
	
	public void doAction(IClock clock, TourContext context) throws StorageException;
	
	public String toString();
	
	public IActivity getSuccessor();
	
	public void setSuccessor(IActivity successor);
	
	public void validate(TourContext context) throws BehaviourException;
	
	public List<IVRPSimulationModelElement> getToAllocate();
	
	/**
	 * Returns true, if an {@link IActivity} is a customer interaction.
	 * 
	 * @return
	 */
	public boolean isCustomerInteraction();
	
	public IVRPSimulationModelNetworkElement getLocation();
	
	public boolean isPrepared();
	
	public void setPrepared(boolean isPrepared);
	
	public void reset();
	
}
