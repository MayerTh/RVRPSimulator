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
package vrpsim.core.model.behaviour.activities.util;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.simulator.ITime;

/**
 * @date 22.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class ActivityPrepareActionResult {

	private final boolean prepareActionSuccessful;
	
	private IVRPSimulationModelStructureElement responsibleElement;
	private ITime timeTillDoAction;

	public ActivityPrepareActionResult(boolean prepareActionSuccessful) {
		this.prepareActionSuccessful = prepareActionSuccessful;
	}

	public void setResponsibleElement(IVRPSimulationModelStructureElement responsibleElement) {
		this.responsibleElement = responsibleElement;
	}

	public void setTimeTillDoAction(ITime timeTillDoAction) {
		this.timeTillDoAction = timeTillDoAction;
	}

	public boolean isPrepareActionSuccessful() {
		return prepareActionSuccessful;
	}

	public IVRPSimulationModelStructureElement getResponsibleElement() {
		return responsibleElement;
	}

	public ITime getTimeTillDoAction() {
		return timeTillDoAction;
	}

}
