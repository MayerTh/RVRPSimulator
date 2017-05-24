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

import java.util.List;

public class RModel {

	private final RPoint start;
	private final List<RPoint> ListOfPoints;
	private final RPoint end;
	private final RPoint max;

	private final boolean pointsAreConnected;

	public RModel(RPoint start, List<RPoint> ListOfPoints, RPoint end, RPoint max, boolean pointsAreConnected) {
		super();
		this.start = start;
		this.ListOfPoints = ListOfPoints;
		this.end = end;
		this.max = max;
		this.pointsAreConnected = pointsAreConnected;
	}

	public RPoint getMax() {
		return max;
	}

	public RPoint getStart() {
		return start;
	}

	public List<RPoint> getListOfPoints() {
		return ListOfPoints;
	}

	public RPoint getEnd() {
		return end;
	}

	public boolean isPointsAreConnected() {
		return pointsAreConnected;
	}

}
