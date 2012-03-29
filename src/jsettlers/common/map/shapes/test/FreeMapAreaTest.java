package jsettlers.common.map.shapes.test;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class FreeMapAreaTest {

	@Test
	public void test() {
		List<ShortPoint2D> positions = new LinkedList<ShortPoint2D>();
		positions.add(new ShortPoint2D(1, 1));
		positions.add(new ShortPoint2D(2, 2));
		positions.add(new ShortPoint2D(3, 3));
		positions.add(new ShortPoint2D(2, 1));
		positions.add(new ShortPoint2D(1, 2));
		positions.add(new ShortPoint2D(1, 3));
		positions.add(new ShortPoint2D(3, 1));

		FreeMapArea area = new FreeMapArea(positions);

		boolean[][] expected = { { false, false, false, false, false }, { false, true, true, true, false }, { false, true, true, false, false },
				{ false, true, false, true, false }, { false, false, false, false, false } };

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				assertEquals(expected[y][x], area.contains(new ShortPoint2D(x, y)));
			}
		}
	}
}
