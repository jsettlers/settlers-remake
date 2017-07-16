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
package jsettlers.mapcreator.tools;

import java.util.Set;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Interface for all tools, displayed in the tree
 * 
 * @author Andreas Butti
 *
 */
public interface Tool extends ToolNode {

	/**
	 * Return a Set with all supported shape types
	 * 
	 * @return Read only list
	 */
	Set<EShapeType> getSupportedShapes();

	/**
	 * Editing
	 * 
	 * @param map
	 * @param shape
	 * @param start
	 * @param end
	 * @param uidx
	 */
	void apply(MapData map, ShapeType shape, ShortPoint2D start, ShortPoint2D end, double uidx);

	/**
	 * Start the editing
	 * 
	 * @param data
	 * @param shape
	 * @param pos
	 */
	void start(MapData data, ShapeType shape, ShortPoint2D pos);
}
