package jsettlers.graphics.map.minimap;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.movable.IMovable;

class LineLoader implements Runnable {
	private static final short TRANSPARENT = 0;

	private static final short BLACK = 0x01;

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
			updateLine();
		}
	};

	private int currYOffset = 0;
	private int currXOffset = 0;
	private static final int Y_STEP_HEIGHT = 5;
	private static final int X_STEP_WIDTH = 5;

	private static final int LINES_PER_RUN = 30;

	private short[][] buffer = new short[1][1];

	/**
	 * Updates a line by putting it to the update buffer. Next time the gl
	 * context is available, it is updated.
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
			}

			calculateLineData(currentline);
			minimap.setUpdatedLine(currentline);

			currentline += Y_STEP_HEIGHT;
			if (currentline >= minimap.getHeight()) {
				currYOffset++;
				currYOffset %= Y_STEP_HEIGHT;

				currentline = currYOffset;
			}
		}
	}

	private void calculateLineData(final int currentline) {
		// may change!
		int safewidth = this.minimap.getWidth();
		int safeheight = this.minimap.getHeight();
		IGraphicsGrid map = this.minimap.getContext().getMap();

		// for height shades
		int maplineheight = map.getHeight() / safeheight + 1;

		// first map tile in line
		int mapmaxy =
		        (int) ((1 - (float) currentline / safeheight) * map.getHeight());
		// first map line not in line
		int mapminy =
		        (int) ((1 - (float) (currentline + 1) / safeheight) * map
		                .getHeight());
		if (mapminy == mapmaxy) {
			if (mapmaxy == map.getHeight()) {
				mapminy = map.getHeight() - 1;
			} else {
				mapmaxy = mapminy - 1;
			}
		}

		for (int x = currXOffset; x < safewidth; x += 5) {
			int mapminx = (int) ((float) x / safewidth * map.getWidth());
			int mapmaxx = (int) ((float) (x + 1) / safewidth * map.getWidth());

			if (mapminx != 0 && mapmaxx == mapminx) {
				mapminx = mapmaxx - 1;
			}
			int centerx = (mapmaxx + mapminx) / 2;
			int centery = (mapmaxy + mapminy) / 2;

			short color = TRANSPARENT;
			if (minimap.getContext().getMap()
			        .getVisibleStatus(centerx, centery) > CommonConstants.FOG_OF_WAR_EXPLORED) {
				color = getSettlerForArea(mapminx, mapminy, mapmaxx, mapmaxy);
			}

			if (color == TRANSPARENT) {
				float basecolor =
				        (float) minimap.getContext().getVisibleStatus(centerx,
				                centery)
				                / CommonConstants.FOG_OF_WAR_VISIBLE;
				int dheight =
				        map.getHeightAt(centerx, mapminy)
				                - map.getHeightAt(
				                        centerx,
				                        Math.min(mapminy + maplineheight,
				                                map.getHeight() - 1));
				basecolor *= (1 + .15f * dheight);

				if (basecolor >= 0) {
					color =
					        getLandscapeForArea(mapminx, mapminy, mapmaxx,
					                mapmaxy, basecolor);
				}
			}
			if (color == TRANSPARENT) {
				color = BLACK;
			}
			buffer[currentline][x] = color;
		}

		currXOffset += 3;
		currXOffset %= X_STEP_WIDTH;
	}

	private short getLandscapeForArea(int mapminx, int mapminy, int mapmaxx,
	        int mapmaxy, float basecolor) {
		int centerx = (mapmaxx + mapminx) / 2;
		int centery = (mapmaxy + mapminy) / 2;

		IGraphicsGrid map = this.minimap.getContext().getMap();
		return getColorForLandscape(map.getLandscapeTypeAt(centerx, centery),
		        basecolor);
	}

	private static short getColorForLandscape(ELandscapeType landscape,
	        float basecolor) {
		return landscape.getColor().toShortColor(basecolor);
	}

	private short getSettlerForArea(int mapminx, int mapminy, int mapmaxx,
	        int mapmaxy) {
		short color = TRANSPARENT;
		IGraphicsGrid map = this.minimap.getContext().getMap();
		for (int y = mapminy; y < mapmaxy && color == TRANSPARENT; y++) {
			for (int x = mapminx; x < mapmaxx && color == TRANSPARENT; x++) {
				IMovable settler = map.getMovableAt(x, y);
				if (settler != null) {
					color = getColor(settler);
				} else if (map.isBorder(x, y)) {
					byte player = map.getPlayerAt(x, y);
					Color playerColor =
					        minimap.getContext().getPlayerColor(player);
					color = playerColor.toShortColor(1);
				}
			}
		}
		return color;
	}

	private short getColor(IMovable settler) {
		return minimap.getContext().getPlayerColor(settler.getPlayer())
		        .toShortColor(1);
	}

	/**
	 * Stops the execution of this line loader.
	 */
	public void stop() {
		stopped = true;
	}
}