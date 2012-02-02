package jsettlers.common.movable;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

/**
 * Enumeration for directions that can be gone on the grid.
 * 
 * @author Andreas Eberle
 */
public enum EDirection {
	NORTH_EAST(0, -1),
	EAST(1, 0),
	SOUTH_EAST(1, 1),
	SOUTH_WEST(0, 1),
	WEST(-1, 0),
	NORTH_WEST(-1, -1);

	private static final double TAN_67_5 = Math.tan(Math.toRadians(45 + 22.5));
	private static final double TAN_22_5 = Math.tan(Math.toRadians(22.5));

	public static final EDirection[] values = EDirection.values();
	public static final byte NUMBER_OF_DIRECTIONS = (byte) values.length;

	private final byte gridDeltaX;
	private final byte gridDeltaY;

	EDirection(int gridDx, int gridDy) {
		this.gridDeltaX = (byte) gridDx;
		this.gridDeltaY = (byte) gridDy;
	}

	/**
	 * @return delta x you have to go on the grid to go into this direction
	 */
	public byte getGridDeltaX() {
		return gridDeltaX;
	}

	/**
	 * @return delta y you have to go on the grid to go into this direction
	 */
	public byte getGridDeltaY() {
		return gridDeltaY;
	}

	/**
	 * LIMITATION: the given ISPosition objects need to be direct neighbors and can not be the same object!! <br>
	 * calculates the direction between the two given ISPosition2D objects.
	 * 
	 * @param first
	 *            one ISPosition2D object
	 * @param second
	 *            another ISPisition2D object
	 * @return EDirection if the direction exists<br>
	 *         null if it does not exist
	 */
	public static EDirection getDirection(ISPosition2D first, ISPosition2D second) {
		byte dx = (byte) (second.getX() - first.getX());
		byte dy = (byte) (second.getY() - first.getY());

		return getDirection(dx, dy);
	}

	/**
	 * Returns the direction thats best to be gone to get from first to second.
	 * <p>
	 * If the points are equal, the result is undefined but not null.
	 * 
	 * @param first
	 *            first position
	 * @param second
	 *            second position
	 * @return
	 */
	public static EDirection getApproxDirection(ISPosition2D first, ISPosition2D second) {
		byte dx = (byte) (second.getX() - first.getX());
		byte dy = (byte) (second.getY() - first.getY());

		float incline = (float) dy / dx;

		if (dx == 0) {
			if (dy < 0) {
				return EDirection.NORTH_EAST;
			} else {
				return EDirection.SOUTH_WEST;
			}
		} else if (dx > 0) {
			if (incline < -1) {
				return EDirection.NORTH_EAST;
			} else if (incline < TAN_22_5) {
				return EDirection.EAST;
			} else if (incline < TAN_67_5) {
				return EDirection.SOUTH_EAST;
			} else {
				return EDirection.SOUTH_WEST;
			}
		} else {
			if (incline < -1) {
				return EDirection.SOUTH_WEST;
			} else if (incline < TAN_22_5) {
				return EDirection.WEST;
			} else if (incline < TAN_67_5) {
				return EDirection.NORTH_WEST;
			} else {
				return EDirection.NORTH_EAST;
			}
		}

	}

	public static EDirection getDirection(byte dx, byte dy) {
		for (EDirection currDir : values) {
			if (currDir.gridDeltaX == dx && currDir.gridDeltaY == dy) {
				return currDir;
			}
		}

		return null;
	}

	public short getNextTileX(short x) {
		return (short) (x + gridDeltaX);
	}

	public short getNextTileY(short y) {
		return (short) (y + gridDeltaY);
	}

	/**
	 * returns the coordinates of the next tile if you go in this direction from the given coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public ISPosition2D getNextHexPoint(short x, short y) {
		return new ShortPoint2D(getNextTileX(x), getNextTileY(y));
	}

	/**
	 * returns the coordinates of the next tile if you go in this direction from the given location
	 * 
	 * @param pos
	 * @return
	 */
	public ISPosition2D getNextHexPoint(ISPosition2D pos) {
		return getNextHexPoint(pos.getX(), pos.getY());
	}

	public ISPosition2D getNextTilePoint(ISPosition2D pos, int steps) {
		return new ShortPoint2D(pos.getX() + gridDeltaX * steps, pos.getY() + gridDeltaY * steps);
	}

	/**
	 * @param direction
	 *            direction can be in [-{@link #NUMBER_OF_DIRECTIONS}, {@link #NUMBER_OF_DIRECTIONS}]<br>
	 *            it specifies in what direction and how far away the neighbor should be taken
	 * @return
	 */
	public EDirection getNeighbor(int direction) {
		return values[(ordinal() + NUMBER_OF_DIRECTIONS - direction) % EDirection.NUMBER_OF_DIRECTIONS];
	}

	public EDirection getInverseDirection() {
		return values()[(this.ordinal() + NUMBER_OF_DIRECTIONS / 2) % NUMBER_OF_DIRECTIONS];
	}

	public static byte[] getXDeltaArray() {
		byte[] result = new byte[NUMBER_OF_DIRECTIONS];
		for (int i = 0; i < NUMBER_OF_DIRECTIONS; i++) {
			result[i] = EDirection.values[i].gridDeltaX;
		}
		return result;
	}

	public static byte[] getYDeltaArray() {
		byte[] result = new byte[NUMBER_OF_DIRECTIONS];
		for (int i = 0; i < NUMBER_OF_DIRECTIONS; i++) {
			result[i] = EDirection.values[i].gridDeltaY;
		}
		return result;
	}

}
