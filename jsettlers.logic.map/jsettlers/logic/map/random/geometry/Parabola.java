package jsettlers.logic.map.random.geometry;

public final class Parabola {

	private static final double EPSILON = .00001;

	private Parabola() {
	}

	public static boolean equalsWithEpsilon(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	/**
	 * Gets the y coordintate of the cut of two parabolas with center1 and
	 * center2 and a common y-directrix that lies between them, assuming
	 * center1y < center2y;
	 * 
	 * @param center1x
	 * @param center1y
	 * @param center2x
	 * @param center2y
	 * @param directrixx
	 * @return
	 */
	public static double getCutY(double center1x, double center1y,
	        double center2x, double center2y, double directrixx) {
		if (equalsWithEpsilon(center1x, center2x)
		        || equalsWithEpsilon(center1y, center2y)) {
			return (center1y + center2y) / 2;
		} else {
			double sqrt =
			        Math.sqrt((center1y - center2y)
			                * (center1y - center2y)
			                * (center1x * center1x + center1y * center1y - 2
			                        * center1x * center2x + center2x * center2x
			                        - 2 * center1y * center2y + center2y
			                        * center2y)
			                * (center1x * center2x - center1x * directrixx
			                        - center2x * directrixx + directrixx
			                        * directrixx));
			double a =
			        -(center1y * center1y * center2x) + center1x * center1y
			                * center2y + center1y * center2x * center2y
			                - center1x * center2y * center2y + center1y
			                * center1y * directrixx - 2 * center1y * center2y
			                * directrixx + center2y * center2y * directrixx;
			double b = (center1x - center2x) * (center1y - center2y);
			if (center1y < (a - sqrt) / b && (a - sqrt) / b < center2y) {
				return (a - sqrt) / b;
			} else {
				return (a + sqrt) / b;
			}
		}
	}
}
