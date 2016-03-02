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
package jsettlers.mapcreator.noise;

/**
 * Part of the noise generator
 * 
 * @author Andreas Butti
 */
public class NoiseSet {

	private final int prime1;
	private final int prime2;
	private final int prime3;

	/**
	 * Constructor
	 * 
	 * @param prime1
	 *            prime 1
	 * @param prime2
	 *            prime 2
	 * @param prime3
	 *            prime 3
	 */
	public NoiseSet(int prime1, int prime2, int prime3) {
		this.prime1 = prime1;
		this.prime2 = prime2;
		this.prime3 = prime3;
	}

	/**
	 * Get noise value for Position
	 * 
	 * @param x
	 *            X Pos
	 * @param y
	 *            Y pos
	 * @return Value
	 */
	public float getInterpolated(float x, float y) {
		int intx = (int) x;
		int inty = (int) y;

		float v1 = getSmoothNoise(intx, inty);
		float v2 = getSmoothNoise(intx, inty + 1);
		float v3 = getSmoothNoise(intx + 1, inty);
		float v4 = getSmoothNoise(intx + 1, inty + 1);

		float xfract = x - intx;
		float yfract = y - inty;

		return interpolate(interpolate(v1, v2, yfract),
				interpolate(v3, v4, yfract), xfract);
	}

	private static float interpolate(float start, float end, float fractional) {
		// return start * (1-fractional) + end * (fractional);
		float f = (1 - (float) Math.cos(fractional * Math.PI)) * .5f;

		return start * (1 - f) + end * f;
	}

	private float getSmoothNoise(int x, int y) {
		// smooth corners:
		float cornerSum = getNoise(x - 1, y - 1) + getNoise(x + 1, y - 1) + getNoise(x - 1, y + 1) + getNoise(x + 1, y + 1);
		float edgeSum = getNoise(x, y - 1) + getNoise(x + 1, y) + getNoise(x, y + 1) + getNoise(x - 1, y);
		return getNoise(x, y) * .25f + edgeSum / 4 * .5f + cornerSum / 4 * .25f;
	}

	private float getNoise(int x, int y) {
		return makeRandom(x + y * 2207963);
	}

	private float makeRandom(int x) {
		return 1.0f - ((x * (x * x * prime1 + prime2) + prime3) & 0x7fffffff) / 1073741824.0f;

	}
}