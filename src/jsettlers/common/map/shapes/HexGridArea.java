package jsettlers.common.map.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

/**
 * Represents a hexagon on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public final class HexGridArea implements IMapArea {
	private static final long serialVersionUID = -2218632675269689379L;
	final short cX;
	final short cY;
	final short startRadius;
	final short maxRadius;

	/**
	 * hexagon from including {@link #startRadius} to including {@link #maxRadius}
	 * 
	 * @param cX
	 *            center x
	 * @param cY
	 *            center y
	 * @param startRadius
	 *            inclusive inner radius
	 * @param maxRadius
	 *            inclusive outer radius
	 */
	public HexGridArea(short cX, short cY, short startRadius, short maxRadius) {
		this.cX = cX;
		this.cY = cY;
		this.startRadius = startRadius;
		this.maxRadius = maxRadius;
	}

	@Override
	public boolean contains(ISPosition2D position) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public ICoordinateIterator iterator() {
		return new HexGridAreaIterator(this);
	}

	private static final class HexGridAreaIterator implements ICoordinateIterator {
		private static final byte[] directionIncreaseX = { -1, 0, 1, 1, 0, -1 };
		private static final byte[] directionIncreaseY = { 0, 1, 1, 0, -1, -1 };

		private static final long serialVersionUID = -8760653162789299782L;
		private final HexGridArea hexGridArea;
		private short radius;
		private short x;
		private short y;
		private byte direction = 0;
		private short length = 0;

		public HexGridAreaIterator(HexGridArea hexGridArea) {
			this.hexGridArea = hexGridArea;
			radius = hexGridArea.startRadius;
			calcNewXY();
		}

		private final void calcNewXY() {
			x = hexGridArea.cX;
			y = (short) (hexGridArea.cY - radius);
		}

		@Override
		public boolean hasNext() {
			if (length >= radius) {
				direction++;
				if (direction >= EDirection.NUMBER_OF_DIRECTIONS) {
					radius++;
					if (radius >= hexGridArea.maxRadius) {
						return false;
					}

					direction = 0;
					calcNewXY();
				}
				length = 0;
			}

			x += directionIncreaseX[direction];
			y += directionIncreaseY[direction];
			length++;
			return true;
		}

		@Override
		public ISPosition2D next() {
			return new ShortPoint2D(x, y);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("not implemented!");
		}

		@Override
		public short getNextX() {
			return x;
		}

		@Override
		public short getNextY() {
			return y;
		}
	}

}
