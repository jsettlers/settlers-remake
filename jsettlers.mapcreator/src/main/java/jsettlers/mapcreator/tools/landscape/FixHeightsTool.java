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

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.LandscapeConstraint;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.objects.ObjectContainer;
import jsettlers.mapcreator.mapvalidator.tasks.error.ValidateLandscape;
import jsettlers.mapcreator.mapvalidator.tasks.error.ValidateResources;
import jsettlers.mapcreator.tools.AbstractTool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Fix height problems
 * 
 * @author Andreas Butti
 *
 */
public class FixHeightsTool extends AbstractTool {

	/**
	 * Constructor
	 */
	public FixHeightsTool() {
		super("fixheights");
		shapeTypes.addAll(LandscapeHeightTool.LANDSCAPE_SHAPES);
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);

		for (int x = 0; x < map.getWidth() - 1; x++) {
			for (int y = 0; y < map.getWidth() - 1; y++) {
				if (influences[x][y] > 0) {
					fix(map, x, y, x + 1, y);
					fix(map, x, y, x + 1, y + 1);
					fix(map, x, y, x, y + 1);
				}
			}
		}

		for (int x = map.getWidth() - 2; x >= 0; x--) {
			for (int y = map.getWidth() - 2; y >= 0; y--) {
				if (influences[x][y] > 0) {
					fix(map, x, y, x + 1, y);
					fix(map, x, y, x + 1, y + 1);
					fix(map, x, y, x, y + 1);
				}
			}
		}
	}

	private static void fix(MapData map, int x, int y, int x2, int y2) {
		byte h1 = map.getLandscapeHeight(x, y);
		byte h2 = map.getLandscapeHeight(x2, y2);
		ELandscapeType l1 = map.getLandscape(x, y);
		ELandscapeType l2 = map.getLandscape(x2, y2);

		int maxHeightDiff = ValidateLandscape.getMaxHeightDiff(l1, l2);

		ObjectContainer container1 = map.getMapObjectContainer(x, y);
		if (container1 instanceof LandscapeConstraint
				&& ((LandscapeConstraint) container1).needsFlattenedGround()) {
			maxHeightDiff = 0;
		}
		ObjectContainer container2 = map.getMapObjectContainer(x2, y2);
		if (container2 instanceof LandscapeConstraint
				&& ((LandscapeConstraint) container2).needsFlattenedGround()) {
			maxHeightDiff = 0;
		}

		if (h1 - h2 > maxHeightDiff) {
			// h1 too big
			map.setHeight(x, y, h2 + maxHeightDiff);
		} else if (h2 - h1 > maxHeightDiff) {
			// h2 too big
			map.setHeight(x2, y2, h1 + maxHeightDiff);
		}
	}
}
