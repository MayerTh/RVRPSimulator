package vrpsim.feature.model;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.correlation.StorelessCovariance;

@Deprecated
public class MahalanobisDistance implements DistanceMeasure {

	// P. C. Mahalanobis: On the generalised distance in statistics. In: Proceedings
	// of the National Institute of Science of India. Vol. 2, Nr. 1, 1936, S. 49–55
	
	private static final long serialVersionUID = -312952833420324456L;
	RealMatrix invertedCovarianceMatrix = null;
	
	public MahalanobisDistance(List<Vector<Double>> vectors) {
		int numberVectors = vectors.size();
		int vectorSize = vectors.get(0).size();
		double[][] data = new double[numberVectors][vectorSize];
		for(int i = 0; i < numberVectors; i++) {
			data[i] = vectors.get(i).stream().mapToDouble(Double::doubleValue).toArray();
		}
		this.initInvertedCovarianceMatrix(data);
	}
	
	public MahalanobisDistance(double[][] data) {
		this.initInvertedCovarianceMatrix(data);
	}
	
	private void initInvertedCovarianceMatrix(double[][] data) {
		StorelessCovariance slc = new StorelessCovariance(data[0].length);
		for(int i = 0; i < data.length; i++) {
//			System.out.println(out(data[i]));
			slc.increment(data[i]);
		}
		RealMatrix tmp = slc.getCovarianceMatrix();
//		System.out.println(data.length);
		this.invertedCovarianceMatrix = new LUDecomposition(tmp).getSolver().getInverse();
	}

	private String out(double[] data) {
		String str = "";
		for(int i = 0; i < data.length; i++) {
			str += data[i];
			if(i < data.length-1) {
				str += ", ";
			}
		}
		return str;
	}
	
	@Override
	public double compute(double[] pv1, double[] pv2) {
	
		// If the arrays are the same, the distance is 0.0 
        if (Arrays.equals(pv1, pv2)) { 
            return 0.0; 
        } 
         
        // Create a new array with the difference between the two arrays 
        double [] diff = new double[pv1.length]; 
         
        for (int i = 0; i < pv1.length; i++) { 
            diff[i] = pv1[i] - pv2[i]; 
        }        
         
        // Left-hand side of the equation: vector * invcov^-1 
        double [] left = this.invertedCovarianceMatrix.preMultiply(diff); 
         
        // Compute the dot product of both vectors 
        double res = 0.0; 
        for (int i = 0; i < diff.length; i++) { 
            res += left[i] * diff[i]; 
        } 
         
        return Math.sqrt(res);
	}

}
