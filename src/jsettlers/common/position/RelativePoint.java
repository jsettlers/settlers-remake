package jsettlers.common.position;

import java.io.Serializable;

/**
 * class to specify a relative position on the grid
 * 
 * @author Andreas Eberle
 * 
 */
public class RelativePoint implements Serializable {
	private static final long serialVersionUID = 7216627427441018520L;
	private final short dy;
	private final short dx;

	public RelativePoint(short dx, short dy) {
		this.dy = dy;
		this.dx = dx;
	}

	/**
	 * constructor for easy use (ints instead of shorts)<br>
	 * NOTE: all arguments will be cast to short !!
	 * 
	 * @param dx
	 * @param dy
	 */
	public RelativePoint(int dx, int dy) {
		this((short) dx, (short) dy);
	}

	/**
	 * calculates the point on the grid from the given start point.
	 * 
	 * @param start
	 * @return
	 */
	public ShortPoint2D calculatePoint(ISPosition2D start) {
		return new ShortPoint2D((short) (start.getX() + dx), (short) (start.getY() + dy));
	}

	public short calculateX(short x) {
		return (short) (x + dx);
	}

	public short calculateY(short y) {
		return (short) (y + dy);
	}

	public static RelativePoint getRelativePoint(ISPosition2D start, ISPosition2D end) {
		short dx = (short) (end.getX() - start.getX());
		short dy = (short) (end.getY() - start.getY());

		return new RelativePoint(dx, dy);
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && (o instanceof RelativePoint)) {
			RelativePoint other = (RelativePoint) o;
			return other.getDy() == this.getDy() && other.getDx() == this.getDx();
		} else {
			return false;
		}
	}

	public int getHashCode() {
		return getDy() << 16 + getDx();
	}

	@Override
	public String toString() {
		return "dx=" + getDx() + ", dy=" + getDy();
	}

	public short getDy() {
		return dy;
	}

	public short getDx() {
		return dx;
	}

}