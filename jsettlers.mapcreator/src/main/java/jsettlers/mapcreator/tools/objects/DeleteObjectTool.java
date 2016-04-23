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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.tools.AbstractTool;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Delete existing objects
 * 
 * @author Andreas Butti
 *
 */
public class DeleteObjectTool extends AbstractTool {

	/**
	 * Constructor
	 */
	public DeleteObjectTool() {
		super("delete");
		shapeTypes.add(EShapeType.POINT);
		shapeTypes.add(EShapeType.LINE_CIRCLE);
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (influences[x][y] > 0) {
					map.deleteObject(x, y);
				}
			}
		}
	}
}
