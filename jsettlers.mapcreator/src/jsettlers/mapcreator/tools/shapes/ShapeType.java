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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * This is a shape a tool can work with.
 * 
 * @author michael
 */
public abstract class ShapeType {

	/**
	 * The translated name of the shape
	 */
	protected String name;

	/**
	 * Properties of this Shape
	 */
	protected final Hashtable<EShapeProperty, ShapeProperty> properties = new Hashtable<>();

	/**
	 * Constructor
	 * 
	 * @param translationKey
	 *            Key for the translated name
	 */
	public ShapeType(String translationKey) {
		this.name = EditorLabels.getLabel("shape." + translationKey);
	}

	/**
	 * Sets the affected status of the tiles. Assumes that the given array is initialized with 0s, and that 0 means no influence, 127 means full
	 * influence.
	 */
	public abstract void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end);

	/**
	 * Gets the size of the shape (as optimisation)
	 * 
	 * @return The (average) size in map units.
	 */
	public abstract int getSize();

	/**
	 * @return The translated name of the shape
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value of a property
	 * 
	 * @param property
	 *            Property
	 * @return Value
	 */
	public int getProperty(EShapeProperty property) {
		ShapeProperty prop = this.properties.get(property);
		if (prop == null) {
			return 0;
		}
		return prop.getValue();
	}

	/**
	 * @return The map with all properties, use as read only!
	 */
	public Hashtable<EShapeProperty, ShapeProperty> getProperties() {
		return properties;
	}
}
