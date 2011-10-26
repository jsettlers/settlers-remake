package go.graphics;

public class Color {
	private final float blue;
	private final float red;
	private final float green;
	private final float alpha;

	public Color(int rgbhex) {
		this(((rgbhex >> 16) & 0xff) / 255f, ((rgbhex >> 8) & 0xff) / 255f,
		        ((rgbhex >> 0) & 0xff) / 255f, 1f);
	}

	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public float getBlue() {
		return blue;
	}

	public float getGreen() {
		return green;
	}

	public float getRed() {
		return red;
	}

	public int getRGB() {
		return ((int) (alpha * 255) & 0xff) << 24
		        | ((int) (red * 255) & 0xff) << 16
		        | ((int) (green * 255) & 0xff) << 8
		        | ((int) (blue * 255) & 0xff);
	}

	public Color multiply(float f) {
		return new Color(Math.min(red * f, 1), Math.min(green * f, 1), Math.min(
		        blue * f, 1), alpha);
	}
}
