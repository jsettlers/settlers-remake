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
import jsettlers.common.player.IPlayerable;

/**
 * This class holds the fog of war for a given map and player.
 * 
 * @author michael
 */
public final class FogOfWar implements Serializable {
	private static final long serialVersionUID = 1877994785778678510L;
	/**
	 * Longest distance any unit may look
	 */
	static final byte MAX_VIEWDISTANCE = 55;
	static final byte MOVABLE_VIEWDISTANCE = 8;

	static final int PADDING = 10;
	private static final int UNOCCUPIED_VIEW_DISTANCE = 5;
	private final byte player;

	final short width;
	final short height;
	final byte[][] sight;

	private boolean enabled = true;
	transient private IFogOfWarGrid grid;

	public FogOfWar(short width, short height) {
		this(width, height, (byte) 0, false);
	}

	public FogOfWar(final short width, final short height, final byte player, final boolean exploredOnStart) {
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
	}

	public void startThread(IFogOfWarGrid grid) {
		this.grid = grid;
		if (height > 3 * MAX_VIEWDISTANCE) {
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
	public final byte getVisibleStatus(int x, int y) {
		if (enabled) {
			return sight[x][y];
		} else {
			return CommonConstants.FOG_OF_WAR_VISIBLE;
		}
	}

	static final byte dimDown(byte oldvalue) {
		if (oldvalue > CommonConstants.FOG_OF_WAR_EXPLORED) {
			return CommonConstants.FOG_OF_WAR_EXPLORED;
		} else {
			return oldvalue;
		}
	}

	final int getViewDistanceForPosition(short x, short y) {
		IMovable movable = grid.getMovableAt(x, y);
		if (movable != null && isPlayerOK(movable)) {
			return MOVABLE_VIEWDISTANCE;
		} else {
			int view = 0;
			IMapObject baseobject = grid.getMapObjectsAt(x, y);
			while (baseobject != null) {
				if (baseobject.getObjectType() == EMapObjectType.BUILDING) {
					view = getViewForBuilding((IBuilding) baseobject);
					break; // there is only one building at a position
				}
				baseobject = baseobject.getNextObject();
			}
			return view;
		}
	}

	final int getViewForBuilding(IBuilding baseobject) {
		if (baseobject.getStateProgress() > .999f && isPlayerOK(baseobject)) {
			if (baseobject.isOccupied()) {
				return UNOCCUPIED_VIEW_DISTANCE;
			} else {
				return baseobject.getBuildingType().getViewDistance();
			}
		} else {
			return 0;
		}
	}

	private final boolean isPlayerOK(IPlayerable playerable) {
		return (CommonConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR || playerable.getPlayer() == player);
	}

	final void rebuildAll(byte[][] buffer) {
		CircleDrawer drawer = new CircleDrawer(buffer);
		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				buffer[x][y] = dimDown(buffer[x][y]);
			}
		}

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				int distance = getViewDistanceForPosition(x, y);
				if (distance > 0) {
					drawer.drawCircleToBuffer(x, y, distance);
				}
			}
		}
	}

	public final void pause() {
	}

	public final void unpause() {
	}

	public final void end() {
	}

	public final boolean isVisible(int centerx, int centery) {
		return sight[centerx][centery] >= CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	public final void toggleEnabled() {
		enabled = !enabled;
	}

	final class SimpleCorrectionTread extends Thread {
		byte[][] myBuffer;

		SimpleCorrectionTread() {
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

		private final void copyBuffer(byte[][] dest, byte[][] src) {
			for (int x = 0; x < dest.length; x++) {
				for (int y = 0; y < dest[x].length; y++) {
					dest[x][y] = src[x][y];
				}
			}
		}
	}

	/**
	 * This thread sweeps the map from top to bottom. It has a buffer of the distance MAX_VIEWDISTANCE behind and before the sweep line.
	 * <p>
	 * Whenever the thread encounters a new item that may see, it draws a visible circle around it on the buffer.
	 * 
	 * @author michael
	 */
	final class FogCorrectionThread extends Thread {
		/**
		 * height of the buffer ( needs to be a power of two! )
		 */
		private final short BUFFER_HEIGHT = (short) Math.pow(2, Math.ceil(Math.log(MAX_VIEWDISTANCE * 2) / Math.log(2)));
		byte[][] buffer;

		private CircleDrawer bufferdrawer;

		FogCorrectionThread() {
			super("advanced fog of war correction");

			buffer = new byte[width][BUFFER_HEIGHT];
			bufferdrawer = new CircleDrawer(buffer);

			super.setDaemon(true);
		}

		/**
		 * Adds everything that can see to the buffer.
		 * 
		 * @param buffery
		 * @param mapy
		 */
		private final void applyBufferLine(short mapy) {
			for (short x = 0; x < width; x++) {
				int distance = getViewDistanceForPosition(x, mapy);
				if (distance > 0) {
					bufferdrawer.drawCircleToBuffer(x, mapy, distance);
				}
			}
		}

		private final int bufferPos(int mapy) {
			return mapy % BUFFER_HEIGHT;
		}

		@Override
		public void run() {
			while (true) {
				loadFirstBuffer();
				for (short sweepline = (short) (BUFFER_HEIGHT / 2); sweepline < height - BUFFER_HEIGHT / 2; sweepline++) {
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

		private final void loadLastBuffer() {
			for (short mapy = (short) (height - BUFFER_HEIGHT / 2); mapy < height; mapy++) {
				applyBufferLine(mapy);
			}

			for (int mapy = height - BUFFER_HEIGHT; mapy < height; mapy++) {
				for (int x = 0; x < width; x++) {
					sight[x][mapy] = buffer[x][bufferPos(mapy)];
				}
			}
		}

		/**
		 * Loads the buffer for the map.
		 */
		private final void loadFirstBuffer() {
			for (short y = 0; y < BUFFER_HEIGHT; y++) {
				for (short x = 0; x < width; x++) {
					buffer[x][y] = dimDown(sight[x][y]);
				}
			}

			for (short y = 0; y < BUFFER_HEIGHT / 2; y++) {
				applyBufferLine(y);
			}
		}

		private final void doNextLine(short sweepline) {
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
		private final void moveToFromBuffer(int lastliney, int frontliney) {
			int lastbuffery = bufferPos(lastliney);
			int frontbuffery = bufferPos(frontliney);
			for (int x = 0; x < width; x++) {
				sight[x][lastliney] = buffer[x][lastbuffery];
				buffer[x][frontbuffery] = dimDown(sight[x][frontliney]);
			}
		}
	}

	class CircleDrawer {
		private final byte[][] buffer;
		private final int mask;

		CircleDrawer(byte[][] buffer) {
			this.buffer = buffer;

			int bufferHeight = buffer[0].length;
			byte usedBits = (byte) Math.ceil(Math.log(bufferHeight) / Math.log(2));

			this.mask = (int) (Math.pow(2, usedBits) - 1);
		}

		private final int convertY(int mapy) {
			if (mapy > 0 && mapy < height) {
				return mapy & mask;
			} else {
				return -1;
			}
		}

		/**
		 * Draws a circle to the buffer line. Each point is only brightened and onyl drawn if its x coordinate is in [0, mapWidth - 1] and its
		 * computed y coordinate is bigger than 0.
		 */
		final void drawCircleToBuffer(int bufferx, int buffery, int viewDistance) {
			MapCircle circle = new MapCircle(bufferx, buffery, Math.min(viewDistance + PADDING, MAX_VIEWDISTANCE));
			final int squaredViewDistance = viewDistance * viewDistance;
			CircleIterator iterator = circle.iterator();
			while (iterator.hasNext()) {
				final int currentY = iterator.nextY();
				final int currentX = iterator.nextX();

				int currentBufferY;
				if (currentX >= 0 && currentX < width && (currentBufferY = convertY(currentY)) >= 0) {
					double squaredDistance = circle.squaredDistanceToCenter(currentX, currentY);
					byte newSight;
					if (squaredDistance < squaredViewDistance) {
						newSight = CommonConstants.FOG_OF_WAR_VISIBLE;
					} else {
						newSight = (byte) (CommonConstants.FOG_OF_WAR_VISIBLE - (Math.sqrt(squaredDistance) - viewDistance) / PADDING
								* CommonConstants.FOG_OF_WAR_VISIBLE);
					}
					increaseBufferAt(currentX, currentBufferY, newSight);
				}
			}
		}

		private final void increaseBufferAt(int x, int y, byte newsight) {
			if (buffer[x][y] < newsight) {
				buffer[x][y] = newsight;
			}
		}

	}
}
