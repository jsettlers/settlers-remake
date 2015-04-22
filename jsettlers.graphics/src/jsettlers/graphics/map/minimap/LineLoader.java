package jsettlers.graphics.map.minimap;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.movable.IMovable;
import jsettlers.graphics.map.MapDrawContext;

class LineLoader implements Runnable {
	private static final short TRANSPARENT = 0;

	protected static final short BLACK = 0x0001;

	/**
     *
     */
	private final Minimap minimap;

	private int currentline = 0;

	private boolean stopped;

	public LineLoader(Minimap minimap) {
		this.minimap = minimap;
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
	};

	private int currYOffset = 0;
	private int currXOffset = 0;
	private static final int Y_STEP_HEIGHT = 5;
	private static final int X_STEP_WIDTH = 5;

	private static final int LINES_PER_RUN = 30;

	private short[][] buffer = new short[1][1];

	/**
	 * Updates a line by putting it to the update buffer. Next time the gl context is available, it is updated.
	 */
	private void updateLine() {
		minimap.blockUntilUpdateAllowedOrStopped();
		for (int i = 0; i < LINES_PER_RUN; i++) {

			if (buffer.length != minimap.getHeight()
					|| buffer[currentline].length != minimap.getWidth()) {
				buffer = new short[minimap.getHeight()][minimap.getWidth()];
				for (int y = 0; y < minimap.getHeight(); y++) {
					for (int x = 0; x < minimap.getWidth(); x++) {
						buffer[y][x] = BLACK;
					}
				}
				minimap.setBufferArray(buffer);
				currentline = 0;
			}

			calculateLineData(currentline);
			minimap.setUpdatedLine(currentline);

			currentline += Y_STEP_HEIGHT;
			if (currentline >= minimap.getHeight()) {
				currYOffset++;
				if (currYOffset > Y_STEP_HEIGHT) {
					currYOffset = 0;
					currXOffset += 3;
					currXOffset %= X_STEP_WIDTH;
				}

				currentline = currYOffset;
			}
		}
	}

	private void calculateLineData(final int currentline) {
		// may change!
		final int safeWidth = this.minimap.getWidth();
		final int safeHeight = this.minimap.getHeight();
		final MapDrawContext context = this.minimap.getContext();
		final IGraphicsGrid map = context.getMap();

		// for height shades
		final short mapWidth = map.getWidth();
		final short mapHeight = map.getHeight();

		int mapLineHeight = mapHeight / safeHeight + 1;

		// first map tile in line
		int mapMaxY =
				(int) ((1 - (float) currentline / safeHeight) * mapHeight);
		// first map line not in line
		int mapMinY =
				(int) ((1 - (float) (currentline + 1) / safeHeight) * mapHeight);
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
			if (map.getVisibleStatus(centerX, centerY) > CommonConstants.FOG_OF_WAR_EXPLORED) {
				color =
						getSettlerForArea(map, context, mapMinX, mapMinY,
								mapMaxX, mapMaxY);
			}

			if (color == TRANSPARENT) {
				float basecolor =
						((float) map.getVisibleStatus(centerX, centerY))
								/ CommonConstants.FOG_OF_WAR_VISIBLE;
				int dheight =
						map.getHeightAt(centerX, mapMinY)
								- map.getHeightAt(centerX, Math.min(mapMinY
										+ mapLineHeight, mapHeight - 1));
				basecolor *= (1 + .15f * dheight);

				if (basecolor >= 0) {
					color =
							getLandscapeForArea(map, mapMinX, mapMinY, mapMaxX,
									mapMaxY, basecolor);
				}
			}

			if (color == TRANSPARENT) {
				color = BLACK;
			}

			buffer[currentline][x] = color;
		}

	}

	private static short getLandscapeForArea(IGraphicsGrid map, int mapminx,
			int mapminy, int mapmaxx, int mapmaxy, float basecolor) {
		int centerx = (mapmaxx + mapminx) / 2;
		int centery = (mapmaxy + mapminy) / 2;

		ELandscapeType landscapeType = map.getLandscapeTypeAt(centerx, centery);

		return landscapeType.color.toShortColor(basecolor);
	}

	private static short getSettlerForArea(IGraphicsGrid map,
			MapDrawContext context, int mapminx, int mapminy, int mapmaxx,
			int mapmaxy) {
		short color = TRANSPARENT;

		for (int y = mapminy; y < mapmaxy && color == TRANSPARENT; y++) {
			for (int x = mapminx; x < mapmaxx && color == TRANSPARENT; x++) {
				IMovable settler = map.getMovableAt(x, y);
				if (settler != null) {
					color =
							context.getPlayerColor(settler.getPlayerId())
									.toShortColor(1);
				} else if (map.isBorder(x, y)) {
					byte player = map.getPlayerIdAt(x, y);
					Color playerColor = context.getPlayerColor(player);
					color = playerColor.toShortColor(1);
				}
			}
		}
		return color;
	}

	/**
	 * Stops the execution of this line loader.
	 */
	public void stop() {
		stopped = true;
	}
}