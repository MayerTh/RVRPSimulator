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
package vrpsim.core.model.events;

/**
 * @date 01.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IEventType {
	
	public static String ARRIVAL_EVENT = "ARRIVAL_EVENT";
	public static String CONSUMPTION_EVENT = "CONSUMPTION_EVENT";
	public static String BREAKDOWN_EVENT = "BREAKDOWN_EVENT";
	public static String ACTIVITY_EVENT = "ACTIVITY_EVENT";
	
	public static String ORDER_EVENT = "ORDER_EVENT";
	public static String TRIGGERING_ORDER_EVENT = "TRIGGERING_ORDER_EVENT";
	
	/**
	 * Return the type of the event. If an {@link IEventOwner} has different events, the
	 * owner can distinguish between the events with the help of the event type.
	 * 
	 * @return
	 */
	public String getType();
	
}
