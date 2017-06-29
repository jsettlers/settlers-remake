/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.graphics.map.minimap;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.minimap.MinimapMode.OccupiedAreaMode;
import jsettlers.graphics.map.minimap.MinimapMode.SettlersMode;

/**
 * This class does the minimap line loading without knowing how to store the data.
 * 
 * @author Michael Zangl
 */
public abstract class AbstractLineLoader implements Runnable {
	protected static final short BLACK = Color.BLACK.toShortColor(1);
	protected static final short TRANSPARENT = 0;
	private static final int Y_STEP_HEIGHT = 5;
	private static final int X_STEP_WIDTH = 5;
	private static final int LINES_PER_RUN = 30;

	private int currentLine = 0;
	private boolean stopped;
	private int workingMinimapWidth = -1;
	private int workingMinimapHeight = -1;
	private int currYOffset = 0;
	private int currXOffset = 0;

	private final MinimapMode modeSettings;
	protected final IMinimapData minimapData;
	/**
	 * The explored landscape.
	 */
	private short[][] landscape = new short[1][1];

	public AbstractLineLoader(IMinimapData minimapData, MinimapMode modeSettings) {
		this.minimapData = minimapData;
		this.modeSettings = modeSettings;
		landscape[0][0] = TRANSPARENT;
	}

	@Override
	public void run() {
		while (!stopped) {
			try {
				updateLine();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates a line by putting it to the update buffer. Next time the gl context is available, it is updated.
	 */
	private void updateLine() {
		minimapData.blockUntilUpdateAllowedOrStopped();
		for (int i = 0; i < LINES_PER_RUN; i++) {
			int width = minimapData.getWidth();
			int height = minimapData.getHeight();
			if (workingMinimapWidth != width || workingMinimapHeight != height) {
				workingMinimapWidth = width;
				workingMinimapHeight = height;
				resizeBuffer(width, height);
				resizeBackground(width, height);
				currentLine = 0;
				currXOffset = 0;
				currYOffset = 0;
			}

			calculateLineData(currentLine);
			markLineUpdate(currentLine);

			currentLine += Y_STEP_HEIGHT;
			if (currentLine >= workingMinimapHeight) {
				currYOffset++;
				if (currYOffset >= Y_STEP_HEIGHT) {
					currYOffset = 0;
					currXOffset += 3;
					currXOffset %= X_STEP_WIDTH;
				}

				currentLine = currYOffset;
			}
		}
	}

	private void resizeBackground(int width, int height) {
		short[][] oldLandscape = landscape;
		landscape = new short[height][width];
		int oldHeight = oldLandscape.length;
		int oldWidth = oldLandscape[0].length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int oldY = Math.round((float) y * oldHeight / height);
				int oldX = Math.round((float) x * oldWidth / width);
				landscape[y][x] = oldLandscape[Math.min(oldY, oldHeight - 1)][Math.min(oldX, oldWidth - 1)];
			}
		}
	}

	protected abstract void markLineUpdate(int line);

	protected abstract void resizeBuffer(int width, int height);

	private void calculateLineData(final int currentline) {
		// may change!
		final int safeWidth = workingMinimapWidth;
		final int safeHeight = workingMinimapHeight;
		final MapDrawContext context = minimapData.getContext();
		final IGraphicsGrid map = context.getMap();

		// for height shades
		final short mapWidth = map.getWidth();
		final short mapHeight = map.getHeight();

		int mapLineHeight = mapHeight / safeHeight + 1;

		// first map tile in line
		int mapMaxY = (int) ((1 - (float) currentline / safeHeight) * mapHeight);
		// first map line not in line
		int mapMinY = (int) ((1 - (float) (currentline + 1) / safeHeight) * mapHeight);
		if (mapMinY == mapMaxY) {
			if (mapMaxY == mapHeight) {
				mapMinY = mapHeight - 1;
			} else {
				mapMaxY = mapMinY - 1;
			}
		}

		int myXOffset = (currXOffset + currentline * 3) % X_STEP_WIDTH;

		for (int x = myXOffset; x < safeWidth; x += X_STEP_WIDTH) {
			int mapMinX = (int) ((float) x / safeWidth * mapWidth);
			int mapMaxX = (int) ((float) (x + 1) / safeWidth * mapWidth);

			if (mapMinX != 0 && mapMaxX == mapMinX) {
				mapMinX = mapMaxX - 1;
			}
			int centerX = (mapMaxX + mapMinX) / 2;
			int centerY = (mapMaxY + mapMinY) / 2;

			short color = TRANSPARENT;
			byte visibleStatus = map.getVisibleStatus(centerX, centerY);
			if (visibleStatus > CommonConstants.FOG_OF_WAR_EXPLORED) {
				color = getSettlerForArea(map, context, mapMinX, mapMinY, mapMaxX, mapMaxY);
			}

			if (visibleStatus > CommonConstants.FOG_OF_WAR_EXPLORED || landscape[currentline][x] == TRANSPARENT) {
				float basecolor = ((float) visibleStatus) / CommonConstants.FOG_OF_WAR_VISIBLE;
				int dheight = map.getHeightAt(centerX, mapMinY) - map.getHeightAt(centerX, Math.min(mapMinY + mapLineHeight, mapHeight - 1));
				basecolor *= 1 + .15f * dheight;

				short landscapeColor;
				if (basecolor >= 0) {
					landscapeColor = getColorForArea(map, mapMinX, mapMinY, mapMaxX, mapMaxY).toShortColor(basecolor);
				} else {
					landscapeColor = BLACK;
				}
				if (color == TRANSPARENT) {
					color = landscapeColor;
				}
				landscape[currentline][x] = landscapeColor;
			}

			if (color == TRANSPARENT) {
				color = landscape[currentline][x];
			}
			setBuffer(currentline, x, color);
		}
	}

	protected abstract void setBuffer(int currentline, int x, short color);

	private Color getColorForArea(IGraphicsGrid map, int mapminX, int mapminY, int mapmaxX, int mapmaxY) {
		int centerx = (mapmaxX + mapminX) / 2;
		int centery = (mapmaxY + mapminY) / 2;

		return map.getLandscapeTypeAt(centerx, centery).color;
	}

	private short getSettlerForArea(IGraphicsGrid map, MapDrawContext context, int mapminX, int mapminY, int mapmaxX, int mapmaxY) {
		SettlersMode displaySettlers = this.modeSettings.getDisplaySettlers();
		OccupiedAreaMode displayOccupied = this.modeSettings.getDisplayOccupied();
		boolean displayBuildings = this.modeSettings.getDisplayBuildings();

		short occupiedColor = TRANSPARENT;
		short settlerColor = TRANSPARENT;
		short buildingColor = TRANSPARENT;

		for (int y = mapminY; y < mapmaxY && (displayOccupied != OccupiedAreaMode.NONE || displayBuildings || displaySettlers != SettlersMode.NONE); y++) {
			for (int x = mapminX; x < mapmaxX
					&& (displayOccupied != OccupiedAreaMode.NONE || displayBuildings || displaySettlers != SettlersMode.NONE); x++) {
				boolean visible = map.getVisibleStatus(x, y) > CommonConstants.FOG_OF_WAR_EXPLORED;
				if (visible && displaySettlers != SettlersMode.NONE) {
					IMovable settler = map.getMovableAt(x, y);
					if (settler != null && (displaySettlers == SettlersMode.ALL || settler.getMovableType().isPlayerControllable())) {
						settlerColor = context.getPlayerColor(settler.getPlayer().getPlayerId()).toShortColor(1);
						// don't search any more.
						displaySettlers = SettlersMode.NONE;
					} else if (displaySettlers != SettlersMode.NONE) {
						IMapObject object = map.getMapObjectsAt(x, y);
						IBuilding building = (object != null) ? (IBuilding) object.getMapObject(EMapObjectType.BUILDING) : null;

						if (building instanceof IBuilding.IOccupied) {
							IBuilding.IOccupied occupyed = (IBuilding.IOccupied) building;
							if (occupyed.isOccupied()) {
								settlerColor = context.getPlayerColor(occupyed.getPlayer().getPlayerId()).toShortColor(1);
							}
						}
					}
				}

				if (visible && displayOccupied == OccupiedAreaMode.BORDERS) {
					if (map.isBorder(x, y)) {
						byte player = map.getPlayerIdAt(x, y);
						Color playerColor = context.getPlayerColor(player);
						occupiedColor = playerColor.toShortColor(1);
						displayOccupied = OccupiedAreaMode.NONE;
					}

				} else if (visible && displayOccupied == OccupiedAreaMode.AREA) {
					byte player = map.getPlayerIdAt(x, y);
					if (player >= 0 && !map.getLandscapeTypeAt(x, y).isBlocking) {
						Color playerColor = context.getPlayerColor(player);
						// Now add a landscape below that....
						Color landscape = getColorForArea(map, mapminX, mapminY, mapmaxX, mapmaxY);
						playerColor = landscape.toGreyScale().multiply(playerColor);
						occupiedColor = playerColor.toShortColor(1);
						displayOccupied = OccupiedAreaMode.NONE;
					}
				}

				if (displayBuildings) {
					if (map.isBuilding(x, y)) {
						buildingColor = BLACK;
					}
				}
			}
		}
		return settlerColor != TRANSPARENT ? settlerColor : buildingColor != TRANSPARENT ? buildingColor : occupiedColor;
	}

	/**
	 * Stops the execution of this line loader.
	 */
	public void stop() {
		stopped = true;
	}
}
