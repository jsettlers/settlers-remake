package jsettlers.algorithms.path.astar;

import static org.junit.Assert.assertEquals;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculatable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.AbstractAStar;
import jsettlers.logic.algorithms.path.astar.BucketQueueAStar;
import jsettlers.logic.algorithms.path.test.DummyEmptyAStarMap;

import org.junit.Test;

public class BucketQueueAStarTest {

	private static final short WIDTH = 200;
	private static final short HEIGHT = 200;

	private final AbstractAStar aStar = new BucketQueueAStar(new DummyEmptyAStarMap(WIDTH, HEIGHT), WIDTH, HEIGHT);

	@Test
	public void testPathLengthSingle() {
		short sx = 50;
		short sy = 50;
		short tx = 52;
		short ty = 50;

		Path path = findPath(sx, sy, tx, ty);

		assertEquals(ShortPoint2D.getOnGridDist(tx - sx, ty - sy) - 1, path.getLength());
	}

	@Test
	public void testPathLengthMultiple() {
		for (short sx = 50; sx < 70; sx++) {
			for (short sy = 50; sy < 70; sy++) {
				for (short tx = 50; tx < 70; tx++) {
					for (short ty = 50; ty < 70; ty++) {
						if (sx == tx && sy == ty) {
							continue;
						}

						assertEquals(ShortPoint2D.getOnGridDist(tx - sx, ty - sy) - 1, findPath(sx, sy, tx, ty).getLength());
					}
				}
			}
		}
	}

	private Path findPath(short sx, short sy, short tx, short ty) {
		return aStar.findPath(getPathable(sx, sy), new ShortPoint2D(tx, ty));
	}

	private IPathCalculatable getPathable(final short x, final short y) {
		return new IPathCalculatable() {
			@Override
			public ShortPoint2D getPos() {
				return new ShortPoint2D(x, y);
			}

			@Override
			public byte getPlayerId() {
				return 0;
			}

			@Override
			public boolean needsPlayersGround() {
				return false;
			}
		};
	}
}
