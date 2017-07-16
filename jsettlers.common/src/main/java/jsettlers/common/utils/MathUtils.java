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
package jsettlers.common.utils;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class contains static util methods needed for some calculations.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MathUtils {
	private MathUtils() {
	}

	/**
	 * Calculates sqrt(dx*dx+dy*dy) (Math.hypot() )
	 *
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static double hypot(float dx, float dy) {
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Calculates sqrt(dx*dx+dy*dy) (Math.hypot() )
	 *
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static double hypot(int dx, int dy) {
		return Math.sqrt(squareHypot(dx, dy));
	}

	/**
	 * Calculates dx*dx+dy*dy (the square of Math.hypot() )
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static int squareHypot(int dx, int dy) {
		return dx * dx + dy * dy;
	}

	/**
	 * Calculates the square of Math.hypot()
	 * 
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public static int squareHypot(ShortPoint2D pos1, ShortPoint2D pos2) {
		return squareHypot(pos1.x - pos2.x, pos1.y - pos2.y);
	}
}
