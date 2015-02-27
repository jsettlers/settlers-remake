package jsettlers.common.map.shapes;

import static org.junit.Assert.fail;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

public class MapCircleTest {

	@Test
	public void testGetBorders() {
		for (int i = 1; i < 40; i++) {
			MapCircle circle = new MapCircle(new ShortPoint2D(100, 100), i);

			SRectangle borders = circle.getBorders();
			for (ShortPoint2D curr : circle) {
				if (!borders.contains(curr)) {
					fail("position: " + curr + " is not in the border " + borders + " radius: " + i);
				}
			}
		}
	}
}
