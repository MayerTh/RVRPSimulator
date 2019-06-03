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
package vrpsim.core.model.network;

/**
 * An {@link IWay} connects two {@link INode}s with each other.
 * 
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 */
public interface IWay extends IVRPSimulationModelNetworkElement {


	/**
	 * Target {@link INode}.
	 * 
	 * @return
	 */
	public INode getTarget();

	/**
	 * Returns the maximum speed for the {@link IWay}.
	 * 
	 * @return
	 */
	public Double getMaxSpeed();
	
	public void reset();

}
