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
package jsettlers.logic.map.random;

import java.util.Random;

import jsettlers.logic.map.random.geometry.Point2D;

/**
 * This class provides methods to generate random points.
 * 
 * @author michael
 *
 */
public class RandomPointGenerator {
	/**
	 * Creates some random points in a 2D area.
	 * 
	 * @param width
	 *            The width of the field for the points.
	 * @param height
	 *            THe height of the field
	 * @param points
	 *            The numbe rof points.
	 * @param random
	 *            A random generator to use
	 * @return The generated points.
	 */
	public static Point2D[] getRadnomPoints(int width, int height, int points, Random random) {
		Point2D[] result = new Point2D[points];
		for (int i = 0; i < points; i++) {
			float x = width * random.nextFloat();
			float y = height * random.nextFloat();
			result[i] = new Point2D(x, y);
		}
		return result;
	}
}
