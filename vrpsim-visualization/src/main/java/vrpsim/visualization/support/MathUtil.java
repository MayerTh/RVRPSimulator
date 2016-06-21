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
