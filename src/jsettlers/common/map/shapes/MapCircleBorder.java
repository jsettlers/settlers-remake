package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

public class MapCircleBorder implements IMapArea {
	private static final long serialVersionUID = 7850239131055274940L;

	private final MapCircle baseCircle;

	public MapCircleBorder(MapCircle baseCircle) {
		this.baseCircle = baseCircle;
	}

	/**
	 * Tests whether a point is on the border of the circle.
	 * <p>
	 * This is exactly the case if the point is contained in the base circle and it is not contained int he volume.
	 * 
	 * @see IMapArea#contains(ShortPoint2D)
	 * @see MapCircleBorder#isInVolume(ShortPoint2D)
	 */
	@Override
	public boolean contains(ShortPoint2D position) {
		return baseCircle.contains(position) && !isInVolume(position);
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new MapCircleBorderIterator(this);
	}

	public MapCircle getBaseCircle() {
		return baseCircle;
	}

	/**
	 * Calculates whether a point is in the volume of the base circle and therefore not on the border.
	 * 
	 * @param point
	 *            The point to test
	 * @return true if and only if the point is in the volume.
	 */
	public boolean isInVolume(ShortPoint2D point) {
		if (point == null) {
			return false;
		}
		short line = point.y;
		float prevLineWidth = baseCircle.getHalfLineWidth(line - baseCircle.getCenterY() - 1);
		float nextLineWidth = baseCircle.getHalfLineWidth(line - baseCircle.getCenterY() + 1);
		float xDistToCenter = Math.abs(-point.x - .5f * (baseCircle.getCenterY() - line) + baseCircle.getCenterX());
		return xDistToCenter < prevLineWidth && xDistToCenter < nextLineWidth;
	}

}
