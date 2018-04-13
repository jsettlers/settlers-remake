/*
 * Copyright (c) 2018
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
 */
package jsettlers.textures.generation;

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
	 * 		An integer in the hexadecimal form: AARRGGBB
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

	/**
	 * Converts a color given in float values to ARGB.
	 *
	 * @param red
	 * 		The red component. Range 0..1
	 * @param green
	 * 		The green component. Range 0..1
	 * @param blue
	 * 		The blue component. Range 0..1
	 * @param alpha
	 * 		The alpha component. Range 0..1
	 * @return The color in argb notation.
	 */
	public static int getARGB(float red, float green, float blue,
			float alpha) {
		return floatToARGBField(alpha) << SHIFT_ARGB_A
				| floatToARGBField(red) << SHIFT_ARGB_R
				| floatToARGBField(green) << SHIFT_ARGB_G
				| floatToARGBField(blue) << SHIFT_ARGB_B;
	}

	private static int floatToARGBField(float f) {
		return floatToAnyField(f, ARGB_FIELD_MAX);
	}

	private Color(int argb, float red, float green, float blue, float alpha) {
		this.argb = argb;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;

		this.shortColor = toShortColorForced(1);
	}

	private static float argbFieldToFloat(int f) {
		return (float) (f & ARGB_FIELD_MAX) / ARGB_FIELD_MAX;
	}

	/**
	 * Converts this color to a short color value required by OpenGL.
	 *
	 * @param multiply
	 * 		The factor to multiply this color with.
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

	private static int floatToAnyField(float f, int fieldMax) {
		return (int) (f * fieldMax) & fieldMax;
	}
}
