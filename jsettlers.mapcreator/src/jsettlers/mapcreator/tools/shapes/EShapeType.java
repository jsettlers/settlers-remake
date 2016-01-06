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
	 * TODO !!!!!!!!
	 */
	LINE_CIRCLE(new LineCircleShape(), ShapeIcon.LINE_CIRCLE),

	/**
	 * TODO !!!!!!!!
	 */
	GRID_CIRCLE(new GridCircleShape(), ShapeIcon.GRID_CIRCLE),

	/**
	 * TODO !!!!!!!!
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
	private EShapeType(ShapeType shape, ShapeIcon icon) {
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
