/*******************************************************************************
 * Copyright (c) 2015 - 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms.path.astar;

import org.junit.Test;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ShortPoint2D;

import static org.junit.Assert.assertEquals;

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

		assertEquals(ShortPoint2D.getOnGridDist(tx - sx, ty - sy), path.getLength());
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

						assertEquals(ShortPoint2D.getOnGridDist(tx - sx, ty - sy), findPath(sx, sy, tx, ty).getLength());
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
			public ShortPoint2D getPosition() {
				return new ShortPoint2D(x, y);
			}

			@Override
			public IPlayer getPlayer() {
				return new IPlayer.DummyPlayer();
			}

			@Override
			public boolean needsPlayersGround() {
				return false;
			}

			@Override
			public boolean isShip() {
				return false;
			}
		};
	}
}
