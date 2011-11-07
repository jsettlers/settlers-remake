package jsettlers.common.position;

import java.io.Serializable;

public class ShortPoint2D implements ISPosition2D, Serializable {
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

	@Override
	public short getX() {
		return x;
	}

	@Override
	public short getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ISPosition2D)) {
			return false;
		}

		return equals((ISPosition2D) o);
	}

	@Override
	public boolean equals(ISPosition2D other) {
		return other.getX() == x && other.getY() == y;
	}

	@Override
	public int hashCode() {
		return hashCode(x, y);
	}

	/**
	 * Computes the hashcode the way ISPosition2D wants it.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int hashCode(int x, int y) {
		return x * 15494071 + y * 12553;
	}
}
