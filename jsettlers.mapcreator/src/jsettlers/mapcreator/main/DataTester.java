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
package jsettlers.mapcreator.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.LandscapeFader;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.main.error.Error;
import jsettlers.mapcreator.main.error.ErrorList;

public class DataTester implements Runnable {

	public static final int MAX_HEIGHT_DIFF = 3;
	private boolean retest = true;
	private final MapData data;

	// onyl used from test thread
	private boolean successful;
	private String result;
	private ShortPoint2D resultPosition;
	private final TestResultReceiver receiver;
	private final LandscapeFader fader = new LandscapeFader();
	private boolean[][] failpoints;
	private final ErrorList errorList;
	private ArrayList<Error> errors = new ArrayList<Error>();

	public DataTester(MapData data, TestResultReceiver receiver) {
		this.data = data;
		this.receiver = receiver;
		errorList = new ErrorList();
	}

	public void start() {
		new Thread(this, "data tester").start();
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				while (!retest) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				retest = false;
			}
			try {
				doTest();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void doTest() {
		successful = true;
		result = "";
		resultPosition = new ShortPoint2D(0, 0);
		errors = new ArrayList<Error>();

		failpoints = new boolean[data.getWidth()][data.getHeight()];
		byte[][] players = new byte[data.getWidth()][data.getHeight()];
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				players[x][y] = (byte) -1;
			}
		}
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingObject) {
					BuildingObject buildingObject = (BuildingObject) mapObject;
					drawBuildingCircle(players, x, y, buildingObject);
				}
			}
		}
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingObject) {
					ShortPoint2D start = new ShortPoint2D(x, y);
					BuildingObject buildingObject = (BuildingObject) mapObject;
					testBuilding(players, x, y, start, buildingObject);
				}
			}
		}

		// test resources
		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				if (data.getResourceAmount(x, y) > 0 && !mayHoldResource(data.getLandscape(x, y), data.getResourceType(x, y))) {
					testFailed("" + data.getLandscape(x, y) + "may not have " + data.getResourceType(x, y), new ShortPoint2D(x, y));
				}
			}
		}

		boolean[][] borders = new boolean[data.getWidth()][data.getHeight()];

		for (int x = 0; x < data.getWidth() - 1; x++) {
			for (int y = 0; y < data.getHeight() - 1; y++) {
				test(x, y, x + 1, y, players, borders);
				test(x, y, x + 1, y + 1, players, borders);
				test(x, y, x, y + 1, players, borders);
			}
		}

		testForBlockedMapBorders();

		for (int player = 0; player < data.getPlayerCount(); player++) {
			ShortPoint2D point = data.getStartPoint(player);
			if (players[point.x][point.y] != player) {
				testFailed("Player " + player + " has invalid start point", point);
			}
			// mark
			borders[point.x][point.y] = true;
		}

		data.setPlayers(players);
		data.setBorders(borders);
		data.setFailpoints(failpoints);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					errorList.setErrors(errors);
					receiver.testResult(result, successful, resultPosition);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void testForBlockedMapBorders() {
		int width = data.getWidth();
		int height = data.getHeight();

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				if (1 <= y && y < height - 1 && 1 <= x && x < width - 1) {
					continue;
				}

				if (!data.getLandscape(x, y).isBlocking) {
					testFailed("All border positions must be blocking!", new ShortPoint2D(x, y));
				}
			}
		}
	}

	public static boolean mayHoldResource(ELandscapeType landscape, EResourceType resourceType) {
		if (resourceType == EResourceType.FISH) {
			return landscape.isWater();
		} else {
			return landscape == ELandscapeType.MOUNTAIN || landscape == ELandscapeType.MOUNTAINBORDER;
		}
	}

	private void testBuilding(byte[][] players, int x, int y, ShortPoint2D start, BuildingObject buildingObject) {
		EBuildingType type = buildingObject.getType();
		int height = data.getLandscapeHeight(x, y);
		for (RelativePoint p : type.getProtectedTiles()) {
			ShortPoint2D pos = p.calculatePoint(start);
			if (!data.contains(pos.x, pos.y)) {
				testFailed("Building " + type + " outside map", pos);
			} else if (!MapData.listAllowsLandscape(type.getGroundtypes(), data.getLandscape(pos.x, pos.y))) {
				testFailed("Building " + type + " cannot be placed on " + data.getLandscape(pos.x, pos.y), pos);
			} else if (players[pos.x][pos.y] != buildingObject.getPlayerId()) {
				testFailed("Building " + type + " of player " + buildingObject.getPlayerId() + ", but is on " + players[x][y] + "'s land", pos);
			} else if (type.getGroundtypes()[0] != ELandscapeType.MOUNTAIN && data.getLandscapeHeight(pos.x, pos.y) != height) {
				testFailed("Building " + type + " of player " + buildingObject.getPlayerId() + " must be on flat ground", pos);
			}
		}
	}

	private void drawBuildingCircle(byte[][] players, int x, int y, BuildingObject buildingObject) {
		byte player = buildingObject.getPlayerId();
		EBuildingType type = buildingObject.getType();
		if (type == EBuildingType.TOWER || type == EBuildingType.BIG_TOWER || type == EBuildingType.CASTLE) {
			MapCircle circle = new MapCircle(x, y, CommonConstants.TOWER_RADIUS);
			drawCircle(players, player, circle);
		}
	}

	private void drawCircle(byte[][] players, byte player, MapCircle circle) {
		for (ShortPoint2D pos : circle) {
			if (data.contains(pos.x, pos.y) && players[pos.x][pos.y] == -1) {
				players[pos.x][pos.y] = player;
			}
		}
	}

	private void test(int x, int y, int x2, int y2, byte[][] players, boolean[][] borders) {
		ELandscapeType l2 = data.getLandscape(x2, y2);
		ELandscapeType l1 = data.getLandscape(x, y);
		int maxHeightDiff = getMaxHeightDiff(l1, l2);
		if (Math.abs(data.getLandscapeHeight(x2, y2) - data.getLandscapeHeight(x, y)) > maxHeightDiff) {
			testFailed("Too high landscape diff", new ShortPoint2D(x, y));
		}
		if (!fader.canFadeTo(l2, l1)) {
			testFailed("Wrong landscape pair: " + l2 + ", " + l1, new ShortPoint2D(x, y));
		}

		if (players[x][y] != players[x2][y2]) {
			if (players[x][y] != -1) {
				borders[x][y] = true;
			}
			if (players[x2][y2] != -1) {
				borders[x2][y2] = true;
			}
		}
	}

	public static int getMaxHeightDiff(ELandscapeType landscape, ELandscapeType landscape2) {
		return landscape.isWater() || landscape == ELandscapeType.MOOR || landscape == ELandscapeType.MOORINNER || landscape2.isWater()
				|| landscape2 == ELandscapeType.MOOR || landscape2 == ELandscapeType.MOORINNER ? 0 : MAX_HEIGHT_DIFF;
	}

	private void testFailed(String string, ShortPoint2D pos) {
		successful = false;
		result = string;
		resultPosition = pos;
		failpoints[pos.x][pos.y] = true;
		errors.add(new Error(pos, string));
	}

	public synchronized void retest() {
		retest = true;
		this.notifyAll();
	}

	public interface TestResultReceiver {
		public void testResult(String name, boolean allowed, ShortPoint2D resultPosition);
	}

	public ErrorList getErrorList() {
		return errorList;
	}
}
