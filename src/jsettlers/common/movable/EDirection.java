package jsettlers.common.movable;

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

	public final byte gridDeltaX;
	public final byte gridDeltaY;

	public final boolean isHorizontal;
	public final byte ordinal;

	EDirection(int gridDx, int gridDy) {
		this.gridDeltaX = (byte) gridDx;
		this.gridDeltaY = (byte) gridDy;

		this.isHorizontal = (gridDy == 0);

		this.ordinal = (byte) super.ordinal();
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
	 * calculates the direction between the two given ShortPoint2D objects.
	 * 
	 * @param first
	 *            one ShortPoint2D object
	 * @param second
	 *            another ISPisition2D object
	 * @return EDirection if the direction exists<br>
	 *         null if it does not exist
	 */
	public static final EDirection getDirection(ShortPoint2D first, ShortPoint2D second) {
		return getDirection(first.x, first.y, second.x, second.y);
	}

	/**
	 * LIMITATION: the given ISPosition objects need to be direct neighbors and can not be the same object!! <br>
	 * calculates the direction between the two given ShortPoint2D objects.
	 * 
	 * @param first
	 *            one ShortPoint2D object
	 * @param second
	 *            another ISPisition2D object
	 * @return EDirection if the direction exists<br>
	 *         null if it does not exist
	 */
	public static final EDirection getDirection(short sx, short sy, short tx, short ty) {
		byte dx = (byte) (tx - sx);
		byte dy = (byte) (ty - sy);

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
	public static EDirection getApproxDirection(ShortPoint2D first, ShortPoint2D second) {
		return getApproxDirection(first.x, first.y, second.x, second.y);
	}

	/**
	 * Returns the direction thats best to be gone to get from first to second.
	 * <p>
	 * If the points are equal, the result is undefined but not null.
	 * 
	 * @param fx
	 *            x coordinate of first position
	 * @param fy
	 *            y coordinate of first position
	 * @param sx
	 *            x coordinate of second position
	 * @param sy
	 *            y coordinate of second position
	 * 
	 * @return
	 */
	public static EDirection getApproxDirection(int fx, int fy, int sx, int sy) {
		int dx = sx - fx;
		int dy = sy - fy;

		float incline = ((float) dy) / dx;

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

	public final static EDirection getDirectionOfMultipleSteps(int dx, int dy) {
		int steps;
		if (dx != 0) {
			steps = Math.abs(dx);
		} else {
			steps = Math.abs(dy);
		}

		return getDirection(dx / steps, dy / steps);
	}

	public final static EDirection getDirection(int dx, int dy) {
		for (EDirection currDir : values) {
			if (currDir.gridDeltaX == dx && currDir.gridDeltaY == dy) {
				return currDir;
			}
		}

		return null;
	}

	public final short getNextTileX(short x) {
		return (short) (x + gridDeltaX);
	}

	public final short getNextTileY(short y) {
		return (short) (y + gridDeltaY);
	}

	/**
	 * returns the coordinates of the next tile if you go in this direction from the given coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public ShortPoint2D getNextHexPoint(short x, short y) {
		return new ShortPoint2D(getNextTileX(x), getNextTileY(y));
	}

	/**
	 * returns the coordinates of the next tile if you go in this direction from the given location
	 * 
	 * @param pos
	 * @return
	 */
	public ShortPoint2D getNextHexPoint(ShortPoint2D pos) {
		return getNextHexPoint(pos.x, pos.y);
	}

	public ShortPoint2D getNextHexPoint(ShortPoint2D pos, int steps) {
		return new ShortPoint2D(pos.x + gridDeltaX * steps, pos.y + gridDeltaY * steps);
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

	public final boolean isHorizontal() {
		return isHorizontal;
	}

}
