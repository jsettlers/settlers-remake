package jsettlers.common.utils;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class contains static util methods needed for some calculations.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MathUtils {
	private MathUtils() {
	}

	/**
	 * Calculates dx*dx+dy*dy (the square of Math.hypot() )
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static int squareHypot(int dx, int dy) {
		return dx * dx + dy * dy;
	}

	/**
	 * Calculates the square of Math.hypot()
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static int squareHypot(ShortPoint2D pos1, ShortPoint2D pos2) {
		return squareHypot(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY());
	}
}
