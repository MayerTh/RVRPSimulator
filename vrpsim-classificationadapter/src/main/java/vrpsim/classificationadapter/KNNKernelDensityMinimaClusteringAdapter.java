package vrpsim.classificationadapter;

import de.lmu.ifi.dbs.elki.algorithm.clustering.onedimensional.KNNKernelDensityMinimaClustering;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.math.statistics.kernelfunctions.GaussianKernelDensityFunction;

public class KNNKernelDensityMinimaClusteringAdapter {

	private Database database;

	public Database getDatabase() {
		return database;
	}

	public Clustering<ClusterModel> executeKMeans(double[][] data) {

		int dim = 1;
		int k = 1;
		int minWindow = 40;

		KNNKernelDensityMinimaClustering<NumberVector> knn = new KNNKernelDensityMinimaClustering<>(dim,
				GaussianKernelDensityFunction.KERNEL, KNNKernelDensityMinimaClustering.Mode.BALLOON, k, minWindow);
		DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
		this.database = new StaticArrayDatabase(dbc, null);
		database.initialize();

		return knn.run(database);
	}

}
