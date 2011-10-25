package jsettlers.common;

public class Color extends go.graphics.Color {

	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color RED = new Color(1, 0, 0, 1);

	public Color(float red, float green, float blue, float alpha) {
		super(red, green, blue, alpha);
	}

	public Color(int i) {
		super(i);
	}
}
