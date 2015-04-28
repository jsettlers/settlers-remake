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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a shape a tool can work with.
 * 
 * @author michael
 */
public abstract class ShapeType {
	private LinkedList<ShapeProperty> properties = new LinkedList<ShapeProperty>();
	private Hashtable<ShapeProperty, Integer> values = new Hashtable<ShapeProperty, Integer>();

	/**
	 * Sets the affected status of the tiles. Assumes that the given array is initialized with 0s, and that 0 means no influence, 127 means full
	 * influence.
	 */
	abstract public void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end);

	/**
	 * Gets the size of the shape (as optimisation)
	 * 
	 * @return The (average) size in map units.
	 */
	abstract public int getSize();

	abstract public String getName();

	protected void addProperty(ShapeProperty property) {
		properties.add(property);
		values.put(property, (property.getMin() + property.getMax()) / 2);
	}

	public void setProperty(ShapeProperty property, int value) {
		if (value < property.getMin() && value < property.getMax()) {
			throw new IllegalArgumentException();
		}
		values.put(property, value);
	}

	public int getProperty(ShapeProperty property) {
		return values.get(property);
	}

	public List<ShapeProperty> getProperties() {
		return properties;
	}
}
