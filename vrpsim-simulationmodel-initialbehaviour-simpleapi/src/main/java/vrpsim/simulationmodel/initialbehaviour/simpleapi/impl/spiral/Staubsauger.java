package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.spiral;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.LocationAPI;

public class Staubsauger {
	int startRow;
	int startColumn;
	int currentRow;
	int currentColumn;
	boolean[][] cellsVisited;
	Raster raster;

	public Staubsauger(LocationAPI location, Raster raster) {
		super();
		this.raster = raster;
		this.currentRow = raster.getCellRow(location);
		this.currentColumn = raster.getCellColumn(location);
		this.startRow = raster.getCellColumn(location);
		this.startColumn = raster.getCellColumn(location);
		this.cellsVisited = new boolean[raster.getRows()][raster.getColumns()];
		for (int column = 0; column < raster.getColumns(); column++) {
			for (int row = 0; row < raster.getRows(); row++) {
				cellsVisited[column][row] = false;
			}
		}
		cellsVisited[this.currentColumn][this.currentRow] = true;
	}

	public Staubsauger(int column, int row, Raster raster) {
		super();
		this.raster = raster;
		this.currentRow = row;
		this.currentColumn = column;
		this.startRow = row;
		this.startColumn = column;
		this.cellsVisited = new boolean[raster.getColumns()][raster.getRows()];
		for (int pcolumn = 0; pcolumn < raster.getColumns(); pcolumn++) {
			for (int prow = 0; prow < raster.getRows(); prow++) {
				cellsVisited[pcolumn][prow] = false;
			}
		}
		cellsVisited[this.currentColumn][this.currentRow] = true;
	}
	
	public boolean hasUnvisitedCell() {
		for (int column = 0; column < raster.getColumns(); column++) {
			for (int row = 0; row < raster.getRows(); row++) {
				if (cellsVisited[column][row] == false)
					return true;
			}
		}
		return false;
	}
	
	public boolean isCellUnvisited(int column, int row) {
		if ((column > cellsVisited.length || row > cellsVisited[column].length)) return false; else {
			return !cellsVisited[column][row];}
	}

	public void moveUp() {
		this.currentRow += 1;
		cellsVisited[this.currentColumn][this.currentRow] = true;
//		System.out.println("Up");
	}

	public void moveDown() {
		this.currentRow -= 1;
		cellsVisited[this.currentColumn][this.currentRow] = true;
//		System.out.println("Down");
	}

	public void moveLeft() {
		this.currentColumn -= 1;
		cellsVisited[this.currentColumn][this.currentRow] = true;
//		System.out.println("Left");
	}

	public void moveRight() {
		this.currentColumn += 1;
		cellsVisited[this.currentColumn][this.currentRow] = true;
//		System.out.println("Right");
	}

	public int getStartRow() {
		return startRow;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public int getCurrentColumn() {
		return currentColumn;
	}
	public Raster getRaster() {
		return raster;
	}
	
}