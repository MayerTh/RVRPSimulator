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
package vrpsim.core.model.structure.occasionaldriver.impl;

public class OccasionalDriver { //extends AbstractOccasionalDriver {

//	private ITour myDailyBehaviour;
//
//	private final double averageSpeed;
//	private IVRPSimulationModelNetworkElement currentPlace;
//	private final IRoutingStrategy routingStrategy;
//
//	public OccasionalDriver(final VRPSimulationModelElementParameters vrpSimulationModelElementParameters,
//			final VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters,
//			final DefaultStorageManager storageManager, final ITour myDailyBehaviour, final double averageSpeed,
//			final IRoutingStrategy routingStrategy) {
//
//		super(vrpSimulationModelElementParameters, vrpSimulationModelStructureElementParameters, storageManager);
//
//		this.myDailyBehaviour = myDailyBehaviour;
//		this.averageSpeed = averageSpeed;
//		this.routingStrategy = routingStrategy;
//		
//		this.currentPlace = vrpSimulationModelStructureElementParameters.getHome();
//	}
//
//	@Override
//	public List<IEventType> getAllEventTypes() {
//		return this.myDailyBehaviour.getAllEventTypes();
//	}
//
//	@Override
//	public List<IEvent> getInitialEvents(IClock clock) {
//		return this.myDailyBehaviour.getInitialEvents(clock);
//	}
//
//	@Override
//	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException {
//		return this.myDailyBehaviour.processEvent(event, clock, eventListAnalyzer);
//	}
//
//	@Override
//	public Double getAverageSpeed() {
//		return this.averageSpeed;
//	}
//
//	@Override
//	public void update(Observable o, Object arg) {
//		this.logger.info("Got informed about change of OrderBord.");
//
//		// TODO Simulation time?
//
//		PublicOrderPlatform orderBord = (PublicOrderPlatform) o;
//		if (orderBord.confirmOrder((Order) arg, this)) {
//			// TODO
//		}
//
//	}
//
//	@Override
//	public void setCurrentPlace(IVRPSimulationModelNetworkElement networkElement) {
//		this.currentPlace = networkElement;
//
//	}
//
//	@Override
//	public IVRPSimulationModelNetworkElement getCurrentPlace() {
//		return this.currentPlace;
//	}
//
//	@Override
//	public IRoutingStrategy getRoutingStrategy() {
//		return this.routingStrategy;
//	}
//	
//	@Override
//	public ITime getServiceTime(TimeCalculationInformationContainer container, IClock clock) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	@Override
//	public ITimeFunction getServiceTimeFunction() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
