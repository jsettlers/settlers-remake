package jsettlers.logic.map.random;

import java.util.Random;

import jsettlers.logic.map.random.geometry.Point2D;

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
			result[i] = new Point2D(x, y);
		}
		return result;
	}
}
