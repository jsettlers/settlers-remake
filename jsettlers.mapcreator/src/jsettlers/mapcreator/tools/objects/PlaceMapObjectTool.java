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
package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.AbstractTool;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class PlaceMapObjectTool extends AbstractTool {
	private final MapObject object;

	public PlaceMapObjectTool(MapObject object) {
		super(null, null);
		shapeTypes.add(EShapeType.POINT);
		shapeTypes.add(EShapeType.GRID_CIRCLE);

		this.object = object;

		if (object == null) {
			// initialized in subclass
			return;
		}

		if (object instanceof MapStoneObject) {
			this.translatedName = String.format(EditorLabels.getLabel("tool.stone"),
					((MapStoneObject) object).getCapacity());
		} else if (object instanceof MapTreeObject) {
			this.translatedName = EditorLabels.getLabel("tool.tree");
		} else if (object instanceof MapDecorationObject) {
			this.translatedName = String.format(
					EditorLabels.getLabel("tool.place"),
					EditorLabels.getLabel("tool.object."
							+ ((MapDecorationObject) object).getType()));
		} else {
			this.translatedName = String.format(EditorLabels.getLabel("tool.place"), object
					.getClass().getSimpleName());
		}
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {

		byte[][] placeAt = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(placeAt, start, end);

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (placeAt[x][y] > Byte.MAX_VALUE / 2) {
					map.placeObject(getObject(), x, y);
				}
			}
		}
	}

	public MapObject getObject() {
		return object;
	}
}
