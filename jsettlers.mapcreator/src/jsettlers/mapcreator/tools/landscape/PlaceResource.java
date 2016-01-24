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

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.tasks.ValidateResources;
import jsettlers.mapcreator.tools.AbstractTool;
import jsettlers.mapcreator.tools.icons.ToolIcon;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Tool to place resources
 * 
 * @author Andreas Butti
 */
public class PlaceResource extends AbstractTool implements ResourceTool {

	private final EResourceType type;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            Type to place, <code>null</code> to delete resources
	 */
	public PlaceResource(EResourceType type) {
		super(type == null ? ToolIcon.loadIcon("remove-resource.png") : null,
				type == null ? EditorLabels.getLabel("tool.remove-resource") : Labels.getName(type));
		this.type = type;
		shapeTypes.add(EShapeType.POINT);
		shapeTypes.add(EShapeType.LINE);
		shapeTypes.add(EShapeType.LINE_CIRCLE);
		shapeTypes.add(EShapeType.NOISY_LINE_CIRCLE);
		shapeTypes.add(EShapeType.GRID_CIRCLE);
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {
		byte[][] influence = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influence, start, end);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				placeAt(map, influence, x, y);
			}
		}

	}

	private void placeAt(MapData map, byte[][] influence, int x, int y) {
		if (type != null) {
			if (ValidateResources
					.mayHoldResource(map.getLandscape(x, y), type)) {
				map.addResource(x, y, type, influence[x][y]);
			}
		} else {
			map.decreaseResourceTo(x, y,
					(byte) (Byte.MAX_VALUE - influence[x][y]));
		}
	}
}
