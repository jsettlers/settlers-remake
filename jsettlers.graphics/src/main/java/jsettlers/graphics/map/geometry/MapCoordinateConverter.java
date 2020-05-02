/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.graphics.map.geometry;

import java.awt.geom.AffineTransform;

import go.graphics.UIPoint;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class converts map coordinates to e.g. draw coordinates.
 * <p>
 * For the conversion formulas used and what the witdh/height parameters mean, see:
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
	private static final float HEIGHT_Y_DISPLACEMENT = 2;

	private float[] matrix = new float[4 * 4];
	// matrix that also converts heights
	private float[] heightmatrix = new float[4 * 4];

	private float[] inverse = new float[4 * 4];

	private float[] heightinverse = new float[4 * 4];

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
		/*
	  The width of a tile in view space.
	 */
		float xscale = viewwidth / realMapWidth;
		/*
	  The height of a tile in view space.
	 */
		float yscale = viewheight / realMapHeight;

		this.matrix[M_00] = xscale;
		this.matrix[M_01] = -.5f * xscale;
		this.matrix[M_02] = .5f * realMapHeight * xscale;
		this.matrix[M_10] = 0;
		this.matrix[M_11] = -yscale;
		this.matrix[M_12] = viewheight;
		this.matrix[2 + 2 * 4] = 1;
		this.matrix[3 + 3 * 4] = 1;

		System.arraycopy(this.matrix, 0, this.heightmatrix, 0,
				this.matrix.length);
		this.heightmatrix[M_HX] = HEIGHT_X_DISPLACEMENT;
		this.heightmatrix[M_HY] = HEIGHT_Y_DISPLACEMENT;

		this.inverse[M_00] = 1 / xscale;
		this.inverse[M_01] = -.5f / yscale;
		this.inverse[M_02] = 0;
		this.inverse[M_10] = 0;
		this.inverse[M_11] = -1 / yscale;
		this.inverse[M_12] = realMapHeight;
		this.inverse[2 + 2 * 4] = 1;
		this.inverse[3 + 3 * 4] = 1;

		System.arraycopy(this.inverse, 0, this.heightinverse, 0,
				this.inverse.length);
		this.heightinverse[M_HX] = 1 / yscale;
		this.heightinverse[M_HY] = 2 / yscale;
	}

	/**
	 * Gets the x coordinate of a point in view space.
	 *
	 * @param x
	 *            The x coordinate in map space.
	 * @param y
	 *            The y coordinate in map space.
	 * @param arg
	 *            The width of the tile.
	 * @return The view x coordinate
	 */
	public float getViewX(float x, float y, float arg) {
		return x * this.heightmatrix[M_00] + y * this.heightmatrix[M_01]
				+ arg * this.heightmatrix[M_HX] + this.heightmatrix[M_02];
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

	public float getExactMapXwithHeight(float x, float y, float height) {
		return x * this.heightinverse[M_00] + y * this.heightinverse[M_01]
				+ height * this.heightinverse[M_HX] + this.heightinverse[M_02];
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

	public float getExactMapYwithHeight(float x, float y, float height) {
		return x * this.heightinverse[M_10] + y * this.heightinverse[M_11]
				+ height * this.heightinverse[M_HY] + this.heightinverse[M_12];
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
	 * Gets the position of a map point on the screen
	 *
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return The position.
	 */
	public UIPoint getView(int x, int y, int height) {
		return new UIPoint(getViewX(x, y, height), getViewY(x, y, height));
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
	public ShortPoint2D getMap(float x, float y) {
		return new ShortPoint2D(getMapX(x, y), getMapY(x, y));
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
	public MapRectangle getMapForScreen(FloatRectangle screen) {
		float maxMountainHeight = HEIGHT_Y_DISPLACEMENT * Byte.MAX_VALUE;
		float minX = getExactMapXwithHeight(screen.getMinX(), screen.getMaxY(), 0);
		float maxX = getExactMapXwithHeight(screen.getMaxX(), screen.getMinY(), maxMountainHeight);
		float minY = getExactMapYwithHeight(screen.getMaxX(), screen.getMaxY(), 0);
		float maxY = getExactMapYwithHeight(screen.getMinX(), screen.getMinY(), maxMountainHeight);
		return new MapRectangle((short) minX, (short) minY, (short) (maxX - minX), (short) (maxY - minY));
	}

	/**
	 * Gets a coordinate converter by the width and the height of the map, rathe than by the final size of the draw space.
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
