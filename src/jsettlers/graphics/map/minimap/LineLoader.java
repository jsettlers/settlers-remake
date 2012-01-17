package jsettlers.graphics.map.minimap;

import go.graphics.Color;

import java.nio.ShortBuffer;

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

	public LineLoader(Minimap minimap) {
		this.minimap = minimap;
	}

	@Override
	public void run() {
		while (true) {
			updateLine();
		}
	};

	/**
	 * Updates a line by putting it to the update buffer. Next time the gl
	 * context is available, it is updated.
	 */
	private void updateLine() {
		int currentline = minimap.getNextUpdateLine();
		ShortBuffer newData = getLineData(currentline);
		newData.position(0);
		minimap.setUpdateData(currentline, newData);
	}

	private ShortBuffer getLineData(int currentline) {
		// may change!
		int safewidth = this.minimap.getWidth();
		int safeheight = this.minimap.getHeight();
		IGraphicsGrid map = this.minimap.getContext().getMap();

		//for height shades
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

		ShortBuffer data = ShortBuffer.allocate(safewidth);
		for (int x = 0; x < safewidth; x++) {
			int mapminx = (int) ((float) x / safewidth * map.getWidth());
			int mapmaxx = (int) ((float) (x + 1) / safewidth * map.getWidth());

			if (mapminx != 0 && mapmaxx == mapminx) {
				mapminx = mapmaxx - 1;
			}
			int centerx = (mapmaxx + mapminx) / 2;
			int centery = (mapmaxy + mapminy) / 2;

			short color = TRANSPARENT;
			if (minimap.getContext().isFogOfWarVisible(centerx, centery)) {
				color = getSettlerForArea(mapminx, mapminy, mapmaxx, mapmaxy);
			}

			if (color == TRANSPARENT) {
				float basecolor =
				        (float) minimap.getContext().getVisibleStatus(centerx,
				                centery)
				                / CommonConstants.FOG_OF_WAR_VISIBLE;
				int dheight =
				        map.getHeightAt(centerx, mapminy)
				                - map.getHeightAt(centerx, Math.min(mapminy + maplineheight, map.getHeight() - 1));
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
			data.put(color);
		}
		return data;
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
		return toShortColor(landscape.getColor(), basecolor);
	}

	private static short toShortColor(Color color, float multiply) {
		return (short) ((int) (Math.min(1, color.getRed() * multiply) * 0x1f) << 11
		        | (int) (Math.min(1, color.getGreen() * multiply) * 0x1f) << 6
		        | (int) (Math.min(1, color.getBlue() * multiply) * 0x1f) << 1 | 1);
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
					color = toShortColor(playerColor, 1);
				}
			}
		}
		return color;
	}

	private short getColor(IMovable settler) {
		return toShortColor(
		        minimap.getContext().getPlayerColor(settler.getPlayer()), 1);
	}
}