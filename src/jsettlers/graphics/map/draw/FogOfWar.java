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

	private static final byte EXPLORED = 5;

	public static final byte VISIBLE = 10;

	/**
	 * Longest distance any unit may look
	 */
	private static final byte MAX_VIEWDISTANCE = 50;

	private static final byte MOVABLE_VIEWDISTANCE = 10;

	private static final int PADDING = 5;

	public FogOfWar(IGraphicsGrid map) {
		this.map = map;
		sight = new byte[map.getWidth()][map.getHeight()];

		// for (int x = 0; x < map.getWidth(); x++) {
		// for (int y = 0; y < map.getHeight(); y++) {
		// sight[x][y] = 0;
		// }
		// }
		rebuildAll(sight);

		if (map.getHeight() > 3 * MAX_VIEWDISTANCE) {
			FogCorrectionThread thread = new FogCorrectionThread();
			thread.start();
		} else {
			SimpleCorrectionTread thread = new SimpleCorrectionTread();
			thread.start();
		}
	}

	public byte getVisibleStatus(int x, int y) {
		return sight[x][y];
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
		int lastline = 0;
		/**
		 * Center sweep line
		 */
		int currentsweepline = MAX_VIEWDISTANCE;

		int frontline = 2 * MAX_VIEWDISTANCE;

		int bufferoffset = 0;

		byte[][] buffer;

		private FogCorrectionThread() {
			super("advanced fog of war correction");
			buffer = new byte[map.getWidth()][BUFFER_HEIGHT];
			for (int y = 0; y < BUFFER_HEIGHT; y++) {
				for (int x = 0; x < map.getWidth(); x++) {
					if (sight[x][y] < EXPLORED) {
						buffer[x][y] = sight[x][y];
					} else {
						buffer[x][y] = EXPLORED;
					}
				}
			}
			for (int y = 0; y < BUFFER_HEIGHT; y++) {
				applyBufferLine(y, y);
			}
		}

		/**
		 * Adds everything that can see to the buffer.
		 * 
		 * @param buffery
		 * @param mapy
		 */
		private void applyBufferLine(int mapy, int buffery) {
			for (int x = 0; x < map.getWidth(); x++) {
				int distance = getViewDistanceForPosition(x, mapy);
				if (distance > 0) {
					drawVisibleCircleToBuffer(x, buffery, distance);
				}
			}
		}

		/**
		 * This function has duplicated code!
		 * 
		 * @param x
		 * @param buffery
		 * @param distance
		 */
		private void drawVisibleCircleToBuffer(int x, int buffery, int distance) {
			MapCircle circle =
			        new MapCircle(x, MAX_VIEWDISTANCE, Math.min(distance
			                + PADDING, MAX_VIEWDISTANCE));
			for (ISPosition2D pos : circle) {
				int currentx = pos.getX();
				int currenty = pos.getY();
				if (currentx >= 0 && currentx < map.getWidth() && currenty >= 0
				        && currenty < BUFFER_HEIGHT) {
					if (circle.isCloserToCenter(currentx, currenty, distance)) {
						buffer[currentx][bufferPos(currenty)] = VISIBLE;
					} else {
						double pointdistance =
						        circle.distanceToCenter(currentx, currenty);
						byte newsight =
						        (byte) (VISIBLE - (pointdistance - distance)
						                / PADDING * VISIBLE);
						if (buffer[currentx][bufferPos(currenty)] < newsight) {
							buffer[currentx][bufferPos(currenty)] = newsight;
						}
					}
				}
			}
		}

		private int bufferPos(int y) {
			return (y + bufferoffset) % BUFFER_HEIGHT;
		}

		@Override
		public void run() {
			while (true) {
				for (int i = 0; i < 20; i++) {
					doNextLine();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void doNextLine() {
			moveLastLine();
			applyBufferLine(currentsweepline, BUFFER_HEIGHT / 2);

			lastline = increase(lastline);
			currentsweepline = increase(currentsweepline);
			frontline = increase(frontline);
			bufferoffset++;
			if (bufferoffset >= BUFFER_HEIGHT) {
				bufferoffset = 0;
			}
		}

		/**
		 * Moves the line from the buffer to the map.
		 */
		private void moveLastLine() {
			for (int x = 0; x < map.getWidth(); x++) {
				sight[x][lastline] = buffer[x][bufferoffset];
				if (sight[x][frontline] > EXPLORED) {
					buffer[x][bufferoffset] = EXPLORED;
				} else {
					buffer[x][bufferoffset] = sight[x][frontline];
				}
			}
		}

		private int increase(int line) {
			if (line < map.getHeight() - 1) {
				return line + 1;
			} else {
				return 0;
			}
		}
	}

	private class SimpleCorrectionTread extends Thread {
		byte[][] myBuffer;

		public SimpleCorrectionTread() {
			super("simple fog of war correction");
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
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (buffer[x][y] > EXPLORED) {
					buffer[x][y] = EXPLORED;
				}
			}
		}

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				int distance = getViewDistanceForPosition(x, y);
				if (distance > 0) {
					drawCircleAround(x, y, distance, buffer);
				}
			}
		}
	}

	private void drawCircleAround(int x, int y, int distance, byte[][] buffer) {
		MapCircle circle = new MapCircle(x, y, distance + PADDING);
		for (ISPosition2D pos : circle) {
			int currentx = pos.getX();
			int currenty = pos.getY();
			if (currentx >= 0 && currentx < map.getWidth() && currenty >= 0
			        && currenty < map.getHeight()) {
				if (circle.isCloserToCenter(currentx, currenty, distance)) {
					buffer[currentx][currenty] = VISIBLE;
				} else {
					double pointdistance =
					        circle.distanceToCenter(currentx, currenty);
					byte newsight =
					        (byte) (VISIBLE - (pointdistance - distance)
					                / PADDING * VISIBLE);
					if (buffer[currentx][currenty] < newsight) {
						buffer[currentx][currenty] = newsight;
					}
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
}
