/*******************************************************************************
 * Copyright (c) 2015
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

public final class Color {
	public static final Color BLACK = new Color(0, 0, 0, 1);
	public static final Color WHITE = new Color(1, 1, 1, 1);
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color RED = new Color(1, 0, 0, 1);
	public static final Color BLUE = new Color(0, 0, 1, 1);
	public static final Color GREEN = new Color(0, 1, 0, 1);
	public static final Color LIGHT_GREEN = new Color(0, 0.7f, 0, 1);
	public static final Color ORANGE = new Color(1, 0.6f, 0, 1);
	public static final Color CYAN = new Color(0, 1, 1, 1);

	private final float blue;
	private final float red;
	private final float green;
	private final float alpha;

	private final int argb;
	private final short shortColor;

	public Color(int argb) {
		this(argb, ((argb >> 16) & 0xff) / 255f, ((argb >> 8) & 0xff) / 255f,
				((argb >> 0) & 0xff) / 255f, ((argb >> 24) & 0xff) / 255f);
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

	public final int getABGR() {
		return (argb & 0xff00ff00) | ((argb & 0xff) << 16) | ((argb >> 16) & 0xff);
	}

	public static final int getARGB(float red, float green, float blue,
			float alpha) {
		return ((int) (alpha * 255) & 0xff) << 24
				| ((int) (red * 255) & 0xff) << 16
				| ((int) (green * 255) & 0xff) << 8
				| ((int) (blue * 255) & 0xff);
	}

	public static final int getABGR(float red, float green, float blue, float alpha) {
		return ((int) (alpha * 255) & 0xff) << 24 | ((int) (red * 255) & 0xff)
				| ((int) (green * 255) & 0xff) << 8
				| ((int) (blue * 255) & 0xff) << 16;
	}

	public static final int convert565to555(int rgb565) {
		int r5 = (int)((rgb565 & 0xF800) >> 11);
		int g6 = (int)((rgb565 & 0x07E0) >> 5);
		int b5 = (int)(rgb565 & 0x001F);

		int g5 = (int)( (float) g6 * 31.0f / 63.0f + 0.5f );

		int rgb555 = r5;
		rgb555 = rgb555 << 5;
		rgb555 |= g5;
		rgb555 = rgb555 << 5;
		rgb555 |= b5;

		return rgb555;
	}

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
		if (alpha < .1f) {
			return 0;
		} else {
			return (short) ((int) (Math.min(1, red * multiply) * 0x1f) << 11
					| (int) (Math.min(1, green * multiply) * 0x1f) << 6
					| (int) (Math.min(1, blue * multiply) * 0x1f) << 1 | 1);
		}
	}

	public static Color fromShort(short s) {
		return new Color((float) (s >> 11 & 0x1f) / 0x1f,
				(float) (s >> 6 & 0x1f) / 0x1f, (float) (s >> 1 & 0x1f) / 0x1f,
				s & 1);
	}

}
