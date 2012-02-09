package jsettlers.common;

public final class Color {
	public static final Color BLACK = new Color(0, 0, 0, 1);
	public static final Color WHITE = new Color(1, 1, 1, 1);
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color RED = new Color(1, 0, 0, 1);
	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color GREEN = new Color(0, 1, 0, 1);

	private final float blue;
	private final float red;
	private final float green;
	private final float alpha;

	private final int argb;

	public Color(int rgb) {
		this(0xff << 24 | rgb, ((rgb >> 16) & 0xff) / 255f, ((rgb >> 8) & 0xff) / 255f, ((rgb >> 0) & 0xff) / 255f, 1f);
	}

	private Color(float red, float green, float blue, float alpha) {
		this(Color.getARGB(red, green, blue, alpha), red, green, blue, alpha);
	}

	private Color(int argb, float red, float green, float blue, float alpha) {
		this.argb = argb;
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

	public final int getARGB() {
		return argb;
	}

	public static final int getARGB(float red, float green, float blue, float alpha) {
		return ((int) (alpha * 255) & 0xff) << 24 | ((int) (red * 255) & 0xff) << 16 | ((int) (green * 255) & 0xff) << 8
				| ((int) (blue * 255) & 0xff);
	}
}
