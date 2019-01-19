/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.algorithms.fogofwar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.algorithms.fogofwar.CachedViewCircle.CachedViewCircleIterator;
import jsettlers.common.CommonConstants;
import jsettlers.common.player.IPlayer;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;

/**
 * This class holds the fog of war for a given map and team.
 * 
 * @author Andreas Eberle
 */
public final class FogOfWar implements Serializable {
	private static final long serialVersionUID = 1877994785778678510L;
	/**
	 * Longest distance any unit may look
	 */
	private static final byte MAX_VIEW_DISTANCE = 65;
	static final int PADDING = 10;

	private final byte team;

	private final short width;
	private final short height;
	private byte[][] sight;

	private transient boolean enabled = Constants.FOG_OF_WAR_DEFAULT_ENABLED;
	private transient IFogOfWarGrid grid;
	private transient boolean canceled;

	public FogOfWar(short width, short height, IPlayer player) {
		this.width = width;
		this.height = height;
		this.team = player.getTeamId();
		this.sight = new byte[width][height];
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		enabled = true;
	}

	public void start(IFogOfWarGrid grid) {
		this.grid = grid;
		NewFoWThread thread = new NewFoWThread();
		thread.start();
	}

	/**
	 * Gets the visible status of a map pint
	 * 
	 * @param x
	 *            The x coordinate of the point in 0..(mapWidth - 1)
	 * @param y
	 *            The y coordinate of the point in 0..(mapHeight - 1)
	 * @return The status from 0 to visible.
	 */
	public final byte getVisibleStatus(int x, int y) {
		if (enabled) {
			return (byte) Math.min(sight[x][y], CommonConstants.FOG_OF_WAR_VISIBLE);
		} else {
			return CommonConstants.FOG_OF_WAR_VISIBLE;
		}
	}

	public byte[][] getVisibleStatusArray() {
		if(enabled) return sight;
		return null;
	}

	private boolean isPlayerOK(IPlayerable playerable) {
		return (MatchConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR || (playerable.getPlayer().getTeamId() == team));
	}

	public final void toggleEnabled() {
		enabled = !enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	final class NewFoWThread extends Thread {
		private static final byte DIM_DOWN_SPEED = 10;
		private final CircleDrawer drawer = new CircleDrawer();
		private byte[][] buffer = new byte[width][height];

		NewFoWThread() {
			super("FoWThread");
			super.setDaemon(true);
		}

		@Override
		public final void run() {
			mySleep(500L);

			while (!canceled) {
				// StopWatch watch = new MilliStopWatch();
				// watch.restart();
				if (enabled) {
					rebuildSight();
				}
				// watch.stop("NewFoWThread needed: ");

				mySleep(800L);
			}
		}

		private void rebuildSight() {
			drawer.setBuffer(buffer);

			for (short x = 0; x < width; x++) {
				for (short y = 0; y < height; y++) {
					byte currSight = sight[x][y];

					if (currSight >= CommonConstants.FOG_OF_WAR_EXPLORED) {
						byte newSight = (byte) (currSight - DIM_DOWN_SPEED);
						if (newSight < CommonConstants.FOG_OF_WAR_EXPLORED) {
							buffer[x][y] = CommonConstants.FOG_OF_WAR_EXPLORED;
						} else {
							buffer[x][y] = newSight;
						}
					} else {
						buffer[x][y] = sight[x][y];
					}
				}
			}

			ConcurrentLinkedQueue<? extends IViewDistancable> buildings = grid.getBuildingViewDistancables();
			applyViewDistances(buildings);

			ConcurrentLinkedQueue<? extends IViewDistancable> movables = grid.getMovableViewDistancables();
			applyViewDistances(movables);

			byte[][] temp = sight;
			sight = buffer;
			buffer = temp;
		}

		private void applyViewDistances(ConcurrentLinkedQueue<? extends IViewDistancable> objects) {
			for (IViewDistancable curr : objects) {
				if (isPlayerOK(curr)) {
					short distance = curr.getViewDistance();
					if (distance > 0) {
						ShortPoint2D pos = curr.getPosition();
						if (pos != null)
							drawer.drawCircleToBuffer(pos.x, pos.y, distance);
					}
				}
			}
		}

		private void mySleep(long ms) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	final class CircleDrawer {
		private byte[][] buffer;
		private final CachedViewCircle[] cachedCircles = new CachedViewCircle[MAX_VIEW_DISTANCE];

		public final void setBuffer(byte[][] buffer) {
			this.buffer = buffer;
		}

		/**
		 * Draws a circle to the buffer line. Each point is only brightened and onlydrawn if its x coordinate is in [0, mapWidth - 1] and its computed y coordinate is bigger than 0.
		 */
		final void drawCircleToBuffer(int bufferX, int bufferY, int viewDistance) {
			CachedViewCircle circle = getCachedCircle(viewDistance);
			CachedViewCircleIterator iterator = circle.iterator(bufferX, bufferY);

			while (iterator.hasNext()) {
				final int x = iterator.getCurrX();
				final int y = iterator.getCurrY();

				if (x >= 0 && x < width && y > 0 && y < height) {
					byte oldSight = buffer[x][y];
					if (oldSight < CommonConstants.FOG_OF_WAR_VISIBLE) {
						byte newSight = iterator.getCurrSight();
						if (oldSight < newSight) {
							buffer[x][y] = newSight;
						}
					}
				}
			}
		}

		private CachedViewCircle getCachedCircle(int viewDistance) {
			int radius = Math.min(viewDistance + PADDING, MAX_VIEW_DISTANCE - 1);
			if (cachedCircles[radius] == null) {
				cachedCircles[radius] = new CachedViewCircle(radius);
			}

			return cachedCircles[radius];
		}
	}

	public void cancel() {
		this.canceled = true;
	}
}
