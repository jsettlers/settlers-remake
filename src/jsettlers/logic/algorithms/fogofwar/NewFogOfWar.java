package jsettlers.logic.algorithms.fogofwar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.fogofwar.CachedViewCircle.CachedViewCircleIterator;

/**
 * This class holds the fog of war for a given map and player.
 * 
 * @author Andreas Eberle
 */
public final class NewFogOfWar implements Serializable {
	private static final long serialVersionUID = 1877994785778678510L;
	/**
	 * Longest distance any unit may look
	 */
	static final byte MAX_VIEWDISTANCE = 55;
	static final int PADDING = 10;

	private final byte player;

	final short width;
	final short height;
	byte[][] sight;

	private transient boolean enabled = true;
	transient private IFogOfWarGrid grid;
	private transient boolean canceled;

	public NewFogOfWar(short width, short height) {
		this(width, height, (byte) 0, false);
	}

	public NewFogOfWar(final short width, final short height, final byte player, final boolean exploredOnStart) {
		this.width = width;
		this.height = height;
		this.player = player;
		this.sight = new byte[width][height];

		byte defaultSight = 0;
		if (exploredOnStart) {
			defaultSight = CommonConstants.FOG_OF_WAR_EXPLORED;
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				sight[x][y] = defaultSight;
			}
		}
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		enabled = true;
	}

	public void startThread(IFogOfWarGrid grid) {
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

	@SuppressWarnings("unused")
	private final boolean isPlayerOK(IPlayerable playerable) {
		return (CommonConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR || (playerable.getPlayer() == player));
	}

	public final boolean isVisible(int centerx, int centery) {
		return sight[centerx][centery] >= CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	public final void toggleEnabled() {
		enabled = !enabled;
	}

	final class NewFoWThread extends Thread {
		private static final byte DIM_DOWN_SPEED = 10;
		private final CircleDrawer drawer;
		private byte[][] buffer;

		NewFoWThread() {
			super("NewFoWThread");
			this.buffer = new byte[width][height];
			super.setDaemon(true);
			drawer = new CircleDrawer();
		}

		@Override
		public final void run() {
			mySleep(500);

			while (!canceled) {
				StopWatch watch = new MilliStopWatch();
				watch.start();
				if (enabled) {
					rebuildSight();
				}
				watch.stop("NewFoWThread needed: ");

				mySleep(800);
			}
		}

		private final void rebuildSight() {
			drawer.setBuffer(buffer);

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
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

		private final void applyViewDistances(ConcurrentLinkedQueue<? extends IViewDistancable> objects) {
			for (IViewDistancable curr : objects) {
				if (isPlayerOK(curr)) {
					short distance = curr.getViewDistance();
					if (distance > 0) {
						ShortPoint2D pos = curr.getPos();
						if (pos != null)
							drawer.drawCircleToBuffer(pos.getX(), pos.getY(), distance);
					}
				}
			}
		}

		private final void mySleep(int ms) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	final class CircleDrawer {
		private byte[][] buffer;
		private final CachedViewCircle[] cachedCircles = new CachedViewCircle[MAX_VIEWDISTANCE];

		public final void setBuffer(byte[][] buffer) {
			this.buffer = buffer;
		}

		/**
		 * Draws a circle to the buffer line. Each point is only brightened and onlydrawn if its x coordinate is in [0, mapWidth - 1] and its computed
		 * y coordinate is bigger than 0.
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
			int radius = Math.min(viewDistance + PADDING, MAX_VIEWDISTANCE);
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
