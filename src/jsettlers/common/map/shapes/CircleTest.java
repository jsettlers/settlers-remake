/**
 * 
 */
package jsettlers.common.map.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleBorder;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.Parallelogram;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

/**
 * @author michael
 */
public class CircleTest {

	private static final int TEST_WIDTH = 100;

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapCircle#contains(jsettlers.common.position.ShortPoint2D)} .
	 */
	@Test
	public void testContainsShortPoint2D() {
		short cx = 0;
		short cy = 0;
		MapCircle circle = new MapCircle(cx, cy, 1);
		ShortPoint2D[] truePoints = new ShortPoint2D[] { new ShortPoint2D(cx - 1, cy - 1), new ShortPoint2D(cx - 1, cy),
				new ShortPoint2D(cx, cy - 1), new ShortPoint2D(cx, cy), new ShortPoint2D(cx + 1, cy), new ShortPoint2D(cx, cy + 1),
				new ShortPoint2D(cx + 1, cy + 1) };

		for (ShortPoint2D point : truePoints) {
			assertTrue("Point should be inside", circle.contains(point));
		}

		ShortPoint2D[] falsePoints = new ShortPoint2D[] { new ShortPoint2D(cx - 2, cy - 1), new ShortPoint2D(cx, cy + 3),
				new ShortPoint2D(cx - 1, cy + 1), };

		for (ShortPoint2D point : falsePoints) {
			assertFalse("Point should not be inside: " + point, circle.contains(point));
		}
	}

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapCircle#iterator()}.
	 */
	@Test
	public void testIterator() {
		MapCircle circle = new MapCircle((short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2), 31.123f);
		testShapeIterator(circle);
	}

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapRectangle#iterator()}.
	 */
	@Test
	public void testRectIterator() {
		MapRectangle rect = new MapRectangle((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2));
		testShapeIterator(rect);
		MapRectangle rect2 = new MapRectangle((short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect2);
		MapRectangle rect3 = new MapRectangle((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect3);

	}

	@Test
	public void testCircleBorder() {
		MapCircle baseCircle = new MapCircle((short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2), TEST_WIDTH / 4);
		testShapeIterator(new MapCircleBorder(baseCircle));
	}

	@Test
	public void testParallelogramIterator() {
		Parallelogram rect = new Parallelogram((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2));
		testShapeIterator(rect);
		Parallelogram rect2 = new Parallelogram((short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect2);
		Parallelogram rect3 = new Parallelogram((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect3);
	}

	private void testShapeIterator(IMapArea circle) {
		boolean[][] foundByIterator = new boolean[TEST_WIDTH][TEST_WIDTH];

		for (ShortPoint2D pos : circle) {
			foundByIterator[pos.getX()][pos.getY()] = true;
		}

		for (int x = 0; x < TEST_WIDTH; x++) {
			for (int y = 0; y < TEST_WIDTH; y++) {
				assertEquals("contains() incosistent with iterator for " + x + "," + y, circle.contains(new ShortPoint2D(x, y)),
						foundByIterator[x][y]);
			}
		}
	}

}
