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

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.util.DelayTimeJob;
import vrpsim.core.model.behaviour.activities.util.IJob;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ActivityValidationException;
import vrpsim.core.simulator.IClock;

/**
 * Models a delay during a {@link ITour}.
 * 
 * @author mayert
 */
public class DelayTimeActivity implements IActivity {

	private final DelayTimeJob delayTimeJob;
	private boolean isActive = false;
	private IActivity successor;
	
	public DelayTimeActivity(DelayTimeJob delayTimeJob) {
		this.delayTimeJob = delayTimeJob;
	}
	
	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws VRPArithmeticException, NetworkException, BehaviourException {
		this.validate(context);
		this.isActive = true;
		context.getCurrentPlace().allocateBy(this);
		ActivityPrepareActionResult actionResult = new ActivityPrepareActionResult(true, "Delay job.");
		actionResult.setTimeTillDoAction(this.delayTimeJob.getDelay());
		return actionResult;
	}

	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context)
			throws StorageException, VRPArithmeticException {
		context.getCurrentPlace().releaseFrom(this);
		ActivityDoActionResult actionResult = new ActivityDoActionResult(0.0);
		return actionResult;
	}

	@Override
	public IJob getJob() {
		return this.delayTimeJob;
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
		if (this.isActive) {
			throw new BehaviourException(
					"During delay the state of the allocated INode changed. Can not handle this state change yet.");
		}
		
	}

	@Override
	public IActivity getSuccessor() {
		return this.successor;
	}

	@Override
	public void setSuccessor(IActivity successor) {
		this.successor = successor;
	}

	@Override
	public void validate(TourContext context) throws BehaviourException {
		if(this.delayTimeJob.getDelay().getDoubleValue() < 0) {
			throw new ActivityValidationException("DelayTimeActivity not valid, negativ delay is set.");
		}
	}

}
