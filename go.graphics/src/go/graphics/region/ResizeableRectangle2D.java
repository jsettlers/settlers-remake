package go.graphics.region;

public class ResizeableRectangle2D {
	public double minx;
	public double maxx;
	public double miny;
	public double maxy;

	public ResizeableRectangle2D(double minx, double maxx, double miny, double maxy) {
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
	}

	public double getHeight() {
		return maxy - miny;
	}

	public double getWidth() {
		return maxy - miny;
	}
}
