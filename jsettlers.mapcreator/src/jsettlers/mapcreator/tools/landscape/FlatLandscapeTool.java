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
 * Makes a flat space on the landscape
 * 
 * @author michael
 */
public class FlatLandscapeTool extends AbstractTool {

	private byte[][] old;
	private double influencefactor = .3;

	/**
	 * Constructor
	 */
	public FlatLandscapeTool() {
		super("flatten");
		shapeTypes.addAll(LandscapeHeightTool.LANDSCAPE_SHAPES);
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);

		long heightsum = 0;
		long heightweights = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				heightsum += influences[x][y] * old[x][y];
				heightweights += influences[x][y];
			}
		}

		double desired = (double) heightsum / heightweights;

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (influences[x][y] == 0) {
					continue;
				}
				int oldheight = map.getLandscapeHeight(x, y);
				double influence = influencefactor * influences[x][y] / Byte.MAX_VALUE;
				int newheight = (int) (influence * desired + (1 - influence)
						* old[x][y]);
				if (desired < old[x][y]) {
					if (newheight < oldheight) {
						map.setHeight(x, y, newheight);
					}
				} else {
					if (newheight > oldheight) {
						map.setHeight(x, y, newheight);
					}
				}
			}
		}
	}

	@Override
	public void start(MapData map, ShapeType shape, ShortPoint2D pos) {
		old = new byte[map.getWidth()][map.getHeight()];

		for (int x = 0; x < old.length; x++) {
			for (int y = 0; y < old[x].length; y++) {
				old[x][y] = map.getLandscapeHeight(x, y);
			}
		}

	}
}
