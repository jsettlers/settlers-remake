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
package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.tools.AbstractTool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Increase / decrease height
 * 
 * @author Andreas Butti
 *
 */
public class IncreaseDecreaseHeightAdder extends AbstractTool {

	private static final int INCREASE_HEIGHT = 5;
	private final boolean subtract;

	private int[][] alreadyadded = null;

	/**
	 * Constructor
	 * 
	 * @param subtract
	 *            increase / decrease height
	 */
	public IncreaseDecreaseHeightAdder(boolean subtract) {
		super(subtract ? "decreaseheight"
				: "increaseheight");
		this.subtract = subtract;
		shapeTypes.addAll(LandscapeHeightTool.LANDSCAPE_SHAPES);
	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
		// do nothing.
		alreadyadded = new int[data.getWidth()][data.getHeight()];
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {
		if (alreadyadded == null) {
			alreadyadded = new int[map.getWidth()][map.getHeight()];
		}

		byte[][] influence = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influence, start, end);
		int factor = subtract ? -1 : 1;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				int dheight = (INCREASE_HEIGHT * influence[x][y] / Byte.MAX_VALUE);
				if (dheight == 0) {
					continue;
				}

				int apply;

				if (alreadyadded[x][y] > dheight) {
					apply = 0;
				} else {
					apply = dheight - alreadyadded[x][y];
					alreadyadded[x][y] = dheight;
				}

				int newheight = (factor * apply + map.getLandscapeHeight(x, y));
				map.setHeight(x, y, newheight);
			}
		}
	}

}
