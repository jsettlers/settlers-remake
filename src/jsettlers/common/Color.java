package jsettlers.common;

public class Color extends go.graphics.Color {
	public static final Color BLACK = new Color(0, 0, 0, 1);
	public static final Color WHITE = new Color(1, 1, 1, 1);
	public static final Color TRANSPARENT = new Color(1, 1, 1, 0);
	public static final Color RED = new Color(1, 0, 0, 1);
	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color GREEN = new Color(0, 1, 0, 1);

	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color RED = new Color(1, 0, 0, 1);

	public Color(float red, float green, float blue, float alpha) {
		super(red, green, blue, alpha);
	}

	public Color(int i) {
		super(i);
	}
}
