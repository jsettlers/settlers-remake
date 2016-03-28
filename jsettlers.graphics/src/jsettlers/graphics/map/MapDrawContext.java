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
package jsettlers.graphics.map;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.util.Iterator;

import jsettlers.common.Color;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.map.draw.DrawConstants;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * This is the drawing context for a map. It is used to translate the visible screen space to local coordinate space and holds the current gl context.
 * <p>
 * This class uses 3 coordiante systems and provides conversion methods between them.
 * <ul>
 * <li>The map space. It is the map coordinate system (x, y)
 * <li>The draw space (used by all draw stuff).
 * <li>The screen space (visible space on which is drawn).
 * </ul>
 * <h2>gl setup</h2> With {@link #begin(GLDrawContext)}, the gl state machine is initialized for drawing the map. The draw coordinates can then be
 * given in draw space.
 * <h2>Draw buffer</h2> We hold a draw buffer everyone drawing with the map draw context can use. The buffer should be flushed when drawing one
 * component finished. When the draw buffer is used after a call to end(), the buffer is invalid.
 * 
 * @author Michael Zangl
 */
public final class MapDrawContext implements IGLProvider {

	private GLDrawContext gl = null;

	private final IGraphicsGrid map;

	private final ScreenPosition screen;

	private final MapCoordinateConverter converter;

	/**
	 * Those are the colors used for the players. The first color is used for the first player. If there are more players than colors, colors are
	 * re-used.
	 * <p>
	 * Color for settler player, the first 20 colors are copied from an original settler 3 editor screenshot, the other 12 colors are choosen so they
	 * are unique
	 */
	private static final Color[] PLAYER_COLORS = new Color[] {
			// Color 0 .. 19 Original settler colors
			new Color(0xFFF71000),
			new Color(0xFF108CF7),
			new Color(0xFFF7F700),
			new Color(0xFF29B552),
			new Color(0xFFF78C00),
			new Color(0xFF00F7F7),
			new Color(0xFFF700F7),
			new Color(0xFF292929),
			new Color(0xFFF7F7F7),
			new Color(0xFF0010F7),
			new Color(0xFFCE4A10),
			new Color(0xFF8C8C8C),
			new Color(0xFFAD08DE),
			new Color(0xFF006B00),
			new Color(0xFFF7BDBD),
			new Color(0xFF84EFA5),
			new Color(0xFF9C0831),
			new Color(0xFFCE8CE7),
			new Color(0xFFF7CE94),
			new Color(0xFF8CBDEF),

			// Additional 12 Colors
			new Color(0xFFBAFF45),
			new Color(0xFFCD0973),
			new Color(0xFFD9F1AF),
			new Color(0xFF6E005F),
			new Color(0xFFA3C503),
			new Color(0xFF64B3B9),
			new Color(0xFFB3F6FB),
			new Color(0xFF8E592B),
			new Color(0xFF8E882B),
			new Color(0xFFD9E0FF),
			new Color(0xFFD4D4D4),
			new Color(0xFFFF578F)
	};

	public boolean ENABLE_ORIGINAL = true;

	/**
	 * The basic draw buffer we use.
	 */
	private final DrawBuffer buffer;

	private final ReplaceableTextDrawer textDrawer;

	/**
	 * Creates a new map context for a given map.
	 * 
	 * @param map
	 *            The map.
	 * @param textDrawer
	 *            The text drawer to use.
	 */
	public MapDrawContext(IGraphicsGrid map, ReplaceableTextDrawer textDrawer) {
		this.map = map;
		this.textDrawer = textDrawer;
		float incline = DrawConstants.DISTANCE_X / 2.0f / DrawConstants.DISTANCE_Y;
		int mapHeight = map.getHeight() * DrawConstants.DISTANCE_Y;
		int mapWidth = map.getWidth() * DrawConstants.DISTANCE_X;
		this.screen = new ScreenPosition(mapWidth, mapHeight, incline);

		this.converter = MapCoordinateConverter.get(DrawConstants.DISTANCE_X,
				DrawConstants.DISTANCE_Y, map.getWidth(),
				map.getHeight());

		buffer = new DrawBuffer(this);
	}

	/**
	 * Sets the size of the context to width/height.
	 * 
	 * @param windowWidth
	 *            The width.
	 * @param windowHeight
	 *            The height.
	 */
	public void setSize(float windowWidth, float windowHeight) {
		this.screen.setSize(windowWidth, windowHeight);
	}

	/**
	 * Sets the center of the screen.
	 * 
	 * @param x
	 *            X in pixels.
	 * @param y
	 *            Y in pixels.
	 */
	public void setScreenCenter(int x, int y) {
		this.screen.setScreenCenter(x, y);
	}

	/**
	 * Begin a new draw session (=> draw a new image). Sets up the gl screen assuming the current viewport is set to (0,0,width,height)
	 * 
	 * @param gl2
	 *            The gl context to use.
	 * @see #end()
	 */
	public void begin(GLDrawContext gl2) {
		this.gl = gl2;

		// beginTime = System.nanoTime();

		gl2.glPushMatrix();
		float zoom = screen.getZoom();
		gl2.glScalef(zoom, zoom, 1);
		gl2.glTranslatef((int) -this.screen.getLeft() + .5f,
				(int) -this.screen.getBottom() + .5f, 0);
	}

	/**
	 * Ends a drawing session.
	 */
	public void end() {
		this.gl.glPopMatrix();
		this.gl = null;
	}

	/**
	 * Gets the current gl context, of <code>null</code> if it is called outside a gl drawing session.
	 * 
	 * @return The gl context that was given to {@link #begin(GLDrawContext)}
	 */
	@Override
	public GLDrawContext getGl() {
		return this.gl;
	}

	/**
	 * Gets the current draw buffer used for this context. You can add draws to this buffer instead of directly calling OpenGL since this object
	 * buffers the calls.
	 * 
	 * @return The buffer.
	 */
	public DrawBuffer getDrawBuffer() {
		return buffer;
	}

	/**
	 * Gets the text drawer for the draw context. Use this method instead of the opengl one, because we might override it.
	 * 
	 * @param size
	 *            The text size
	 * @return A text drawer.
	 */
	public TextDrawer getTextDrawer(EFontSize size) {
		return textDrawer.getTextDrawer(gl, size);
	}

	/**
	 * Gets the region of the draw space that is drawn on the screen and therefore rendered.
	 * 
	 * @return The region displayed on the screen as Rectangle.
	 */
	public ScreenPosition getScreen() {
		return this.screen;
	}

	/**
	 * @param x
	 *            The x coordinate in draw space
	 * @param y
	 *            The y coordinate in draw space.
	 * @return The map position under the point.
	 */
	public ShortPoint2D getPositionUnder(float screenx, float screeny) {
		ShortPoint2D currentPoint = converter.getMap(screenx, screeny);
		UIPoint desiredOnScreen = new UIPoint(screenx, screeny);

		UIPoint onscreen = converter.getView(currentPoint.x, currentPoint.y,
				getHeight(currentPoint.x, currentPoint.y));
		double currentbest = onscreen.distance(desiredOnScreen);

		boolean couldBeImproved;
		do {
			couldBeImproved = false;

			for (ShortPoint2D p : new MapNeighboursArea(currentPoint)) {
				onscreen = converter.getView(p.x, p.y, getHeight(p.x, p.y));
				double newDistance = onscreen.distance(desiredOnScreen);
				if (newDistance < currentbest) {
					currentbest = newDistance;
					currentPoint = p;
					couldBeImproved = true;
				}
			}

		} while (couldBeImproved);

		return currentPoint;
	}

	/**
	 * @param x
	 *            The x coordinate in screen space
	 * @param y
	 *            The y coordinate in screen space.
	 * @return The map position under the screen point.
	 */
	public ShortPoint2D getPositionOnScreen(float x, float y) {
		return getPositionUnder(
				x / this.screen.getZoom() + this.screen.getLeft(), y
						/ this.screen.getZoom() + this.screen.getBottom());
	}

	/**
	 * Checks two map coordiantes if they are on the map.
	 * 
	 * @param x
	 *            The y coordinate in map space.
	 * @param y
	 *            The x coordinate in map space.
	 * @return If the map coordinates are on the map.
	 */
	public boolean checkMapCoordinates(int x, int y) {
		return x >= 0 && x < this.map.getWidth() && y >= 0
				&& y < this.map.getHeight();
	}

	/**
	 * Gets the color for a given player.
	 * 
	 * @param player
	 *            The player to get the color for.
	 * @return The color.
	 */
	public Color getPlayerColor(byte player) {
		if (player >= 0) {
			return PLAYER_COLORS[player % PLAYER_COLORS.length];
		} else {
			return Color.BLACK;
		}
	}

	/**
	 * Gets the converter for the map coordinate system to screen coordinates.
	 * 
	 * @return The map coordinate converter.
	 */
	public MapCoordinateConverter getConverter() {
		return this.converter;
	}

	/**
	 * sets up the gl drawing context to draw a given tile.
	 * 
	 * @param x
	 *            The tile to draw.
	 * @param y
	 *            The tile to draw.
	 */
	public void beginTileContext(int x, int y) {
		this.gl.glPushMatrix();
		int height = getHeight(x, y);
		this.gl.glTranslatef(this.converter.getViewX(x, y, height),
				this.converter.getViewY(x, y, height), 0);
	}

	/**
	 * Assumes that the user begun drawing a tile recently, and ends drawing the tile. This also resets the view matrix to the one before starting to
	 * draw.
	 */
	public void endTileContext() {
		this.gl.glPopMatrix();
	}

	/**
	 * Sets up drawing between two tiles. This is e.g. used to draw walking settlers.
	 * 
	 * @param startx
	 *            The start tile
	 * @param starty
	 *            The start tile
	 * @param destinationx
	 *            The second tile
	 * @param destinationy
	 *            The second tile
	 * @param progress
	 *            The progress between those two bytes.
	 */
	public void beginBetweenTileContext(int startx, int starty,
			int destinationx, int destinationy, float progress) {
		this.gl.glPushMatrix();
		float theight = getHeight(startx, starty);
		float dheight = getHeight(destinationx, destinationy);
		float x = (1 - progress)
				* this.converter.getViewX(startx, starty, theight)
				+ progress
				* this.converter.getViewX(destinationx, destinationy,
						dheight);
		float y = (1 - progress)
				* this.converter.getViewY(startx, starty, theight)
				+ progress
				* this.converter.getViewY(destinationx, destinationy,
						dheight);
		this.gl.glTranslatef(x, y, 0);
	}

	/**
	 * Converts a screen rectangle to an area on the map. Map heights are respected.
	 * 
	 * @param x1
	 *            one x (not ordered)
	 * @param y1
	 *            one y
	 * @param x2
	 *            an other x
	 * @param y2
	 *            an other y
	 * @return The rectangle on the map
	 */
	public IMapArea getRectangleOnScreen(int x1, int y1, int x2, int y2) {
		float drawx1 = x1 / this.screen.getZoom() + this.screen.getLeft();
		float drawx2 = x2 / this.screen.getZoom() + this.screen.getLeft();
		float drawy1 = y1 / this.screen.getZoom() + this.screen.getBottom();
		float drawy2 = y2 / this.screen.getZoom() + this.screen.getBottom();
		return new HeightedMapRectangle(new FloatRectangle(drawx1, drawy1,
				drawx2, drawy2));
	}

	/**
	 * This class represents an area of the map that looks rectangular on the screen. Due to height differences, this is not a rectangle on the map.
	 * 
	 * @author Michael Zangl
	 *
	 */
	private class HeightedMapRectangle implements IMapArea {
		/**
		 * Note: This class is nor serializeable.
		 */
		private static final long serialVersionUID = 5868822981883722458L;

		/**
		 * Helper rectangle.
		 */
		private final MapRectangle base;
		private final FloatRectangle drawRect;

		/**
		 * Creates a new IMapArea that contains the points that are in the rectangle on the screen.
		 * 
		 * @param drawRect
		 *            The rectangle in draw space
		 */
		HeightedMapRectangle(FloatRectangle drawRect) {
			this.drawRect = drawRect;
			base = converter.getMapForScreen(drawRect);
		}

		@Override
		public boolean contains(ShortPoint2D point) {
			int height = getHeight(point.x, point.y);
			float x = converter.getViewX(point.x, point.y, height);
			float y = converter.getViewY(point.x, point.y, height);
			return drawRect.contains(x, y);
		}

		@Override
		public Iterator<ShortPoint2D> iterator() {
			return new ScreenIterator();
		}

		/**
		 * This class iterates over a {@link HeightedMapRectangle}.
		 * 
		 * @author Michael Zangl
		 *
		 */
		private final class ScreenIterator implements Iterator<ShortPoint2D> {
			/**
			 * How many lines to search at least.
			 */
			private static final int MIN_SEARCH_LINES = 20;
			private ShortPoint2D next;
			private int currentLine = 0;
			private int currentX;

			private ScreenIterator() {
				currentX = base.getLineStartX(0);
				next = new ShortPoint2D(currentX, base.getLineY(0));
				if (!contains(next)) {
					next = searchNext();
				}
			}

			private ShortPoint2D searchNext() {
				int startLine = currentLine;
				while (startLine >= currentLine - 2
						|| currentLine < MIN_SEARCH_LINES) {
					currentX++;
					if (currentX > base.getLineEndX(currentLine)) {
						currentLine++;
						currentX = base.getLineStartX(currentLine);
					}
					ShortPoint2D point = new ShortPoint2D(currentX,
							base.getLineY(currentLine));
					if (contains(point)) {
						return point;
					}
				}
				return null;
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public ShortPoint2D next() {
				ShortPoint2D ret = next;
				next = searchNext();
				return ret;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}

	}

	/**
	 * Gets the area of the screen.
	 * 
	 * @return An rectangle of the map in which the screen lies completely. The rectangle can be bigger than the screen.
	 */
	public MapRectangle getScreenArea() {
		return this.converter.getMapForScreen(this.screen.getPosition());
	}

	/**
	 * Move the view center to a given point.
	 * 
	 * @param point
	 *            The point to move the view to.
	 */
	public void scrollTo(ShortPoint2D point) {
		int height = getHeight(point.x, point.y);
		float x = converter.getViewX(point.x, point.y, height);
		float y = converter.getViewY(point.x, point.y, height);
		screen.setScreenCenter(x, y);
	}

	/**
	 * Gets the landscape at a given position.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return The landscape type.
	 */
	public ELandscapeType getLandscape(int x, int y) {
		return map.getLandscapeTypeAt(x, y);
	}

	public int getHeight(int x, int y) {
		if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
			return map.getHeightAt(x, y);
		} else {
			return 0;
		}
	}

	public byte getVisibleStatus(int x, int y) {
		return map.getVisibleStatus(x, y);
	}

	public IGraphicsGrid getMap() {
		return map;
	}

}
