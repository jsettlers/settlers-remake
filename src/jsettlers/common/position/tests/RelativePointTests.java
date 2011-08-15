package jsettlers.common.position.tests;

import static org.junit.Assert.assertEquals;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

public class RelativePointTests {

	@Test
	public void testCalculatePoint() {
		ShortPoint2D p1 = new ShortPoint2D((short) 1, (short) 2);
		ShortPoint2D p2 = new ShortPoint2D((short) 5, (short) 6);

		RelativePoint expected = new RelativePoint((short) 4, (short) 4);
		assertEquals(expected, RelativePoint.getRelativePoint(p1, p2));

		assertEquals(p2, expected.calculatePoint(p1));
	}

}
