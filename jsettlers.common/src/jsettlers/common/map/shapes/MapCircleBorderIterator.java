package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

public class MapCircleBorderIterator extends MapCircleIterator implements
		Iterator<ShortPoint2D> {

	private final MapCircleBorder circle;

	public MapCircleBorderIterator(MapCircleBorder circle) {
		super(circle.getBaseCircle());
		this.circle = circle;
	}

	@Override
	public ShortPoint2D next() {
		/**
		 * Skip inner parts. Assume not to skip the last point of the row, so we need no additional checking.
		 */

		ShortPoint2D next;
		do {
			next = super.next();
		} while (circle.isInVolume(next));
		return next;
	}
}
