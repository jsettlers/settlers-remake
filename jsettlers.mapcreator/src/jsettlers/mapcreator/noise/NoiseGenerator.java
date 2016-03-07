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
 * Used to draw some random shapes
 * 
 * @author Andreas Butti
 */
public class NoiseGenerator {
	private static final float PERSISTENCE = .5f;

	private static final float FREQ_INCREASE = 2;
	private final NoiseSet[] SETS = new NoiseSet[] {
			new NoiseSet(15731, 789221, 1376312589),
			new NoiseSet(15731, 789221, 2350883)
	};

	/**
	 * Get noise value for Position
	 * 
	 * @param x
	 *            X Pos
	 * @param y
	 *            Y Pos
	 * @return Noise value
	 */
	public float getNoise(int x, int y) {
		return getRealNoise(x * .3f, y * .3f);
	}

	private float getRealNoise(float x, float y) {
		float sum = 0;

		float amplitude = 1;
		float frequency = 1;
		for (int i = 0; i < SETS.length; i++) {
			sum += SETS[i].getInterpolated(x * frequency, y * frequency) * amplitude;

			frequency *= FREQ_INCREASE;
			amplitude *= PERSISTENCE;
		}

		return sum;
	}
}
