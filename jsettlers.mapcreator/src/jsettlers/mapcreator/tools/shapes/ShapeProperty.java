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

/**
 * This is a property definition a shape can have.
 * 
 * @author michael
 *
 */
public class ShapeProperty {

	/**
	 * Minimum of the property
	 */
	private final int min;

	/**
	 * Maximum of the property
	 */
	private final int max;

	/**
	 * The current value of the Property
	 */
	private int value;

	/**
	 * Constructor
	 * 
	 * @param min
	 *            Minimum of the property
	 * @param max
	 *            Maximum of the property
	 * @param value
	 *            The current value of the Property
	 */
	public ShapeProperty(int min, int max, int value) {
		if (min > max) {
			throw new IllegalArgumentException();
		}
		this.min = min;
		this.max = max;
		this.value = value;
	}

	/**
	 * @param value
	 *            The current value of the Property
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return The current value of the Property
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return Maximum of the property
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @return Minimum of the property
	 */
	public int getMin() {
		return min;
	}

}
