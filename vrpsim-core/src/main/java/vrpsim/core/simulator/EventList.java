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
package vrpsim.core.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vrpsim.core.model.events.IEvent;

/**
 * @date 03.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class EventList implements Comparator<IEvent> {

	private List<IEvent> eventList;
	
	public EventList() {
		this.eventList = new ArrayList<IEvent>();
	}
	
	public List<IEvent> getEventList() {
		return this.eventList;
	}
	
	public void sort() {
		Collections.sort(this.eventList, this);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IEvent o1, IEvent o2) {
		int result = o1.getSimulationTimeOfOccurence().compareTo(o2.getSimulationTimeOfOccurence());
		if(result == 0) {
			result = o1.getPriority().compareTo(o2.getPriority());
		}
		return result;
	}
	
}
