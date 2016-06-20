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
package vrpsim.core.model.behaviour;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationElement;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Behaviour {

	private final List<ITour> tours;
	
	private BehaviourService behaviourService;

	public Behaviour(final List<ITour> tours) {
		this.tours = tours;
	}

	public List<ITour> getTours() {
		return this.tours;
	}
	
	/**
	 * Returns all existing {@link IVRPSimulationElement}.
	 * 
	 * @return
	 */
	public List<IVRPSimulationElement> getAllSimulationElements() {
		List<IVRPSimulationElement> allElements = new ArrayList<>();
		allElements.addAll(this.getTours());
		return allElements;
	}
	
	/**
	 * Returns the {@link BehaviourService} which should be used to get
	 * information and services for the {@link Behaviour}.
	 * 
	 * @return
	 */
	public BehaviourService getBehaviourService() {
		if(this.behaviourService == null) {
			this.behaviourService = new BehaviourService(this);
		}
		return this.behaviourService;
	}

}
