package jsettlers.graphics.map.geometry;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import jsettlers.common.map.IHexTile;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.Parallelogram;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.IntRectangle;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class converts map coordinates to e.g. draw coordinates.
 * <p>
 * For the conversion formulas used and what the witdh/height parameters mean,
 * see:
 * <p>
 * <img src="doc-files/MapCoordinateConverter.png">
 * 
 * @author michael
 */
public final class MapCoordinateConverter {
	private static final int M_HX = 2 * 4 + 0;
	private static final int M_HY = 2 * 4 + 1;
	/**
	 * The part of the matrix that is called m00 in {@link AffineTransform}.
	 */
	private static final int M_00 = 4 * 0;
	private static final int M_01 = 4 * 1;
	private static final int M_02 = 4 * 3;
	private static final int M_10 = 4 * 0 + 1;
	private static final int M_11 = 4 * 1 + 1;
	private static final int M_12 = 4 * 3 + 1;
	private static final float HEIGHT_X_DISPLACEMENT = 0;
	private static final float HEIGHT_Y_DISPLACEMENT = 3;

	private float[] matrix = new float[4 * 4];
	// matrix that also converts heights
	private float[] heightmatrix = new float[4 * 4];

	private float[] inverse = new float[4 * 4];
	/**
	 * The width of a tile in view space.
	 */
	private float xscale;
	/**
	 * The height of a tile in view space.
	 */
	private float yscale;

	/**
	 * Creates a new converter.
	 * 
	 * @param mapwidth
	 *            The map width
	 * @param mapheight
	 *            The map height
	 * @param viewwidth
	 *            The view width
	 * @param viewheight
	 *            The view height
	 */
	public MapCoordinateConverter(short mapwidth, short mapheight,
	        float viewwidth, float viewheight) {
		if (mapwidth <= 1 || mapheight <= 1) {
			throw new IllegalArgumentException("Map size too small");
		}
		if (viewwidth <= 0 || viewheight <= 0) {
			throw new IllegalArgumentException("View size too small");
		}

		int realMapWidth = mapwidth - 1;
		int realMapHeight = mapheight - 1;
		this.xscale = viewwidth / realMapWidth;
		this.yscale = viewheight / realMapHeight;

		this.matrix[M_00] = this.xscale;
		this.matrix[M_01] = -.5f * this.xscale;
		this.matrix[M_02] = .5f * realMapHeight * this.xscale;
		this.matrix[M_10] = 0;
		this.matrix[M_11] = -this.yscale;
		this.matrix[M_12] = viewheight;
		this.matrix[2 + 2 * 4] = 1;
		this.matrix[3 + 3 * 4] = 1;

		System.arraycopy(this.matrix, 0, this.heightmatrix, 0,
		        this.matrix.length);
		this.heightmatrix[M_HX] = HEIGHT_X_DISPLACEMENT;
		this.heightmatrix[M_HY] = HEIGHT_Y_DISPLACEMENT;

		this.inverse[M_00] = 1 / this.xscale;
		this.inverse[M_01] = -.5f / this.yscale;
		this.inverse[M_02] = 0;
		this.inverse[M_10] = 0;
		this.inverse[M_11] = -1 / this.yscale;
		this.inverse[M_12] = realMapHeight;
		this.inverse[2 + 2 * 4] = 1;
		this.inverse[3 + 3 * 4] = 1;
	}

	/**
	 * Gets the x coordinate of a point in view space.
	 * 
	 * @param x
	 *            The x coordinate in map space.
	 * @param y
	 *            The y coordinate in map space.
	 * @param height
	 *            The height of the tile.
	 * @return The view x coordinate
	 */
	public float getViewX(float x, float y, float height) {
		return x * this.heightmatrix[M_00] + y * this.heightmatrix[M_01]
		        + height * this.heightmatrix[M_HX] + this.heightmatrix[M_02];
	}

	/**
	 * Gets the y coordinate of a point in view space.
	 * 
	 * @param x
	 *            The x coordinate in map space.
	 * @param y
	 *            The y coordinate in map space.
	 * @param height
	 *            The height of the tile.
	 * @return The view y coordinate
	 */
	public float getViewY(float x, float y, float height) {
		return x * this.heightmatrix[M_10] + y * this.heightmatrix[M_11]
		        + height * this.heightmatrix[M_HY] + this.heightmatrix[M_12];
	}

	private float getExactMapX(float x, float y) {
		return x * this.inverse[M_00] + y * this.inverse[M_01]
		        + this.inverse[M_02];
	}

	/**
	 * Gets the closest map coordinates of a given pixel.
	 * 
	 * @param x
	 *            The x coordinate in draw space
	 * @param y
	 *            The y coordinate in draw space
	 * @return THe map coordinate x part
	 */
	public short getMapX(float x, float y) {
		return (short) Math.round(getExactMapX(x, y));

	}

	private float getExactMapY(float x, float y) {
		return x * this.inverse[M_10] + y * this.inverse[M_11]
		        + this.inverse[M_12];
	}

	/**
	 * Gets the closest map coordinates of a given pixel.
	 * 
	 * @param x
	 *            The x coordinate in draw space
	 * @param y
	 *            The y coordinate in draw space
	 * @return The map coordinate y part
	 */
	public short getMapY(float x, float y) {
		return (short) Math.round(getExactMapY(x, y));
	}

	/**
	 * Gets the matrix for the conversion from map coordinates to screen
	 * coordinates. The matrix must not be changed.
	 * 
	 * @return The opengl-compatibel matrix.
	 */
	public float[] getMatrix() {
		return this.matrix;
	}

	private short roundUpShort(float f) {
		return (short) Math.ceil(f);
	}

	/**
	 * Gets all tiles that are mapped to the given pixel.
	 * 
	 * @param x
	 *            the x pixel pos.
	 * @param y
	 *            the y pixel pos.
	 * @return The area. Might be empty but not null;
	 */
	public IMapArea getAreaForPixel(int x, int y) {
		float mapx = getExactMapX(x, y);
		float mapy = getExactMapY(x, y);

		float mapstartx = mapx - .5f / this.xscale;
		float mapstarty = mapy - .5f / this.yscale;
		float mapendx = mapx + .5f / this.xscale;
		float mapendy = mapy + .5f / this.yscale;

		System.out.println("map part: (" + mapstartx + "|" + mapstarty + "), ("
		        + mapendx + "|" + mapendy + ")");
		return new Parallelogram(roundUpShort(mapstartx),
		        roundUpShort(mapstarty), roundUpShort(mapendx - 1),
		        roundUpShort(mapendy - 1));
	}

	public float getViewX(ISPosition2D tile) {
		return getViewX(tile.getX(), tile.getY(), 0);
	}

	public float getViewY(ISPosition2D tile) {
		return getViewY(tile.getX(), tile.getY(), 0);
	}

	public float getViewX(IHexTile tile) {
		return getViewX(tile.getX(), tile.getY(), tile.getHeight());
	}

	public float getViewY(IHexTile tile) {
		return getViewY(tile.getX(), tile.getY(), tile.getHeight());
	}

	/**
	 * Gets the position of a map point on the screen
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return The position.
	 */
	public Point2D getView(int x, int y, byte height) {
		return new Point2D.Float(getViewX(x, y, height), getViewY(x, y, height));
	}

	/**
	 * Gets the closest position on the map for a position.
	 * 
	 * @param x
	 *            The x position in draw space.
	 * @param y
	 *            The y position in draw space
	 * @return The map point
	 */
	public ISPosition2D getMap(int x, int y) {
		return new ShortPoint2D(getMapX(x, y), getMapY(x, y));
	}

	/**
	 * Gets the x distance between two tile origins.
	 * 
	 * @return The distance.
	 */
	public float getTileXDistance() {
		return this.matrix[M_00];
	}

	/**
	 * Gets the y disntance between two tiles.
	 * 
	 * @return The distance in y direction.
	 */
	public float getTileYDistance() {
		return this.matrix[M_11];
	}

	/**
	 * Gets a rectangle that is almost covered by the given int rectangle.
	 * <p>
	 * No assumptions can be made about the rect.
	 * 
	 * @param screen
	 *            The screen positions
	 * @return A MapRectangle
	 */
	public MapRectangle getMapForScreen(IntRectangle screen) {
		float width = screen.getWidth() * this.inverse[M_00];
		float height = -screen.getHeight() * this.inverse[M_11];
		float minx = getMapX(screen.getMinX(), screen.getMaxY());
		float miny = getMapY(screen.getMinX(), screen.getMaxY());
		return new MapRectangle((short) minx, (short) miny, (short) Math.max(
		        Math.ceil(width), 0), (short) Math.max(Math.ceil(height), 0));
	}

	/**
	 * Gets a coordinate converter by the width and the height of the map, rathe
	 * than by the final size of the draw space.
	 * 
	 * @param xTileDistance
	 *            The x distance between two tiles in the same row.
	 * @param yTileDistance
	 *            The distance between two rows
	 * @param mapWidth
	 *            The width of the map in tile columns.
	 * @param mapHeight
	 *            The height of the map in rows.
	 * @return The new created converter.
	 */
	public static MapCoordinateConverter get(int xTileDistance,
	        int yTileDistance, short mapWidth, short mapHeight) {
		return new MapCoordinateConverter(mapWidth, mapHeight, (mapWidth - 1)
		        * xTileDistance, (mapHeight - 1) * yTileDistance);
	}

	public float[] getMatrixWithHeight() {
		return this.heightmatrix;
	}
}
