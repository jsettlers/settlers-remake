/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.sound;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.graphics.map.MapDrawContext;

/**
 * Plays the background sound for birds and landscapes.
 *
 * @author michael
 */
public class BackgroundSound implements Runnable {
	private static final int BACKGROUND_SLEEP_TIME = 50;
	private static final double BIRDS1_FRACTION = .25;
	private static final double BIRDS_FREQUENCY = .15;

	private static final float BIRDS_VOLUME = .3f;
	private static final float WATER_VOLUME =  .1f;
	private static final float DESERT_VOLUME =  .05f;
	private static final float RIVER_VOLUME =  .03f;
	private static final float MOUNTAIN_VOLUME =  .003f;

	private static final int INDEX_BIRDS1 = 69;
	private static final int INDEX_BIRDS2 = 70;
	private static final int INDEX_WATER = 68;
	private static final int INDEX_DESERT = 67;
	private static final int INDEX_RIVER = 71;
	private static final int INDEX_MOUNTAIN = 73;

	private final MapDrawContext map;
	private final SoundManager sound;
	private final Object waitMutex = new Object();
	private boolean stopped = false;

	/**
	 * Creates a new background sound player.
	 * 
	 * @param map
	 *            The map draw context to generate sounds for.
	 * @param sound
	 *            The sound manager to use.
	 */
	public BackgroundSound(MapDrawContext map, SoundManager sound) {
		this.map = map;
		this.sound = sound;
	}

	@Override
	public void run() {
		try {
			while (!stopped) {
				waitTime(BACKGROUND_SLEEP_TIME);
				if (stopped) {
					break;
				}
				MapRectangle screen = map.getScreenArea();
				if (screen == null) {
					continue;
				}
				sound.setMap(map);
				int line = (int) (Math.random() * screen.getHeight());

				int x0 = screen.getLineStartX(line);
				int x = x0 + (int) (Math.random() * screen.getWidth());
				int y = screen.getLineY(line);

				if (hasTree(x, y) && Math.random() < BIRDS_FREQUENCY) {
					if (Math.random() < BIRDS1_FRACTION) {
						sound.playSound(INDEX_BIRDS1, BIRDS_VOLUME, x, y);
					} else {
						sound.playSound(INDEX_BIRDS2, BIRDS_VOLUME, x, y);
					}
				} else if (hasDesert(x, y)) {
					sound.playSound(INDEX_DESERT, DESERT_VOLUME, x, y);
				} else if (hasWater(x, y)) {
					sound.playSound(INDEX_WATER, WATER_VOLUME, x, y);
				} else if (hasMountain(x, y)) {
					sound.playSound(INDEX_MOUNTAIN, MOUNTAIN_VOLUME, x, y);
				} else for (int x1 = 0; x1 < screen.getWidth(); x1++) {
					if (hasRiver(x0 + x1, y)) {
						sound.playSound(INDEX_RIVER, RIVER_VOLUME, x0 + x1, y);
					}
				}
			}
		} catch (Throwable e) {
			System.out.println("Sound thread died because of an exception.");
			e.printStackTrace();
		}
	}

	private void waitTime(int time) {
		synchronized (waitMutex) {
			try {
				waitMutex.wait(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean hasDesert(int x, int y) {
		return map.checkMapCoordinates(x, y) && map.getVisibleStatus(x, y) != 0
				&& map.getLandscape(x, y) == ELandscapeType.DESERT;
	}

	private boolean hasWater(int x, int y) {
		return map.checkMapCoordinates(x, y) && map.getVisibleStatus(x, y) != 0
				&& map.getLandscape(x, y) == ELandscapeType.WATER1;
	}

	private boolean hasRiver(int x, int y) {
		return map.checkMapCoordinates(x, y) && map.getVisibleStatus(x, y) != 0
				&& map.getLandscape(x, y) == ELandscapeType.RIVER1;
	}

	private boolean hasMountain(int x, int y) {
		return map.checkMapCoordinates(x, y) && map.getVisibleStatus(x, y) != 0
				&& map.getLandscape(x, y) == ELandscapeType.MOUNTAIN;
	}

	private boolean hasTree(int cx, int cy) {
		for (int x = cx - 2; x <= cx + 2; x++) {
			for (int y = cy - 2; y <= cy + 2; y++) {
				if (map.checkMapCoordinates(x, y)
						&& map.getVisibleStatus(x, y) != 0
						&& hasTreeObject(x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasTreeObject(int x, int y) {
		IMapObject o = map.getMap().getMapObjectsAt(x, y);
		while (o != null) {
			EMapObjectType type = o.getObjectType();
			if (type == EMapObjectType.TREE_ADULT
					|| type == EMapObjectType.TREE_DEAD) {
				return true;
			}
			o = o.getNextObject();
		}
		return false;
	}

	/**
	 * Starts the sound player.
	 */
	public void start() {
		Thread thread = new Thread(this, "background-sound");
		thread.start();
	}

	/**
	 * Stops the sound player.
	 */
	public void stop() {
		synchronized (waitMutex) {
			stopped = true;
			waitMutex.notifyAll();
		}
	}
}
