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
package vrpsim.core.model.network;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Location {

	private final Double x;
	private final Double y;
	private final Double z;

	public Location(Double x, Double y, Double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location(Integer x, Integer y, Integer z) {
		this.x = x.doubleValue();
		this.y = y.doubleValue();
		this.z = z.doubleValue();
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Double getZ() {
		return z;
	}

	@Override
	public String toString() {
		return "x=" + this.x + ", y=" + this.y + ", z=" + this.z;
	}

}
