/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common;

/**
 * This class represents a color with an alpha value.
 * 
 * @author Michael Zangl
 */
public final class Color {
	/**
	 * Constant to quickly access black.
	 */
	public static final Color BLACK = new Color(0, 0, 0, 1);
	/**
	 * Constant to quickly access white.
	 */
	public static final Color WHITE = new Color(1, 1, 1, 1);
	/**
	 * Constant to quickly access red.
	 */
	public static final Color RED = new Color(1, 0, 0, 1);
	/**
	 * Constant to quickly access blue.
	 */
	public static final Color BLUE = new Color(0, 0, 1, 1);
	/**
	 * Constant to quickly access green.
	 */
	public static final Color GREEN = new Color(0, 1, 0, 1);
	/**
	 * Constant to quickly access a light green.
	 */
	public static final Color LIGHT_GREEN = new Color(0, 0.7f, 0, 1);
	/**
	 * Constant to quickly access orange.
	 */
	public static final Color ORANGE = new Color(1, 0.6f, 0, 1);
	/**
	 * Constant to quickly access cyan.
	 */
	public static final Color CYAN = new Color(0, 1, 1, 1);
	/**
	 * Constant to quickly access transparent.
	 */
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private static final int SHIFT_ARGB_A = 24;
	private static final int SHIFT_ARGB_R = 16;
	private static final int SHIFT_ARGB_G = 8;
	private static final int SHIFT_ARGB_B = 0;
	private static final int ARGB_FIELD_MAX = 0xff;
	private static final float VALUE_CONSIDERED_TRANSPARENT_BELOW = .1f;
	private static final int SHORT_SHIFT_RED = 11;
	private static final int SHORT_SHIFT_GREEN = 6;
	private static final int SHORT_SHIFT_BLUE = 1;
	private static final int SHORT_FIELD_MAX = 0x1f;
	private static final int SHORT_MASK_ALPHA = 1;

	private final float blue;
	private final float red;
	private final float green;
	private final float alpha;

	private final int argb;
	private final short shortColor;

	/**
	 * Creates a new color using the argb notation.
	 * 
	 * @param argb
	 *            An integer in the hexadecimal form: AARRGGBB
	 */
	public Color(int argb) {
		this(argb, argbFieldToFloat(argb >> SHIFT_ARGB_R),
				argbFieldToFloat(argb >> SHIFT_ARGB_G),
				argbFieldToFloat(argb >> SHIFT_ARGB_B),
				argbFieldToFloat(argb >> SHIFT_ARGB_A));
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

		this.shortColor = toShortColorForced(1);
	}

	/**
	 * Converts this color to a greyscale color.
	 * 
	 * @return The color in grey scale.
	 */
	public Color toGreyScale() {
		float intensity = 0.2126f * red + 0.7152f * green + 0.0722f * blue;
		return new Color(intensity, intensity, intensity, alpha);
	}

	/**
	 * Multiplies all color components with those of the other color.
	 * 
	 * @param color
	 *            The color.
	 * @return A new color.
	 */
	public Color multiply(Color color) {
		return new Color(red * color.getRed(), green * color.getGreen(), blue * color.getBlue(), alpha * color.getAlpha());
	}

	/**
	 * Gets the (float) alpha value.
	 * 
	 * @return The alpha value. Range is 0..1
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * Gets the (float) blue value.
	 * 
	 * @return The blue value. Range is 0..1
	 */
	public float getBlue() {
		return blue;
	}

	/**
	 * Gets the (float) green value.
	 * 
	 * @return The green value. Range is 0..1
	 */
	public float getGreen() {
		return green;
	}

	/**
	 * Gets the (float) red value.
	 * 
	 * @return The red value. Range is 0..1
	 */
	public float getRed() {
		return red;
	}

	/**
	 * Gets this color in ARGB notation.
	 * 
	 * @return The color as integer in ARGB format.
	 */
	public int getARGB() {
		return argb;
	}

	/**
	 * Converts a color given in float values to ARGB.
	 * 
	 * @param red
	 *            The red component. Range 0..1
	 * @param green
	 *            The green component. Range 0..1
	 * @param blue
	 *            The blue component. Range 0..1
	 * @param alpha
	 *            The alpha component. Range 0..1
	 * @return The color in argb notation.
	 */
	public static int getARGB(float red, float green, float blue,
			float alpha) {
		return floatToARGBField(alpha) << SHIFT_ARGB_A
				| floatToARGBField(red) << SHIFT_ARGB_R
				| floatToARGBField(green) << SHIFT_ARGB_G
				| floatToARGBField(blue) << SHIFT_ARGB_B;
	}

	/**
	 * Gets this color in ABGR notation.
	 * 
	 * @return The color as integer in ABGR format.
	 */
	public int getABGR() {
		return (argb & 0xff00ff00) | ((argb & 0xff) << 16) | ((argb >> 16) & 0xff);
	}

	/**
	 * Converts a color given in float values to ARGB.
	 * 
	 * @param red
	 *            The red component. Range 0..1
	 * @param green
	 *            The green component. Range 0..1
	 * @param blue
	 *            The blue component. Range 0..1
	 * @param alpha
	 *            The alpha component. Range 0..1
	 * @return The color in abgr notation.
	 */
	public static int getABGR(float red, float green, float blue, float alpha) {
		return getARGB(blue, green, red, alpha);
	}

	private static int floatToARGBField(float f) {
		return floatToAnyField(f, ARGB_FIELD_MAX);
	}

	private static int floatToAnyField(float f, int fieldMax) {
		return (int) (f * fieldMax) & fieldMax;
	}

	private static float argbFieldToFloat(int f) {
		return (float) (f & ARGB_FIELD_MAX) / ARGB_FIELD_MAX;
	}

	/**
	 * Convert a 16 bit color to a 32 bit color
	 * 
	 * @param color16bit
	 *            The 16 bit color in
	 * @return The 32 bit color;
	 */
	public static  int convertTo32Bit(int color16bit) {
		// TODO: Make faster
		float red = (float) ((color16bit >> 11) & 0x1f) / 0x1f;
		float green = (float) ((color16bit >> 6) & 0x1f) / 0x1f;
		float blue = (float) ((color16bit >> 1) & 0x1f) / 0x1f;
		float alpha = color16bit & 0x1;
		return Color.getARGB(red, green, blue, alpha);
	}

	private static final int[] table6to5 = new int[64];
	private static final int[] table5to8 = new int[2 << 5];

	static {
		// Generate table6to5
		for (int i = 0; i < 64; i++) {
			table6to5[i] = Math.round(i / 63.0f * 31.0f);
		}
		for (int i = 0; i < table5to8.length; i++) {
			table5to8[i] = Math.round(i / (table5to8.length - 1f) * 255.0f);
		}
	}

	private static  int convertColorChannel6to5(int c) {
		return table6to5[c];
	}

	public static  int convert565to555(int rgb565) {
		int r5 = (rgb565 & 0xf800) >> 11;
		int g6 = (rgb565 & 0x07e0) >> 5;
		int b5 = rgb565 & 0x001f;

		int g5 = convertColorChannel6to5(g6);

		int rgb555 = r5;
		rgb555 = rgb555 << 5;
		rgb555 |= g5;
		rgb555 = rgb555 << 5;
		rgb555 |= b5;

		return rgb555;
	}

	/**
	 * Converts this color to a short color value required by OpenGL.
	 * 
	 * @param multiply
	 *            The factor to multiply this color with.
	 * @return The short color.
	 */
	public short toShortColor(float multiply) {
		if (multiply == 1) {
			return shortColor;
		} else if (multiply < 0) {
			return BLACK.toShortColor(1);
		} else {
			return toShortColorForced(multiply);
		}
	}

	private short toShortColorForced(float multiply) {
		if (alpha < VALUE_CONSIDERED_TRANSPARENT_BELOW) {
			return 0;
		} else {
			return (short) (convertToShortField(red, multiply) << SHORT_SHIFT_RED
					| convertToShortField(green, multiply) << SHORT_SHIFT_GREEN
					| convertToShortField(blue, multiply) << SHORT_SHIFT_BLUE | SHORT_MASK_ALPHA);
		}
	}

	private int convertToShortField(float value, float multiply) {
		return floatToAnyField(Math.min(1, value * multiply), SHORT_FIELD_MAX);
	}

	/**
	 * Converts a short value to a color object.
	 * 
	 * @param s
	 *            The short
	 * @return The color object
	 * @see #toShortColor(float)
	 */
	public static Color fromShort(short s) {
		return new Color((float) (s >> SHORT_SHIFT_RED & SHORT_FIELD_MAX) / SHORT_FIELD_MAX, (float) (s >> SHORT_SHIFT_GREEN & SHORT_FIELD_MAX)
				/ SHORT_FIELD_MAX, (float) (s >> SHORT_SHIFT_BLUE & SHORT_FIELD_MAX)
						/ SHORT_FIELD_MAX,
				s & SHORT_MASK_ALPHA);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[argb=" + String.format("%08x", getARGB()) + "]";
	}

}
