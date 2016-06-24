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
package vrpsim.visualization.support;

import java.util.Vector;

public class MathUtil {

	/**
	 * Calculates the arrow vector. The result can be read like 0=x1 1=y1 2=x2
	 * 3=y2.
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param angle
	 * @return
	 */
	public static Vector<Double> calculateArrowVector(double startX, double startY, double endX, double endY,
			double angle) {

		double arrowHeadLengh = 4;

		double dx = endX - startX;
		double dy = endY - startY;

		double length = Math.sqrt(dx * dx + dy * dy);
		double unitDx = dx / length;
		double unitDy = dy / length;

		double point1X = endX - unitDx * arrowHeadLengh - unitDy * arrowHeadLengh;
		double point1Y = endY - unitDy * arrowHeadLengh + unitDx * arrowHeadLengh;

		double point2X = endX - unitDx * arrowHeadLengh + unitDy * arrowHeadLengh;
		double point2Y = endY - unitDy * arrowHeadLengh - unitDx * arrowHeadLengh;

		Vector<Double> result = new Vector<>();
		result.addElement(point1X);
		result.addElement(point1Y);
		result.addElement(point2X);
		result.addElement(point2Y);
		return result;
	}

}
