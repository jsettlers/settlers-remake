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

public class FuzzyLineCircleShape extends LineCircleShape {

	private static final ShapeProperty INNER_PROPERTY = new ShapeProperty(EditorLabels.getLabel("line_inner"), 0, 100);

	public FuzzyLineCircleShape() {
		addProperty(INNER_PROPERTY);
	}

	@Override
	protected byte getFieldRating(int x, int y, double distance) {
		if (distance > getRadius()) {
			return 0;
		} else {
			int sloped = (int) getSlopedRating(distance);
			return toByte(sloped);
		}
	}

	protected static byte toByte(int sloped) {
		return sloped < 0 ? 0 : sloped > Byte.MAX_VALUE ? Byte.MAX_VALUE
				: (byte) sloped;
	}

	protected double getSlopedRating(double distance) {
		if (getProperty(INNER_PROPERTY) == INNER_PROPERTY.getMax()) {
			// to hard to compute
			return Byte.MAX_VALUE;
		}
		if (distance > getRadius()) {
			return 0;
		}
		// linear falloff:
		// double m = Byte.MAX_VALUE / ((1 - inner) * getRadius());
		// return (getRadius() - distance) * m;

		// cosine falloff:
		// return (byte) Byte.MAX_VALUE * Math.cos(distance * Math.PI / getRadius() / 2);

		// even better:
		return Byte.MAX_VALUE * (.5 * Math.cos(distance * Math.PI / getRadius()) + .5);
	}

	/*
	 * public void setInner(float inner) { if (inner < 0) { this.inner = 0; } else if (inner > 1) { this.inner = 1; } else { this.inner = inner; } }
	 */

	public float getInner() {
		return getProperty(INNER_PROPERTY) / 100f;
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("fuzzy_line");
	}
}
