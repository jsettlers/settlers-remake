package jsettlers.common.movable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

/**
 * Unit Tests for {@link EDirection}
 * 
 * @author Andreas Eberle
 * 
 */
public class EDirectionTest {

	@Test
	public void testGetDirection() {
		short startX = 100;
		short startY = 100;

		for (EDirection currDir : EDirection.values) {
			ShortPoint2D target = currDir.getNextHexPoint(new ShortPoint2D(startX, startY));

			EDirection calculatedDir = EDirection.getDirectionOfMultipleSteps(target.getX() - startX, target.getY() - startY);
			assertNotNull(calculatedDir);
			assertEquals(currDir, calculatedDir);
		}
	}

	@Test
	public void testGetDirectionOfMultipleSteps() {
		short startX = 100;
		short startY = 100;

		for (EDirection currDir : EDirection.values) {
			for (int i = 1; i < 30; i++) {
				ShortPoint2D target = currDir.getNextHexPoint(new ShortPoint2D(startX, startY), i);

				EDirection calculatedDir = EDirection.getDirectionOfMultipleSteps(target.getX() - startX, target.getY() - startY);
				assertNotNull(calculatedDir);
				assertEquals(currDir, calculatedDir);
			}
		}
	}
}
