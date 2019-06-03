package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.twoopt;

public final class Point {

	private final double x;
	private final double y;
	private final String id;
	private final boolean isStart;
	private final boolean isDepot;
	private final boolean solveOpenTSP;
	private boolean active = true;

	public Point(final double x, final double y, final String id, final boolean solveOpenTSP, final boolean isStart, boolean isDepot) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.solveOpenTSP = solveOpenTSP;
		this.isStart = isStart;
		this.isDepot = isDepot;
	}

	public String getId() {
		return id;
	}

	public boolean isSolveOpenTSP() {
		return solveOpenTSP;
	}

	public boolean isStart() {
		return isStart;
	}

	public boolean isDepot() {
		return isDepot;
	}

	/**
	 * Euclidean distance. tour wraps around N-1 to 0.
	 */
	public static double distance(final Point[] points) {
		final int len = points.length;
		double d = points[len - 1].distance(points[0]);
		for (int i = 1; i < len; i++)
			d += points[i - 1].distance(points[i]);
		return d;
	}

	/**
	 * Euclidean distance.
	 */
	private final double distance(final Point to) {
		if (isDistanceNull(this, to)) {
			return 0.0;
		}
		return Math.sqrt(_distance(to));
	}

	/**
	 * compare 2 points. no need to square when comparing.
	 * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
	 */
	public final double _distance(final Point to) {
		if (isDistanceNull(this, to)) {
			return 0.0;
		}
		final double dx = this.x - to.x;
		final double dy = this.y - to.y;
		return (dx * dx) + (dy * dy);
	}

	private boolean isDistanceNull(Point from, Point to) {
		if (solveOpenTSP) {
			if (from.isStart && to.isDepot) {
				return true;
			}
			if (from.isDepot && to.isStart) {
				return true;
			}
		}
		return false;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public String toString() {
		return x + " " + y;
	}
}
