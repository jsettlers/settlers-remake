package jsettlers.graphics.map.minimap;

import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jsettlers.common.map.IHexMap;
import jsettlers.common.map.IHexTile;

/**
 * This is the minimap view.
 * 
 * @author michael
 */
public class MiniMap {
	// private final MapCoordinateConverter coordinates;

	private final short[] colors;
	private final IHexMap map;
	private final int height;
	private final int width;
	private final int[] textures = new int[1];

	/**
	 * Displays the given map as mini map.
	 * 
	 * @param map
	 *            The map to display
	 * @param width
	 *            The width the map should have on its base.
	 * @param height
	 *            The height of the map.
	 */
	public MiniMap(IHexMap map, int width, int height) {
		this.map = map;
		this.width = width;
		this.height = height;
		// float realWidth = width + .5f * height;
		// coordinates = new MapCoordinateConverter(map.getWidth(),
		// map.getHeight(), realWidth, height);
		this.colors = new short[width * height];

		reload();
	}

	/**
	 * Draws the minimap with left,bottom as lower left corner.
	 * 
	 * @param gl
	 *            The gl context.
	 * @param left
	 *            the left corner
	 * @param bottom
	 *            the bottom corner
	 */
	public void drawAt(GL2 gl, int left, int bottom) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		if (this.textures[0] == 0) {
			gl.glGenTextures(1, this.textures, 0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, this.textures[0]);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
			        GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
			        GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
			        GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
			        GL.GL_NEAREST);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, this.width,
			        this.height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_SHORT_5_5_5_1,
			        ShortBuffer.wrap(this.colors));

		}
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.textures[0]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(left, bottom);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(left + this.width, bottom);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(left + this.width, bottom + this.height);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(left, bottom + this.height);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	private void reload() {
		for (int y = 0; y < this.height; y++) {
			reloadLine(y);
		}
	}

	/**
	 * Relaads an image line
	 * 
	 * @param y
	 *            The y coordinate in view.
	 */
	private void reloadLine(int line) {
		short mapy =
		        (short) ((this.height - line - 1)
		                * (float) this.map.getHeight() / this.height);
		float scalex = (float) this.map.getWidth() / this.width;
		for (int x = 0; x < this.width; x++) {
			short mapx = (short) (x * scalex);
			IHexTile tile = this.map.getTile(mapx, mapy);
			this.colors[line * this.width + x] = getColorForTile(tile);
		}
	}

	private short getColorForTile(IHexTile tile) {
		int color = 0;
		switch (tile.getLandscapeType()) {
			case WATER:
				color = 0x0027;
				break;

			case SNOW:
				color = 0xffff;
				break;

			case MOUNTAIN:
				color = 0x8421;
				break;

			case SAND:
			case DESERT:
				color = 0x8401;
				break;

			default:
				color = 0x0481;
				break;
		}

		return (short) color;
	}
}
