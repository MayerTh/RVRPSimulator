package vrpsim.classificationadapter;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractNumberVectorDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;

public class DBSCANAdapter {

	private Database db;

	public Database getDatabase() {
		return this.db;
	}

	public Clustering<Model> executeDBSCAN(double[][] data, DistanceMeasure distanceMeasure) {
		String[] labels = LabelUtil.generateLableList(data);
		return this.executeDBSCAN(data, labels, distanceMeasure);
	}

	public Clustering<Model> executeDBSCAN(double[][] data, String[] labels, DistanceMeasure distanceMeasure) {

		DistanceFunction<NumberVector> mappedDistanceFunction = map(distanceMeasure);
		DBSCANParameterEstimator parameter = new DBSCANParameterEstimator(distanceMeasure);
		double epsilon = parameter.getEstimateForEpsilon(data);
		int minPoints = parameter.getEstimateForMinPoints(data, epsilon);

		DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data, labels);
		db = new StaticArrayDatabase(dbc, null);
		db.initialize();
		DBSCAN<NumberVector> dbscan = new DBSCAN<NumberVector>(mappedDistanceFunction, epsilon, minPoints);
		Clustering<Model> result = dbscan.run(db);

		return result;
	}

	private DistanceFunction<NumberVector> map(DistanceMeasure distanceMeasure) {
		return new DistanceFunctionMapper(distanceMeasure);
	}

	class DistanceFunctionMapper extends AbstractNumberVectorDistanceFunction {

		private final DistanceMeasure distanceMeasure;

		public DistanceFunctionMapper(DistanceMeasure distanceMeasure) {
			this.distanceMeasure = distanceMeasure;
		}

		@Override
		public double distance(NumberVector o1, NumberVector o2) {
			return this.distanceMeasure.compute(translate(o1), translate(o2));
		}

		private double[] translate(NumberVector nv) {
			int dim = nv.getDimensionality();
			double[] result = new double[dim];
			for (int i = 0; i < dim; i++) {
				result[i] = nv.doubleValue(i);
			}
			return result;
		}

	}

}
