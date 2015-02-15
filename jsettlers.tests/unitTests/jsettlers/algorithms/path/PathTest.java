package jsettlers.algorithms.path;

import static org.junit.Assert.assertEquals;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.Path;

import org.junit.Test;

public class PathTest {

	@Test
	public void testPathExtension() {
		Path p = new Path(5);
		for (int i = 0; i < 5; i++) {
			p.insertAt(i, (short) (4 + i), (short) 1);
		}
		p.initPath();
		p.goToNextStep();

		Path extended = new Path(p, new ShortPoint2D(3, 1), new ShortPoint2D(4, 1));

		for (int i = 0; i < 6; i++) {
			assertEquals(i + 3, extended.nextX());
			assertEquals(1, extended.nextY());
			extended.goToNextStep();
		}
	}
}
