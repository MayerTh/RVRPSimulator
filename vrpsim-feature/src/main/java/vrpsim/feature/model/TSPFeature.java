package vrpsim.feature.model;

public enum TSPFeature {

	centroid_centroid_x("centroid_centroid_x"), chull_points_on_hull("chull_points_on_hull"), angle_mean("angle_mean"),
	distance_mode_quantity("distance_mode_quantity"), bounding_box_20_ratio_of_cities_outside_box("bounding_box_20_ratio_of_cities_outside_box"),
	distance_distinct_distances("distance_distinct_distances"), mst_depth_median("mst_depth_median"), mst_dists_mean("mst_dists_mean"),
	centroid_dist_max("centroid_dist_max"), cluster_10pct_mean_distance_to_centroid("cluster_10pct_mean_distance_to_centroid"),
	distance_mean_tour_length("distance_mean_tour_length"), mst_dists_median("mst_dists_median"), distance_sd("distance_sd"),
	mst_dists_coef_of_var("mst_dists_coef_of_var"), distance_coef_of_var("distance_coef_of_var"), angle_span("angle_span"),
	bounding_box_10_ratio_of_cities_outside_box("bounding_box_10_ratio_of_cities_outside_box"), angle_max("angle_max"), mst_depth_max("mst_depth_max"),
	angle_min("angle_min"), mst_dists_sum("mst_dists_sum"), nnds_sd("nnds_sd"), angle_median("angle_median"), modes_number("modes_number"),
	centroid_centroid_y("centroid_centroid_y"), nnds_min("nnds_min"), mst_dists_max("mst_dists_max"),
	cluster_01pct_number_of_clusters("cluster_01pct_number_of_clusters"), mst_depth_span("mst_depth_span"), nnds_mean("nnds_mean"),
	mst_depth_coef_of_var("mst_depth_coef_of_var"), cluster_05pct_mean_distance_to_centroid("cluster_05pct_mean_distance_to_centroid"), distance_min("distance_min"),
	mst_dists_span("mst_dists_span"), distance_max("distance_max"), mst_dists_sd("mst_dists_sd"), nnds_median("nnds_median"), nnds_coef_of_var("nnds_coef_of_var"),
	centroid_dist_mean("centroid_dist_mean"), centroid_dist_sd("centroid_dist_sd"),
	bounding_box_30_ratio_of_cities_outside_box("bounding_box_30_ratio_of_cities_outside_box"), distance_median("distance_median"), distance_mean("distance_mean"),
	cluster_01pct_mean_distance_to_centroid("cluster_01pct_mean_distance_to_centroid"), mst_depth_mean("mst_depth_mean"),
	centroid_dist_median("centroid_dist_median"), distance_mode_mean("distance_mode_mean"), mst_dists_min("mst_dists_min"), centroid_dist_min("centroid_dist_min"),
	distance_distances_shorter_mean_distance("distance_distances_shorter_mean_distance"), distance_sum_of_lowest_edge_values("distance_sum_of_lowest_edge_values"),
	distance_span("distance_span"), nnds_span("nnds_span"), chull_area("chull_area"), angle_sd("angle_sd"), centroid_dist_coef_of_var("centroid_dist_coef_of_var"),
	distance_mode_frequency("distance_mode_frequency"), mst_depth_min("mst_depth_min"), centroid_dist_span("centroid_dist_span"),
	angle_coef_of_var("angle_coef_of_var"), mst_depth_sd("mst_depth_sd"), nnds_max("nnds_max"), cluster_05pct_number_of_clusters("cluster_05pct_number_of_clusters"),
	cluster_10pct_number_of_clusters("cluster_10pct_number_of_clusters"), ALL("ALL");
	
	private final String value;

	private TSPFeature(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}

}
