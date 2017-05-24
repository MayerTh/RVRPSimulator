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
package vrpsim.r.util.api.model;

public class RPoint {

	private double x;
	private double y;
	private boolean highlight;

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public RPoint(double x, double y, boolean highlight) {
		super();
		this.x = x;
		this.y = y;
		this.highlight = highlight;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RPoint) {
			return Double.compare(x, ((RPoint) obj).getX()) == 0 && Double.compare(y, ((RPoint) obj).getY()) == 0;
		}
		return super.equals(obj);
	}

}
