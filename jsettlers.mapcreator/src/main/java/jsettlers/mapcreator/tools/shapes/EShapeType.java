/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
 * Supported shape types
 * 
 * @author Andreas Butti
 *
 */
public enum EShapeType {

	/**
	 * Draw a single point
	 */
	POINT(new PointShape(), ShapeIcon.POINT),

	/**
	 * Line without breaks, even if you move the mouse faster
	 */
	LINE(new LineShape(), ShapeIcon.LINE),

	/**
	 * TODO A good description
	 */
	LINE_CIRCLE(new LineCircleShape(), ShapeIcon.LINE_CIRCLE),

	/**
	 * TODO A good description
	 */
	GRID_CIRCLE(new GridCircleShape(), ShapeIcon.GRID_CIRCLE),

	/**
	 * TODO A good description
	 */
	FUZZY_LINE_CIRCLE(new FuzzyLineCircleShape(), ShapeIcon.FUZZY_LINE_CIRCLE),

	/**
	 * Noisy line without breaks, even if you move the mouse faster
	 */
	NOISY_LINE_CIRCLE(new NoisyLineCircleShape(), ShapeIcon.NOISY_LINE_CIRCLE);

	/**
	 * The shape corresponding to this enum value
	 */
	private final ShapeType shape;

	/**
	 * Shape icon
	 */
	private final ShapeIcon icon;

	/**
	 * Constructor
	 * 
	 * @param shape
	 */
	EShapeType(ShapeType shape, ShapeIcon icon) {
		this.shape = shape;
		this.icon = icon;
	}

	/**
	 * @return The shape corresponding to this enum value
	 */
	public ShapeType getShape() {
		return shape;
	}

	/**
	 * @return Shape icon
	 */
	public ShapeIcon getIcon() {
		return icon;
	}

}
