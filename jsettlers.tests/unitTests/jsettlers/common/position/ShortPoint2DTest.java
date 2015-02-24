package jsettlers.common.position;

import static org.junit.Assert.assertEquals;
import jsettlers.common.map.shapes.HexBorderArea;

import org.junit.Test;

public class ShortPoint2DTest {

	@Test
	public void testGetOnGridDist() {
		ShortPoint2D center = new ShortPoint2D(100, 100);

		for (short i = 1; i < 30; i++) {
			HexBorderArea border = new HexBorderArea(center, i);

			for (ShortPoint2D pos : border) {
				assertEquals(i, pos.getOnGridDistTo(center));
				assertEquals(pos.getOnGridDistTo(center), i);
			}
		}
	}

	@Test
	public void singleGetOnGridDistTest() {
		ShortPoint2D center = new ShortPoint2D(100, 100);
		ShortPoint2D pos = new ShortPoint2D(98, 99);

		assertEquals(2, center.getOnGridDistTo(pos));
		assertEquals(2, pos.getOnGridDistTo(center));
	}
}
