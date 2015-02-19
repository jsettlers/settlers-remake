package jsettlers.logic.map.random.geometry;

public class Point2D {

	private final double y;
	private final double x;

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D interpolate(Point2D to, double percentage) {
		return new Point2D(to.getX() * percentage + getX() * (1 - percentage),
				to.getY() * percentage + getY() * (1 - percentage));
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public double distanceSquared(Point2D center) {
		double dx = center.x - x;
		double dy = center.y - y;
		return dx * dx + dy * dy;
	}

	public Point getIntPoint() {
		return new Point((int) Math.round(x), (int) Math.round(y));
	}

	public double getDirectionTo(Point2D point) {
		return Math.atan2(y - point.y, x - point.x);
	}
}
