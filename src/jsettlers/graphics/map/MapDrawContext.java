package jsettlers.graphics.map;

import java.util.Iterator;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.Color;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.draw.DrawConstants;
import jsettlers.graphics.map.draw.FogOfWar;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * This is the drawing context for a map. It is used to translate the visible
 * screen space to local coordinate space and holds the current gl context.
 * <p>
 * This class uses 3 coordiante systems and provides conversion methods between
 * them.
 * <ul>
 * <li>The map space. It is the map coordinate system (x, y)
 * <li>The draw space (used by all draw stuff).
 * <li>The screen space (visible space on which is drawn).
 * <ul>
 * <h2>gl setup</h2> With {@link #begin(GLDrawContext)}, the gl state machine is
 * initialized for drawing the map. The draw coordinates can then be given in
 * draw space.
 * 
 * @author michael
 */
public class MapDrawContext {

	// private static final byte HEIGHT_FACTOR = 4;

	private GLDrawContext gl = null;

	private final IGraphicsGrid map;

	private ScreenPosition screen;

	private MapCoordinateConverter converter;
	Color[] playerColors = new Color[] {
	        // red
	        new Color(0xe50000),
	        // blue
	        new Color(0x0343df),
	        // green
	        new Color(0x15b01a),
	        // yellow
	        new Color(0xffff14),
	        // purple
	        new Color(0x7e1e9c),
	        // teal
	        new Color(0x029386),
	        // orange
	        new Color(0xf97306),
	        // magenta
	        new Color(0xc20078),
	        // grey
	        new Color(0x929591),
	        // violet
	        new Color(0x9a0eea),
	        // olive
	        new Color(0x6e750e),
	};

	private final FogOfWar fogOfWar;

	private float zoom;

	// private long beginTime;

	/**
	 * Creates a new map context for a given map.
	 * 
	 * @param map
	 *            The map.
	 */
	public MapDrawContext(IGraphicsGrid map) {
		this.map = map;
		float incline =
		        DrawConstants.DISTANCE_X / 2.0f / DrawConstants.DISTANCE_Y;
		int mapHeight = map.getHeight() * DrawConstants.DISTANCE_Y;
		int mapWidth = map.getWidth() * DrawConstants.DISTANCE_X;
		this.screen = new ScreenPosition(mapWidth, mapHeight, incline);

		this.converter =
		        MapCoordinateConverter.get(DrawConstants.DISTANCE_X,
		                DrawConstants.DISTANCE_Y, map.getWidth(),
		                map.getHeight());

		this.fogOfWar = new FogOfWar(map);
	}

	/**
	 * Sets the size of the context to width/height.
	 * 
	 * @param newWidth
	 *            The width.
	 * @param newHeight
	 *            The height.
	 * @param zoom
	 */
	public void setSize(float windowWidth, float windowHeight, float zoom) {
		this.zoom = zoom;
		float newWidth = windowWidth / zoom;
		float newHeight = windowHeight / zoom;
		this.screen.setSize(newWidth, newHeight);
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
	 * Begin a new draw session (=> draw a new image). Sets up the gl screen
	 * assuming the current viewport is set to (0,0,width,height)
	 * 
	 * @param gl2
	 *            The gl context to use.
	 * @see #end()
	 */
	public void begin(GLDrawContext gl2) {
		this.gl = gl2;

		// beginTime = System.nanoTime();

		gl2.glPushMatrix();
		gl2.glScalef(zoom, zoom, 1);
		gl2.glTranslatef(-this.screen.getLeft(), -this.screen.getBottom(), 0);
	}

	/**
	 * Ends a drawing session.
	 */
	public void end() {
		this.gl.glPopMatrix();
		this.gl = null;
	}

	/**
	 * Gets the current gl context, of <code>null</code> if it is called outside
	 * a gl drawing session.
	 * 
	 * @return The gl context that was given to {@link #begin(GLDrawContext)}
	 */
	public GLDrawContext getGl() {
		return this.gl;
	}

	/**
	 * Gets the region of the draw space that is drawn on the screen and
	 * therefore rendered.
	 * 
	 * @return The region displayed on the screen as Rectangle.
	 */
	public ScreenPosition getScreen() {
		return this.screen;
	}

	/**
	 * TODO: height
	 * 
	 * @param x
	 *            The x coordinate in draw space
	 * @param y
	 *            The y coordinate in draw space.
	 * @return The map position under the point.
	 */
	public ISPosition2D getPositionUnder(float screenx, float screeny) {
		ISPosition2D currentPoint = converter.getMap(screenx, screeny);
		UIPoint desiredOnScreen = new UIPoint(screenx, screeny);

		UIPoint onscreen =
		        converter.getView(currentPoint.getX(), currentPoint.getY(),
		                getHeight(currentPoint.getX(), currentPoint.getY()));
		double currentbest = onscreen.distance(desiredOnScreen);

		boolean couldBeImproved;
		do {
			couldBeImproved = false;

			for (ISPosition2D p : new MapNeighboursArea(currentPoint)) {
				onscreen =
				        converter.getView(p.getX(), p.getY(),
				                getHeight(p.getX(), p.getY()));
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
	 * TODO: height
	 * 
	 * @param x
	 *            The x coordinate in screen space
	 * @param y
	 *            The y coordinate in screen space.
	 * @return The map position under the screen point.
	 */
	public ISPosition2D getPositionOnScreen(float x, float y) {
		return getPositionUnder(x / zoom + this.screen.getLeft(), y / zoom
		        + this.screen.getBottom());
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
			return this.playerColors[player % this.playerColors.length];
		} else {
			return Color.BLACK;
		}
	}

	public void debugTime(String string) {
		// System.out.println("Draw progress: " + string + " (time: "
		// + (System.nanoTime() - beginTime) + ")");
	}

	/**
	 * Gets the converter for the map coordinate system
	 * 
	 * @return The map coordinate converter.
	 */
	public MapCoordinateConverter getConverter() {
		return this.converter;
	}

	/**
	 * sets up the gl drawing context to draw a given tile.
	 * 
	 * @param pos
	 *            The tile to draw.
	 */
	public void beginTileContext(ISPosition2D pos) {
		this.gl.glPushMatrix();
		short x = pos.getX();
		short y = pos.getY();
		int height = getHeight(x, y);
		this.gl.glTranslatef(this.converter.getViewX(x, y, height),
		        this.converter.getViewY(x, y, height), 0);
	}

	/**
	 * Assumes that the user begun drawing a tile recently, and ends drawing the
	 * tile. This also resets the view matrix to the one before starting to
	 * draw.
	 */
	public void endTileContext() {
		this.gl.glPopMatrix();
	}

	/**
	 * Sets up drawing between two tiles.
	 * 
	 * @param tile
	 *            The start tile
	 * @param destination
	 *            The second tile
	 * @param progress
	 *            The progress between those two bytes.
	 */
	public void beginBetweenTileContext(ISPosition2D tile,
	        ISPosition2D destination, float progress) {
		this.gl.glPushMatrix();
		short tx = tile.getX();
		short ty = tile.getY();
		float theight = getHeight(tx, ty);
		short dx = destination.getX();
		short dy = destination.getY();
		float dheight = getHeight(dx, dy);
		float x =
		        (1 - progress) * this.converter.getViewX(tx, ty, theight)
		                + progress * this.converter.getViewX(dx, dy, dheight);
		float y =
		        (1 - progress) * this.converter.getViewY(tx, ty, theight)
		                + progress * this.converter.getViewY(dx, dy, dheight);
		this.gl.glTranslatef(x, y, 0);
	}

	/**
	 * gets a rect on the screen.
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
		float drawx1 = x1 / zoom + this.screen.getLeft();
		float drawx2 = x2 / zoom + this.screen.getLeft();
		float drawy1 = y1 / zoom + this.screen.getBottom();
		float drawy2 = y2 / zoom + this.screen.getBottom();
		return new HeightedMapRectangle(new FloatRectangle(drawx1, drawy1,
		        drawx2, drawy2));
	}

	private class HeightedMapRectangle implements IMapArea {
		/**
         * FIXME: This class is nor serializeable
         */
        private static final long serialVersionUID = 5868822981883722458L;
		
        /**
		 * Helper rectangle.
		 */
		MapRectangle base;
		private final FloatRectangle drawRect;

		/**
		 * Creates a new IMapArea that contains the points that are in the
		 * rectangle on the screen.
		 * 
		 * @param drawRect
		 *            The rectangle in draw space
		 */
		public HeightedMapRectangle(FloatRectangle drawRect) {
			this.drawRect = drawRect;
			base = converter.getMapForScreen(drawRect);
		}

		@Override
		public boolean contains(ISPosition2D point) {
			int height = getHeight(point.getX(), point.getY());
			float x = converter.getViewX(point.getX(), point.getY(), height);
			float y = converter.getViewY(point.getX(), point.getY(), height);
			return drawRect.contains(x, y);
		}

		@Override
		public Iterator<ISPosition2D> iterator() {
			return new MyIterator();
		}

		private class MyIterator implements Iterator<ISPosition2D> {
			/**
			 * How many lines to search at least.
			 */
			private static final int MIN_SEARCH_LINES = 20;
			private ShortPoint2D next;
			private int currentLine = 0;
			private int currentX;

			private MyIterator() {
				currentX = base.getLineStartX(0);
				next = new ShortPoint2D(currentX, base.getLineY(0));
				if (!contains(next)) {
					next = searchNext();
				}
			}

			private ShortPoint2D searchNext() {
	            int startLine = currentLine;
	            while (startLine >= currentLine - 2 || currentLine < MIN_SEARCH_LINES) {
	            	currentX++;
	            	if (currentX > base.getLineEndX(currentLine)) {
	            		currentLine++;
	            		currentX = base.getLineStartX(currentLine);
	            	}
	            	ShortPoint2D point = new ShortPoint2D(currentX, base.getLineY(currentLine));
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
			public ISPosition2D next() {
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

	public MapRectangle getScreenArea() {
		return this.converter.getMapForScreen(this.screen.getPosition());
	}

	public void scrollTo(ISPosition2D point) {
		int height = getHeight(point.getX(), point.getY());
		float x = converter.getViewX(point.getX(), point.getY(), height);
		float y = converter.getViewY(point.getX(), point.getY(), height);
		screen.setScreenCenter(x, y);
	}

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

	public IGraphicsGrid getMap() {
		return map;
	}

	public FogOfWar getFogOfWar() {
		return fogOfWar;
	}
}
