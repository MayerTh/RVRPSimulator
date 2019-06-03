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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.util;

import java.util.Map;

public class DynamicModelDegreeCalculationHelperTest {

	public static void main(String[] args) {
		testNNN();
	}

	private static void testNNN() {
		double[][] points = getPoints();
		// double[][] nnn = DynamicModelDegreeCalculationHelper.getNNearestNeighbors(0,
		// 0, points, 6);
		System.out.println("(" + points[0][0] + "," + points[0][1] + ")(" + points[1][0] + "," + points[1][1] + ")(" + points[5][0] + ","
				+ points[5][1] + ")");
		double angle = DynamicModelDegreeCalculationHelper.calculateAngle(points[0][0], points[0][1], points[1][0], points[1][1],
				points[5][0], points[5][1]);
		System.out.println(angle);
	}

	private static void printMap(Map<String, Double> values) {
		String line = "";
		for (String key : values.keySet()) {
			double v = values.get(key);
			line += key + "=" + v + ",";
		}
		System.out.println(line);
	}

	private static void printPoint(double[][] points) {
		String line = "";
		for (int i = 0; i < points.length; i++) {
			double x = points[i][0];
			double y = points[i][1];
			line += "(" + x + "," + y + ")";
		}
		System.out.println(line);
	}

	private static double[][] getPoints() {

		double[][] points = new double[10][];
		points[0] = new double[2];
		points[0][0] = 1;
		points[0][1] = 1;

		points[1] = new double[2];
		points[1][0] = 2;
		points[1][1] = 1;

		points[2] = new double[2];
		points[2][0] = 3;
		points[2][1] = 1;

		points[3] = new double[2];
		points[3][0] = 4;
		points[3][1] = 1;

		points[4] = new double[2];
		points[4][0] = 5;
		points[4][1] = 1;

		points[5] = new double[2];
		points[5][0] = 1;
		points[5][1] = 2;

		points[6] = new double[2];
		points[6][0] = 2;
		points[6][1] = 2;

		points[7] = new double[2];
		points[7][0] = 3;
		points[7][1] = 2;

		points[8] = new double[2];
		points[8][0] = 4;
		points[8][1] = 2;

		points[9] = new double[2];
		points[9][0] = 5;
		points[9][1] = 2;

		return points;
	}

}
