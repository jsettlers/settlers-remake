package jsettlers.mapcreator.tools.shapes;

/**
 * This is a property definition a shape can have.
 * @author michael
 *
 */
public class ShapeProperty {

	private final String name;
	private final int min;
	private final int max;

	public ShapeProperty(String name, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.min = min;
		this.max = max;
    }

	public String getName() {
	    return name;
    }
	
	public int getMax() {
	    return max;
    }
	
	public int getMin() {
	    return min;
    }

}
