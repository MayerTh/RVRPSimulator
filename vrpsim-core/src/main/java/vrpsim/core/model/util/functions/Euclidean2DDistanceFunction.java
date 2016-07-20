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
package vrpsim.core.model.util.functions;

import vrpsim.core.model.network.Location;

/**
 * @date 24.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Euclidean2DDistanceFunction implements IDistanceFunction {

	@Override
	public Double getDistance(Location location1, Location location2) {
		double a = Math.abs(location1.getX() - location2.getX());
		double b = Math.abs(location1.getY() - location2.getY());
		return Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0));
	}

}
