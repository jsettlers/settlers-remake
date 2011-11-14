package jsettlers.graphics.map.draw;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;

/**
 * This class holds the fog of war for a given map and player.
 * 
 * @author michael
 */
public class FogOfWar {
	private final IGraphicsGrid map;

	private final byte[][] sight;

	private boolean enabled = true;

	public static final byte EXPLORED = 50;

	public static final byte VISIBLE = 100;

	/**
	 * Longest distance any unit may look
	 */
	private static final byte MAX_VIEWDISTANCE = 55;

	private static final byte MOVABLE_VIEWDISTANCE = 8;

	private static final int PADDING = 10;

	public FogOfWar(IGraphicsGrid map) {
		this(map, false);
	}

	public FogOfWar(IGraphicsGrid map, boolean exploredOnStart) {
		this.map = map;
		sight = new byte[map.getWidth()][map.getHeight()];

		byte defaultSight = 0;
		if (exploredOnStart) {
			defaultSight = EXPLORED;
		}
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				sight[x][y] = defaultSight;
			}
		}
		rebuildAll(sight);

		if (map.getHeight() > 3 * MAX_VIEWDISTANCE) {
			FogCorrectionThread thread = new FogCorrectionThread();
			thread.start();
		} else {
			SimpleCorrectionTread thread = new SimpleCorrectionTread();
			thread.start();
		}
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
	public byte getVisibleStatus(int x, int y) {
		if (enabled) {
			return sight[x][y];
		} else {
			return VISIBLE;
		}
	}

	/**
	 * This thread sweeps the map from top to bottom. It has a buffer of the
	 * distance MAX_VIEWDISTANCE behind and before the sweep line.
	 * <p>
	 * Whenever the thread encounters a new item that may see, it draws a
	 * visible circle around it on the buffer.
	 * 
	 * @author michael
	 */
	private class FogCorrectionThread extends Thread {
		private static final int BUFFER_HEIGHT = MAX_VIEWDISTANCE * 2;
		byte[][] buffer;

		private CircleDrawer bufferdrawer;

		private FogCorrectionThread() {
			super("advanced fog of war correction");

			buffer = new byte[map.getWidth()][BUFFER_HEIGHT];
			bufferdrawer = new CircleDrawer(buffer) {
				@Override
				protected int convertY(int mapy) {
					if (mapy > 0 && mapy < map.getHeight()) {
						return bufferPos(mapy);
					} else {
						return -1;
					}
				}
			};

			super.setDaemon(true);
		}

		/**
		 * Adds everything that can see to the buffer.
		 * 
		 * @param buffery
		 * @param mapy
		 */
		private void applyBufferLine(int mapy) {
			for (int x = 0; x < map.getWidth(); x++) {
				int distance = getViewDistanceForPosition(x, mapy);
				if (distance > 0) {
					bufferdrawer.drawCircleToBuffer(x, mapy, distance);
				}
			}
		}

		private int bufferPos(int mapy) {
			return mapy % BUFFER_HEIGHT;
		}

		@Override
		public void run() {
			while (true) {
				loadFirstBuffer();
				for (int sweepline = BUFFER_HEIGHT / 2; sweepline < map
				        .getHeight() - BUFFER_HEIGHT / 2; sweepline++) {
					doNextLine(sweepline);
					if (sweepline % 32 == 0) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				loadLastBuffer();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void loadLastBuffer() {
			for (int mapy = map.getHeight() - BUFFER_HEIGHT / 2; mapy < map
			        .getHeight(); mapy++) {
				applyBufferLine(mapy);
			}

			for (int mapy = map.getHeight() - BUFFER_HEIGHT; mapy < map
			        .getHeight(); mapy++) {
				for (int x = 0; x < map.getWidth(); x++) {
					sight[x][mapy] = buffer[x][bufferPos(mapy)];
				}
			}
		}

		/**
		 * Loads the buffer for the map.
		 */
		private void loadFirstBuffer() {
			for (int y = 0; y < BUFFER_HEIGHT; y++) {
				for (int x = 0; x < map.getWidth(); x++) {
					buffer[x][y] = dimDown(sight[x][y]);
				}
			}

			for (int y = 0; y < BUFFER_HEIGHT / 2; y++) {
				applyBufferLine(y);
			}
		}

		private void doNextLine(int sweepline) {
			moveToFromBuffer(sweepline - BUFFER_HEIGHT / 2, sweepline
			        + BUFFER_HEIGHT / 2);
			applyBufferLine(sweepline);
		}

		/**
		 * Moves the line from the buffer to the map.
		 * 
		 * @param lastliney
		 *            The last line of the buffer to convert to the map.
		 * @param frontline
		 *            The line of the buffer to load to it.
		 */
		private void moveToFromBuffer(int lastliney, int frontliney) {
			int lastbuffery = bufferPos(lastliney);
			int frontbuffery = bufferPos(frontliney);
			for (int x = 0; x < map.getWidth(); x++) {
				sight[x][lastliney] = buffer[x][lastbuffery];
				buffer[x][frontbuffery] = dimDown(sight[x][frontliney]);
			}
		}
	}

	private byte dimDown(byte oldvalue) {
		if (oldvalue > EXPLORED) {
			return EXPLORED;
		} else {
			return oldvalue;
		}
	}

	private class CircleDrawer {
		private final byte[][] buffer;

		private CircleDrawer(byte[][] buffer) {
			this.buffer = buffer;
		}

		protected int convertY(int mapy) {
			return mapy;
		}

		/**
		 * Draws a circle to the buffer line. Each point is only brightened and
		 * onyl drawn if its x coordinate is in [0, mapWidth - 1] and its
		 * computed y coordinate is bigger than 0.
		 */
		private void drawCircleToBuffer(int bufferx, int buffery,
		        int viewdistance) {
			MapCircle circle =
			        new MapCircle(bufferx, buffery, Math.min(viewdistance
			                + PADDING, MAX_VIEWDISTANCE));
			for (ISPosition2D pos : circle) {
				int currentx = pos.getX();
				int currenty = pos.getY();
				int currentbuffery = convertY(currenty);
				if (currentx >= 0 && currentx < map.getWidth()
				        && currentbuffery >= 0) {
					double distance =
					        circle.distanceToCenter(currentx, currenty);
					byte newsight;
					if (circle.isCloserToCenter(currentx, currenty,
					        viewdistance)) {
						newsight = VISIBLE;
					} else {
						newsight =
						        (byte) (VISIBLE - (distance - viewdistance)
						                / PADDING * VISIBLE);

					}
					increaseBufferAt(currentx, currentbuffery, newsight);
				}
			}
		}

		private void increaseBufferAt(int currentx, int bufferPos, byte newsight) {
			if (buffer[currentx][bufferPos] < newsight) {
				buffer[currentx][bufferPos] = newsight;
			}
		}

	}

	private class SimpleCorrectionTread extends Thread {
		byte[][] myBuffer;

		public SimpleCorrectionTread() {
			super("simple fog of war correction");
			super.setDaemon(true);
		}

		@Override
		public void run() {
			myBuffer = new byte[sight.length][sight[0].length];
			copyBuffer(myBuffer, sight);

			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				rebuildAll(myBuffer);
				copyBuffer(sight, myBuffer);
			}
		}

		private void copyBuffer(byte[][] dest, byte[][] src) {
			for (int x = 0; x < dest.length; x++) {
				for (int y = 0; y < dest[x].length; y++) {
					dest[x][y] = src[x][y];
				}
			}
		}
	}

	private int getViewDistanceForPosition(int x, int y) {
		IMovable movable = map.getMovableAt(x, y);
		if (movable != null) {
			return MOVABLE_VIEWDISTANCE;
		} else {
			int view = 0;
			IMapObject baseobject = map.getMapObjectsAt(x, y);
			while (baseobject != null) {
				if (baseobject.getObjectType() == EMapObjectType.BUILDING) {
					view = getViewForBuilding((IBuilding) baseobject);
				}
				baseobject = baseobject.getNextObject();
			}
			return view;
		}
	}

	private int getViewForBuilding(IBuilding baseobject) {
		if (baseobject.getStateProgress() > .999f) {
			return baseobject.getBuildingType().getViewDistance();
		} else {
			return 0;
		}
	}

	private void rebuildAll(byte[][] buffer) {
		CircleDrawer drawer = new CircleDrawer(buffer) {
			@Override
			protected int convertY(int mapy) {
				if (mapy < map.getHeight()) {
					return mapy;
				} else {
					return -1;
				}
			}
		};
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				buffer[x][y] = dimDown(buffer[x][y]);
			}
		}

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				int distance = getViewDistanceForPosition(x, y);
				if (distance > 0) {
					drawer.drawCircleToBuffer(x, y, distance);
				}
			}
		}
	}

	public void pause() {

	}

	public void unpause() {

	}

	public void end() {

	}

	public boolean isVisible(int centerx, int centery) {
		return sight[centerx][centery] >= VISIBLE;
	}

	public void toggleEnabled() {
		enabled = !enabled;
	}
}
