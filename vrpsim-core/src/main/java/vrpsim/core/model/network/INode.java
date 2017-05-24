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

import java.util.List;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface INode extends IVRPSimulationModelNetworkElement {

	/**
	 * Returns the {@link Location} of the {@link INode}.
	 * 
	 * @return
	 */
	public Location getLocation();

	/**
	 * {@link IWay} where the {@link INode} is {@link IWay#getSource()} is from.
	 * By calling {@link IWay#getTarget()} for all returned {@link IWay}s, all
	 * reachable destinations can be listed for this {@link INode}.
	 * 
	 * @return
	 */
	public List<IWay> getWays();

	/**
	 * Sets all {@link IWay} for this {@link INode}.
	 * 
	 * @param ways
	 */
	public void setWays(List<IWay> ways);
	
	/**
	 * Adds an {@link IWay} for this {@link INode}.
	 * 
	 * @param ways
	 */
	public void addWay(IWay way);
	
	/**
	 * Return the {@link IWay} to the given node.
	 * @param node
	 * @return
	 */
	public IWay getWayTo(INode node);

}
