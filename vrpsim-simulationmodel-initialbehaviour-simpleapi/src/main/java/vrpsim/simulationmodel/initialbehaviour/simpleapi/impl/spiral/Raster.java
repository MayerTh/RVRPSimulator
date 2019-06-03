package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.spiral;

import java.util.ArrayList;
import java.util.List;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.LocationAPI;

public class Raster {

	private int rows;
	private int columns;
	private LocationAPI bottomLeft;
	private LocationAPI topRight;
	private double height;
	private double length;
	private double cellHeight;
	private double cellLength;
	private LocationAPI[][] cells;

	public Raster(int columns, int rows, LocationAPI bottomLeft, LocationAPI topRight) {
		super();
		this.rows = rows;
		this.columns = columns;
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
		this.height = topRight.getyCoord() - bottomLeft.getyCoord();
		this.length = topRight.getxCoord() - bottomLeft.getxCoord();
		this.cellHeight = this.height / (rows - 1);
		this.cellLength = this.length / (columns - 1);
		this.cells = new LocationAPI[columns][rows];
		double x = bottomLeft.getxCoord();
		double y = bottomLeft.getyCoord();
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				cells[i][j] = new LocationAPI(x, y);
				y += cellHeight;
			}
			x += cellLength;
			y = bottomLeft.getyCoord();
		}
	}

	public List<CustomerAPI> getJobsInCell(int column, int row, List<CustomerAPI> jobs) {
		List<CustomerAPI> res = new ArrayList<>();
		LocationAPI cellLocation = cells[column][row];
		for (CustomerAPI job : jobs) {
			if (isJobInCell(job, cellLocation))
				res.add(job);
		}
		return res;
	}

	public LocationAPI getCellLocation(IJob job) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (isJobInCell(job, cells[i][j]))
					return cells[i][j];
			}
		}
		return null;
	}

	public int getCellRow(IJob job) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (isJobInCell(job, cells[i][j]))
					return j;
			}
		}
		return -1;
	}

	public int getCellRow(LocationAPI location) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (isLocationInCell(location, cells[i][j]))
					return j;
			}
		}
		return -1;
	}

	public int getCellColumn(IJob job) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (isJobInCell(job, cells[i][j]))
					return i;
			}
		}
		return -1;
	}

	public int getCellColumn(LocationAPI location) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (isLocationInCell(location, cells[i][j]))
					return j;
			}
		}
		return -1;
	}

	private boolean isJobInCell(IJob job, LocationAPI cellLocation) {
		double jobx = job.getLocation().getxCoord();
		double joby = job.getLocation().getyCoord();
		if ((jobx >= cellLocation.getxCoord()) && (joby >= cellLocation.getyCoord()) && (jobx < cellLocation.getxCoord() + cellLength)
				&& (joby < cellLocation.getyCoord() + cellHeight))
			return true;
		else
			return false;
	}

	private boolean isLocationInCell(LocationAPI location, LocationAPI cellLocation) {
		double locx = location.getxCoord();
		double locy = location.getyCoord();
		if ((locx >= cellLocation.getxCoord()) && (locy >= cellLocation.getyCoord()) && (locx < cellLocation.getxCoord() + cellLength)
				&& (locy < cellLocation.getyCoord() + cellHeight))
			return true;
		else
			return false;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public LocationAPI getBottomLeft() {
		return bottomLeft;
	}

	public LocationAPI getTopRight() {
		return topRight;
	}

	public double getHeight() {
		return height;
	}

	public double getLength() {
		return length;
	}

	public double getCellHeight() {
		return cellHeight;
	}

	public double getCellLength() {
		return cellLength;
	}

	public LocationAPI[][] getCells() {
		return cells;
	}

}
