package jsettlers.logic.algorithms.fogofwar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.CircleIterator;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

/**
 * This class holds the fog of war for a given map and player.
 * 
 * @author michael
 */
public class FogOfWar implements Serializable {
	private static final long serialVersionUID = 1877994785778678510L;
	/**
	 * Longest distance any unit may look
	 */
	private static final byte MAX_VIEWDISTANCE = 55;
	private static final byte MOVABLE_VIEWDISTANCE = 8;

	private static final int PADDING = 10;

	private final byte[][] sight;
	private boolean enabled = true;
	transient private IFogOfWarGrid grid;

	public FogOfWar(short width, short height) {
		this(width, height, false);
	}

	public FogOfWar(final short width, final short height, boolean exploredOnStart) {
		sight = new byte[width][height];

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
	}

	public void startThread(IFogOfWarGrid grid) {
		this.grid = grid;
		if (grid.getHeight() > 3 * MAX_VIEWDISTANCE) {
			FogCorrectionThread thread = new FogCorrectionThread();
			thread.start();
		} else {
			SimpleCorrectionTread thread = new SimpleCorrectionTread();
			thread.start();
		}

		rebuildAll(sight);
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
			return CommonConstants.FOG_OF_WAR_VISIBLE;
		}
	}

	/**
	 * This thread sweeps the map from top to bottom. It has a buffer of the distance MAX_VIEWDISTANCE behind and before the sweep line.
	 * <p>
	 * Whenever the thread encounters a new item that may see, it draws a visible circle around it on the buffer.
	 * 
	 * @author michael
	 */
	private class FogCorrectionThread extends Thread {
		private static final int BUFFER_HEIGHT = MAX_VIEWDISTANCE * 2;
		byte[][] buffer;

		private CircleDrawer bufferdrawer;

		private FogCorrectionThread() {
			super("advanced fog of war correction");

			buffer = new byte[grid.getWidth()][BUFFER_HEIGHT];
			bufferdrawer = new CircleDrawer(buffer) {
				@Override
				protected int convertY(int mapy) {
					if (mapy > 0 && mapy < grid.getHeight()) {
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
			for (int x = 0; x < grid.getWidth(); x++) {
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
				for (int sweepline = BUFFER_HEIGHT / 2; sweepline < grid.getHeight() - BUFFER_HEIGHT / 2; sweepline++) {
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
			for (int mapy = grid.getHeight() - BUFFER_HEIGHT / 2; mapy < grid.getHeight(); mapy++) {
				applyBufferLine(mapy);
			}

			for (int mapy = grid.getHeight() - BUFFER_HEIGHT; mapy < grid.getHeight(); mapy++) {
				for (int x = 0; x < grid.getWidth(); x++) {
					sight[x][mapy] = buffer[x][bufferPos(mapy)];
				}
			}
		}

		/**
		 * Loads the buffer for the map.
		 */
		private void loadFirstBuffer() {
			for (int y = 0; y < BUFFER_HEIGHT; y++) {
				for (int x = 0; x < grid.getWidth(); x++) {
					buffer[x][y] = dimDown(sight[x][y]);
				}
			}

			for (int y = 0; y < BUFFER_HEIGHT / 2; y++) {
				applyBufferLine(y);
			}
		}

		private void doNextLine(int sweepline) {
			moveToFromBuffer(sweepline - BUFFER_HEIGHT / 2, sweepline + BUFFER_HEIGHT / 2);
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
			for (int x = 0; x < grid.getWidth(); x++) {
				sight[x][lastliney] = buffer[x][lastbuffery];
				buffer[x][frontbuffery] = dimDown(sight[x][frontliney]);
			}
		}
	}

	private static byte dimDown(byte oldvalue) {
		if (oldvalue > CommonConstants.FOG_OF_WAR_EXPLORED) {
			return CommonConstants.FOG_OF_WAR_EXPLORED;
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
		 * Draws a circle to the buffer line. Each point is only brightened and onyl drawn if its x coordinate is in [0, mapWidth - 1] and its
		 * computed y coordinate is bigger than 0.
		 */
		protected void drawCircleToBuffer(int bufferx, int buffery, int viewdistance) {
			MapCircle circle = new MapCircle(bufferx, buffery, Math.min(viewdistance + PADDING, MAX_VIEWDISTANCE));
			CircleIterator iterator = circle.iterator();
			while (iterator.hasNext()) {
				int point = iterator.nextXY();
				int currentx = point & 0xffff;
				int currenty = point >> 16;
			
				int currentbuffery = convertY(currenty);
				if (currentx >= 0 && currentx < grid.getWidth() && currentbuffery >= 0) {
					double distance = circle.distanceToCenter(currentx, currenty);
					byte newsight;
					if (circle.isCloserToCenter(currentx, currenty, viewdistance)) {
						newsight = CommonConstants.FOG_OF_WAR_VISIBLE;
					} else {
						newsight = (byte) (CommonConstants.FOG_OF_WAR_VISIBLE - (distance - viewdistance) / PADDING
								* CommonConstants.FOG_OF_WAR_VISIBLE);
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
		IMovable movable = grid.getMovableAt(x, y);
		if (movable != null) {
			return MOVABLE_VIEWDISTANCE;
		} else {
			int view = 0;
			IMapObject baseobject = grid.getMapObjectsAt(x, y);
			while (baseobject != null) {
				if (baseobject.getObjectType() == EMapObjectType.BUILDING) {
					view = getViewForBuilding((IBuilding) baseobject);
				}
				baseobject = baseobject.getNextObject();
			}
			return view;
		}
	}

	private static int getViewForBuilding(IBuilding baseobject) {
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
				if (mapy < grid.getHeight()) {
					return mapy;
				} else {
					return -1;
				}
			}
		};
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				buffer[x][y] = dimDown(buffer[x][y]);
			}
		}

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
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
		return sight[centerx][centery] >= CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	public void toggleEnabled() {
		enabled = !enabled;
	}
}
