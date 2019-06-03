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
package vrpsim.core.model.behaviour.activities.impl;

import vrpsim.core.model.IVRPSimulationModelElement;

/**
 * @date 22.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class ActivityPrepareActionResult {

	private final boolean prepareActionSuccessful;
	private final String msg;

	private IVRPSimulationModelElement responsibleElement;
	private Double reletaiveTimeTillDoAction;

	public ActivityPrepareActionResult(boolean prepareActionSuccessful, String msg) {
		this.prepareActionSuccessful = prepareActionSuccessful;
		this.msg = msg;
	}

	public void setResponsibleElement(IVRPSimulationModelElement responsibleElement) {
		this.responsibleElement = responsibleElement;
	}

	public boolean isPrepareActionSuccessful() {
		return prepareActionSuccessful;
	}

	public IVRPSimulationModelElement getResponsibleElement() {
		return responsibleElement;
	}

	public String getMsg() {
		return this.msg;
	}

	public Double getReletaiveTimeTillDoAction() {
		return reletaiveTimeTillDoAction;
	}

	public void setReletaiveTimeTillDoAction(Double reletaiveTimeTillDoAction) {
		this.reletaiveTimeTillDoAction = reletaiveTimeTillDoAction;
	}

}
