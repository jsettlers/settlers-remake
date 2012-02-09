package jsettlers.common;

public final class Color {
	public static final Color BLACK = new Color(0, 0, 0, 1);
	public static final Color WHITE = new Color(1, 1, 1, 1);
	public static final Color TRANSPARENT = new Color(1, 1, 1, 0);
	public static final Color RED = new Color(1, 0, 0, 1);
	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color GREEN = new Color(0, 1, 0, 1);

	public final float blue;
	public final float red;
	public final float green;
	public final float alpha;

	public Color(int rgbhex) {
		this(((rgbhex >> 16) & 0xff) / 255f, ((rgbhex >> 8) & 0xff) / 255f, ((rgbhex >> 0) & 0xff) / 255f, 1f);
	}

	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public final float getAlpha() {
		return alpha;
	}

	public final float getBlue() {
		return blue;
	}

	public final float getGreen() {
		return green;
	}

	public final float getRed() {
		return red;
	}

	public final int getRGB() {
		return ((int) (alpha * 255) & 0xff) << 24 | ((int) (red * 255) & 0xff) << 16 | ((int) (green * 255) & 0xff) << 8
				| ((int) (blue * 255) & 0xff);
	}
}
