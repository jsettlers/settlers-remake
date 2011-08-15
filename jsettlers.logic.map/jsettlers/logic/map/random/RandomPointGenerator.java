package jsettlers.logic.map.random;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * This class provides methods to generate random points.
 * @author michael
 *
 */
public class RandomPointGenerator {
	/**
	 * Creates some random points in a 2D area.
	 * @param width The width of the field for the points.
	 * @param height THe height of the field
	 * @param points The numbe rof points.
	 * @param random A random generator to use
	 * @return The generated points.
	 */
	public static Point2D[] getRadnomPoints(int width, int height, int points, Random random) {
		Point2D[] result = new Point2D[points];
		for (int i = 0; i < points; i++) {
			float x = width * random.nextFloat();
			float y = height * random.nextFloat();
			result[i] = new Point2D.Float(x, y);
		}
		return result;
	}
}
