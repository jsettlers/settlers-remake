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
package jsettlers.mapcreator.tools.shapes;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.noise.NoiseGenerator;

/**
 * Shape to draw some random shapes
 * 
 * @author Andreas Butti
 */
public class NoisyLineCircleShape extends FuzzyLineCircleShape {

	private final int noiseSize = 100;

	private final NoiseGenerator noise = new NoiseGenerator();

	/**
	 * Constructor
	 */
	public NoisyLineCircleShape() {
		this.name = EditorLabels.getLabel("shape.noisy_circle_line");
	}

	@Override
	protected byte getFieldRating(int x, int y, double distance) {
		if (distance > (double) getRadius() * (Byte.MAX_VALUE + noiseSize) / Byte.MAX_VALUE) {
			return 0;
		} else {
			double sloped = getSlopedRating(distance);
			double add = noise.getNoise(x, y) * noiseSize;
			return toByte((int) (sloped + add));
		}
	}

}
