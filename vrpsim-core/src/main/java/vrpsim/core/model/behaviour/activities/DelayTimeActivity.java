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
package vrpsim.core.model.behaviour.activities;

import vrpsim.core.model.behaviour.IActivity;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.behaviour.ITour;
import vrpsim.core.model.behaviour.TourContext;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.util.DelayTimeJob;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;

/**
 * Models a delay during a {@link ITour}.
 * 
 * @author mayert
 */
public class DelayTimeActivity implements IActivity {

	private final DelayTimeJob delayTimeJob;
	
	public DelayTimeActivity(DelayTimeJob delayTimeJob) {
		this.delayTimeJob = delayTimeJob;
	}
	
	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws VRPArithmeticException, NetworkException {
		ActivityPrepareActionResult actionResult = new ActivityPrepareActionResult(true);
		actionResult.setTimeTillDoAction(this.delayTimeJob.getDelay());
		return actionResult;
	}

	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context)
			throws StorageException, VRPArithmeticException {
		ActivityDoActionResult actionResult = new ActivityDoActionResult(0.0);
		return actionResult;
	}

	@Override
	public IJob getJob() {
		return this.delayTimeJob;
	}

}
