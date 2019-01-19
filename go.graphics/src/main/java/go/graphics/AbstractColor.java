package go.graphics;

public abstract class AbstractColor {

	protected AbstractColor(int argb, float red, float green, float blue, float alpha) {
		this.argb = argb;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public final float blue;
	public final float red;
	public final float green;
	public final float alpha;
	public final int argb;
}
