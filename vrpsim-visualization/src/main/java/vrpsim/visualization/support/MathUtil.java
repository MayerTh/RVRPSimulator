package vrpsim.visualization.support;

import java.util.Vector;

public class MathUtil {
	/**
	 * Solving following equation with z1 = 50.
	 * 
	 * z2 = (-sqrt((-4 u2 cos(a) sqrt(u1^2+u2^2)-4 u2^2 z1)^2-4 (-cos(2 a)+2
	 * u2^2-1) (4 u2 z1 cos(a) sqrt(u1^2+u2^2)+u1^2 cos(2 a)+u2^2 cos(2 a)-z1^2
	 * cos(2 a)+u1^2+2 u2^2 z1^2+u2^2-z1^2))+4 u2 cos(a) sqrt(u1^2+u2^2)+4 u2^2
	 * z1)/(2 (-cos(2 a)+2 u2^2-1))
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param angle
	 * @return
	 */
	public static Vector<Double> calc(double startX, double startY, double endX, double endY, double angle) {

		double arrowHeadLengh = 5;
		
		double dx = endX - startX;
		double dy = endY - startY;
		
		double length = Math.sqrt(dx*dx+dy*dy);
		double unitDx = dx / length;
		double unitDy = dy / length;

		double point1X = endX - unitDx * arrowHeadLengh - unitDy * arrowHeadLengh;
		double point1Y = endY - unitDy * arrowHeadLengh + unitDx * arrowHeadLengh;
		
		double point2X = endX -unitDx * arrowHeadLengh + unitDy * arrowHeadLengh;
		double point2Y = endY - unitDy * arrowHeadLengh - unitDx * arrowHeadLengh;
				
		Vector<Double> result = new Vector<>();
		result.addElement(point1X);
		result.addElement(point1Y);
		result.addElement(point2X);
		result.addElement(point2Y);
		return result;
	}

}
