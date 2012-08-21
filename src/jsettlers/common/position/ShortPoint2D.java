package jsettlers.common.position;

import java.io.Serializable;

public class ShortPoint2D implements Serializable {
	private static final long serialVersionUID = -6227987796843655750L;

	protected final short x;
	protected final short y;

	public ShortPoint2D(short x, short y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * NOTE: the values of the parameters will be casted to (short). This constructor is just to save typing!
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public ShortPoint2D(int x, int y) {
		this((short) x, (short) y);
	}

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ShortPoint2D)) {
			return false;
		}

		return equals((ShortPoint2D) o);
	}

	public boolean equals(ShortPoint2D other) {
		return other != null && other.getX() == x && other.getY() == y;
	}

	@Override
	public int hashCode() {
		return hashCode(x, y);
	}

	/**
	 * Computes the hashcode the way ShortPoint2D wants it.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return Hashcode of the given position.
	 */
	public static int hashCode(int x, int y) {
		return x * 15494071 + y * 12553;
	}

	/**
	 * Gets the number of tiles a movable must at least walk to get from this to the other position.
	 * 
	 * @param otherPos
	 *            The other position.
	 * 
	 * @return The distance a movable needs to walk to get from this to the other position.
	 */
	public int getDistTo(ShortPoint2D otherPos) {
		final int dx = Math.abs(x - otherPos.x);
		final int dy = Math.abs(y - otherPos.y);

		return dx + dy;
	}
}
