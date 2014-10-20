package go.graphics;

public class UIPoint {
	private final double x;
	private final double y;

	public UIPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
	    return x;
    }
	public double getY() {
	    return y;
    }
	
	public double distance(UIPoint other) {
		return Math.hypot(x - other.x, y - other.y);
	}
}
