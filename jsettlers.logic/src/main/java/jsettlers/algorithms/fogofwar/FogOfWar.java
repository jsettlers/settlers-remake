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
import java.util.BitSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ShortPoint2D;
import go.graphics.FramerateComputer;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.movable.Movable;

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
	public static final byte MAX_VIEW_DISTANCE = 65;
	public static final int PADDING = 10;

	public final byte team;

	public final short width;
	public final short height;
	public byte[][] sight;
	public short[][][] visibleRefs;
	public transient FowDimThread dimThread;
	public transient FoWRefThread refThread;

	public transient CircleDrawer circleDrawer = new CircleDrawer();
	private transient IGraphicsBackgroundListener backgroundListener = new MainGrid.NullBackgroundListener();
	public transient boolean enabled = Constants.FOG_OF_WAR_DEFAULT_ENABLED;
	public transient boolean canceled = false;

	public FogOfWar(short width, short height, IPlayer player) {
		this.width = width;
		this.height = height;
		this.team = player.getTeamId();
		this.sight = new byte[width][height];
		this.visibleRefs = new short[width][height][0];
		refThread = new FoWRefThread();
		dimThread = new FowDimThread();
	}

	public void start() {
		instance = this;
		refThread.start();
		dimThread.start();
	}

	public static void queueResizeCircle(ShortPoint2D at, short from, short to) {
		BuildingFoWTask foWTask = new BuildingFoWTask();
		foWTask.from = from;
		foWTask.to = to;
		foWTask.at = at;
		synchronized (instance.refThread.nextTasks) {
			instance.refThread.nextTasks.add(foWTask);
		}
	}

	public static FogOfWar instance;

	public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		if (backgroundListener != null) {
			this.backgroundListener = backgroundListener;
		} else {
			this.backgroundListener = new MainGrid.NullBackgroundListener();
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		refThread = new FoWRefThread();
		dimThread = new FowDimThread();
		circleDrawer = new CircleDrawer();
		enabled = Constants.FOG_OF_WAR_DEFAULT_ENABLED;
		backgroundListener = new MainGrid.NullBackgroundListener();
	}

	public static class BuildingFoWTask implements FoWTask {
		ShortPoint2D at;
		short from;
		short to;
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
		return enabled ? sight[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	public byte[][] getVisibleStatusArray() {
		return sight;
	}

	public final void toggleEnabled() {
		enabled = !enabled;
		backgroundListener.updateAllColors();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		backgroundListener.updateAllColors();
	}

	public static final int CIRCLE_REMOVE = 1;
	public static final int CIRCLE_ADD = 2;
	public static final int CIRCLE_DIM = 8;

	public class FoWRefThread extends FoWThread {
		public final ConcurrentLinkedQueue<FoWTask> nextTasks = new ConcurrentLinkedQueue<>();
		public final ConcurrentLinkedQueue<FoWTask> tasks = new ConcurrentLinkedQueue<>();

		FoWRefThread() {
			super("FOW-reference-updater", CommonConstants.FOG_OF_WAR_REF_UPDATE_FRAMERATE);
		}

		@Override
		public void taskProcessor() {
			synchronized (nextTasks) {
				tasks.addAll(nextTasks);
				nextTasks.clear();
			}
			if (enabled) {
				Iterator<FoWTask> it = tasks.iterator();
				while(it.hasNext()) {
					if(runTask(it.next())) {
						it.remove();
					}

				}
			}
		}

		boolean runTask(FoWTask task) {
			if(task instanceof BuildingFoWTask) {
				BuildingFoWTask bFOW = (BuildingFoWTask) task;
				if (bFOW.to > 0) circleDrawer.drawCircleToBuffer(bFOW.at.x, bFOW.at.y, bFOW.to, CIRCLE_ADD);
				if (bFOW.from > 0) circleDrawer.drawCircleToBuffer(bFOW.at.x, bFOW.at.y, bFOW.from, CIRCLE_REMOVE);
				circleDrawer.drawCircleToBuffer(bFOW.at.x, bFOW.at.y, bFOW.to>bFOW.from ? bFOW.to : bFOW.from, CIRCLE_DIM);
				return true;
			} else {
				Movable mv = (Movable) task;
				ShortPoint2D currentPos = mv.getPosition();
				boolean alive = mv.isAlive();
				if(mv.fowPosition != currentPos) {
					if(alive) circleDrawer.drawCircleToBuffer(currentPos.x, currentPos.y, Constants.MOVABLE_VIEW_DISTANCE, CIRCLE_ADD|CIRCLE_DIM);
					if(mv.fowPosition != null) circleDrawer.drawCircleToBuffer(mv.fowPosition.x, mv.fowPosition.y, Constants.MOVABLE_VIEW_DISTANCE, CIRCLE_REMOVE|CIRCLE_DIM);
					mv.fowPosition = currentPos;
				}
				return !alive; // remove if Movable is deleted
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public class FowDimThread extends FoWThread {
		FowDimThread() {
			super("FOW-dimmer", CommonConstants.FOG_OF_WAR_DIM_FRAMERATE);
			nextUpdate = new BitSet(width*height);
		}

		public final BitSet nextUpdate;
		public BitSet update;

		@Override
		public void taskProcessor() {
			synchronized (nextUpdate) {
				update = (BitSet) nextUpdate.clone();
			}

			for(int x=0;x != width;x++) {
				for(int y=0;y!=height;y++) {
					if(!update.get(y*width+x)) continue;

					byte dimTo = dimmedSight(x, y);

					if(dimTo != sight[x][y]) {
						sight[x][y] = dim(sight[x][y], dimTo);
						backgroundListener.backgroundColorChangedAt(x, y);

						if (sight[x][y] == dimTo) update.set(y * width + x, false);
					} else {
						update.set(y*width+x, false);
					}
				}
			}
		}
	}

	public static byte dim(byte value, byte dimTo) {
		if(value >= CommonConstants.FOG_OF_WAR_EXPLORED && dimTo < CommonConstants.FOG_OF_WAR_EXPLORED) dimTo = CommonConstants.FOG_OF_WAR_EXPLORED;
		if(value < CommonConstants.FOG_OF_WAR_EXPLORED && dimTo < value) return value;

		byte dV = (byte) (value-dimTo);
		if(dV < 0) dV = (byte) -dV;

		if(dV < CommonConstants.FOG_OF_WAR_DIM) return dimTo;
		if(value < dimTo) return (byte) (value+CommonConstants.FOG_OF_WAR_DIM);
		else return (byte) (value-CommonConstants.FOG_OF_WAR_DIM);
	}

	public byte dimmedSight(int x, int y) {
		short[] refs = instance.visibleRefs[x][y];
		if(refs.length == 0) return 0;

		byte value = CommonConstants.FOG_OF_WAR_VISIBLE;

		for(int i = 0;i != refs.length;i++) {
			if(refs[i] > 0) break;
			value -= 10;
		}

		return value;
	}

	public abstract class FoWThread extends Thread {
		public final int framerate;

		final FramerateComputer fc = new FramerateComputer();

		FoWThread(String name, int framerate) {
			super(name);
			this.framerate = framerate;
		}

		@Override
		public final void run() {
			Movable.initFow(team);
			Building.initFow(team);

			while (!canceled) {
				if(!MatchConstants.clock().isPausing()) taskProcessor();
				fc.nextFrame(framerate);
			}
		}

		public abstract void taskProcessor();

		public long start;
	}

	public void cancel() {
		canceled = true;
	}

	public int maxIndex(int x, int y) {
		short[] array = instance.visibleRefs[x][y];
		int lastEntry = array.length-1;

		if(array.length == 0) return 0;

		for(int i = lastEntry;i >= 0;i--) {
			if(array[i] > 0) return i+1;
		}
		return 0;
	}

	final class CircleDrawer {
		public final CachedViewCircle[] cachedCircles = new CachedViewCircle[MAX_VIEW_DISTANCE];

		/**
		 * Draws a circle to the buffer line. Each point is only brightened and onlydrawn if its x coordinate is in [0, mapWidth - 1] and its computed y coordinate is bigger than 0.
		 */
		final void drawCircleToBuffer(int bufferX, int bufferY, int viewDistance, int state) {
			CachedViewCircle circle = getCachedCircle(viewDistance);
			CachedViewCircle.CachedViewCircleIterator iterator = circle.iterator(bufferX, bufferY);

			while (iterator.hasNext()) {
				final int x = iterator.getCurrX();
				final int y = iterator.getCurrY();

				if (x >= 0 && x < width && y > 0 && y < height) {
					byte tmpIndex = iterator.getRefIndex();

					if((state&CIRCLE_ADD) > 0) {
						if(instance.visibleRefs[x][y].length <= tmpIndex) { // enlarge ref index array
							short[] tmpRef = instance.visibleRefs[x][y];
							instance.visibleRefs[x][y] = new short[tmpIndex+1];
							System.arraycopy(tmpRef, 0, instance.visibleRefs[x][y], 0, tmpRef.length);
						}

						instance.visibleRefs[x][y][tmpIndex]++;
					}
					if((state&CIRCLE_REMOVE) > 0) {
						instance.visibleRefs[x][y][tmpIndex]--;
						if(instance.visibleRefs[x][y][tmpIndex] == 0 && instance.visibleRefs[x][y].length == tmpIndex+1) { // minimize ref index array size
							int newLength = maxIndex(x, y);

							short[] tmpRef = instance.visibleRefs[x][y];
							instance.visibleRefs[x][y] = new short[newLength];
							System.arraycopy(tmpRef, 0, instance.visibleRefs[x][y], 0, newLength);
						}
					}

					if((state&CIRCLE_DIM) > 0 && sight[x][y] != dimmedSight(x, y)) {
						synchronized (instance.dimThread.nextUpdate) {
							instance.dimThread.nextUpdate.set(y*width+x);
						}
					}
				}
			}
		}

		public CachedViewCircle getCachedCircle(int viewDistance) {
			int radius = Math.min(viewDistance, MAX_VIEW_DISTANCE - 1);
			if (cachedCircles[radius] == null) {
				cachedCircles[radius] = new CachedViewCircle(radius);
			}

			return cachedCircles[radius];
		}
	}
}
