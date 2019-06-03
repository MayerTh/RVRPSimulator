package vrpsim.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import vrpsim.feature.model.TSPFeature;
import vrpsim.r.util.api.IRServiceAPI;
import vrpsim.r.util.impl.RServiceImpl;

/**
 * Generates the TSP feature vector with the help of R package tspmeta.
 * Following 64 features can be calculated:
 * 
 * centroid_centroid_x, chull_points_on_hull, angle_mean,
 * distance_mode_quantity, bounding_box_20_ratio_of_cities_outside_box,
 * distance_distinct_distances, mst_depth_median, mst_dists_mean,
 * centroid_dist_max, cluster_10pct_mean_distance_to_centroid,
 * distance_mean_tour_length, mst_dists_median, distance_sd,
 * mst_dists_coef_of_var, distance_coef_of_var, angle_span,
 * bounding_box_10_ratio_of_cities_outside_box, angle_max, mst_depth_max,
 * angle_min, mst_dists_sum, nnds_sd, angle_median, modes_number,
 * centroid_centroid_y, nnds_min, mst_dists_max,
 * cluster_01pct_number_of_clusters, mst_depth_span, nnds_mean,
 * mst_depth_coef_of_var, cluster_05pct_mean_distance_to_centroid, distance_min,
 * mst_dists_span, distance_max, mst_dists_sd, nnds_median, nnds_coef_of_var,
 * centroid_dist_mean, centroid_dist_sd,
 * bounding_box_30_ratio_of_cities_outside_box, distance_median, distance_mean,
 * cluster_01pct_mean_distance_to_centroid, mst_depth_mean,
 * centroid_dist_median, distance_mode_mean, mst_dists_min, centroid_dist_min,
 * distance_distances_shorter_mean_distance, distance_sum_of_lowest_edge_values,
 * distance_span, nnds_span, chull_area, angle_sd, centroid_dist_coef_of_var,
 * distance_mode_frequency, mst_depth_min, centroid_dist_span,
 * angle_coef_of_var, mst_depth_sd, nnds_max, cluster_05pct_number_of_clusters,
 * cluster_10pct_number_of_clusters,
 * 
 * For using this TSPFeatures:
 * 
 * 1. Install R 2. Install R package tspmeta with command:
 * install.packages("tspmeta") 3. Install R package Rserve with command:
 * install.packages("Rserve") 4. Load library Rserve with command:
 * library(Rserve) 5. Start R service with command: Rserve()
 * 
 * @author mayert
 *
 */
public class TSPFeatures implements Feature {

	private static Logger logger = LoggerFactory.getLogger(TSPFeatures.class);
	private final IRServiceAPI rServiceAPI;
	private RConnection rConnection;
	private HashMap<String, Double> featureMap = new HashMap<>();
	private final TSPFeature feature;
	private final boolean rescaleFeatures;

	public TSPFeatures(boolean rescaleFeatures) {
		this.rescaleFeatures = rescaleFeatures;
		this.feature = TSPFeature.ALL;
		rServiceAPI = new RServiceImpl();
		this.rConnection = rServiceAPI.establishRConnection();
	}
	
	public TSPFeatures(int rPort, String rAdress, TSPFeature feature, boolean rescaleFeatures)
			throws InstantiationException, IllegalAccessException {
		this.rescaleFeatures = rescaleFeatures;
		this.feature = feature;
		rServiceAPI = new RServiceImpl();
		this.rConnection = rServiceAPI.establishRConnection(rAdress, rPort);
	}

	public TSPFeatures(int rPort, TSPFeature feature, boolean rescaleFeatures) throws InstantiationException, IllegalAccessException {
		this.rescaleFeatures = rescaleFeatures;
		this.feature = feature;
		rServiceAPI = new RServiceImpl();
		this.rConnection = rServiceAPI.establishRConnection(rPort);
	}

	public TSPFeatures(String rAdress, TSPFeature feature, boolean rescaleFeatures) throws InstantiationException, IllegalAccessException {
		this.rescaleFeatures = rescaleFeatures;
		this.feature = feature;
		rServiceAPI = new RServiceImpl();
		this.rConnection = rServiceAPI.establishRConnection(rAdress);
	}

	public TSPFeatures(TSPFeature feature, boolean rescaleFeatures) throws InstantiationException, IllegalAccessException {
		this.rescaleFeatures = rescaleFeatures;
		this.feature = feature;
		rServiceAPI = new RServiceImpl();
		this.rConnection = rServiceAPI.establishRConnection();
	}

	@Override
	public FeatureIdentifier getFeatureIdentifier() {
		return FeatureIdentifier.TSPMETA;
	}

	@Override
	public double[] getFeature(double[][] data) {

		String errorLine = null;
		
		if(!this.rConnection.isConnected()) {
			this.rConnection = rServiceAPI.establishRConnection();
		}

		try {

			String rescale = this.rescaleFeatures ? "TRUE" : "FALSE";

			
			rServiceAPI.evaluate(this.rConnection, buildScript(buildCoords(data)));
			String toEvolve = errorLine = "features(tsp.ins, rescale=\"" + rescale + "\")";
			REXP rexp = rConnection.eval(toEvolve);
			REXPGenericVector rexpVector = (REXPGenericVector) rexp;

			if (rexpVector.asNativeJavaObject() instanceof HashMap) {
				@SuppressWarnings("rawtypes")
				HashMap map = (HashMap) rexpVector.asNativeJavaObject();

				for (Object o : map.keySet()) {

					Double value = null;
					if (o instanceof double[]) {
						double[] oda = (double[]) o;
						value = oda[0];
					} else if (o instanceof int[]) {
						int[] oia = (int[]) o;
						value = new Double(oia[0]);
					} else if (o instanceof byte[]) {
						@SuppressWarnings("unused")
						byte[] oba = (byte[]) o;
						value = Double.NaN;
					} else {
						logger.error("Not supported class/array type: {}", o.getClass().getSimpleName());
						throw new RuntimeException("Not supported class/array type: " + o.getClass().getSimpleName());
					}

					this.featureMap.put(map.get(o).toString(), value);
				}
			}

		} catch (RserveException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (IOException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (JAXBException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (XMLStreamException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (FactoryConfigurationError e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (SAXException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		} catch (REXPMismatchException e) {
			rConnection.close();
			e.printStackTrace();
			logger.error("Exception {} during evaluation of line: {}", e.getMessage(), errorLine);
		}

		rConnection.close();

		double[] result;
		if (this.feature.equals(TSPFeature.ALL)) {
			result = this.toArray(new ArrayList<>(featureMap.values()));
		} else {
			result = this.toArray(featureMap.get(this.feature.getValue()));
		}
		return result;
	}

	public double[] getAdditionalFeature(TSPFeature feature) {
		return this.toArray(featureMap.get(feature.getValue()));
	}
	
	public HashMap<String, Double> getFeatureMap(double[][] data) {
		this.getFeature(data);
		return this.featureMap;
	}

	private double[] toArray(List<Double> list) {
		double[] result = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private double[] toArray(Double value) {
		List<Double> values = new ArrayList<>();
		if (value != null) {
			values.add(value);
		}
		return toArray(values);
	}

	private List<String> buildScript(String[] data) {
		List<String> result = new ArrayList<>();
		result.add("library(tspmeta)");
		result.add("data.x  <- c(" + data[0] + ")");
		result.add("data.y  <- c(" + data[1] + ")");
		result.add("coords.df <- data.frame(long=data.x, lat=data.y)");
		result.add("coords.mx <- as.matrix(coords.df)");
		result.add("dist.mx <- dist(coords.mx)");
		result.add("tsp.ins <- tsp_instance(coords.mx, dist.mx)");
		return result;
	}

	private String[] buildCoords(double[][] data)
			throws IOException, JAXBException, XMLStreamException, FactoryConfigurationError, SAXException {

		String xLine = "";
		String yLine = "";

		for (int i = 0; i < data.length; i++) {
			xLine += data[i][0];
			yLine += data[i][1];
			if (i < data.length - 1) {
				xLine += ",";
				yLine += ",";
			}
		}

		String[] result = new String[2];
		result[0] = xLine;
		result[1] = yLine;
		return result;
	}

}
