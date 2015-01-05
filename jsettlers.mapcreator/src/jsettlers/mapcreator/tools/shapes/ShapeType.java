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
