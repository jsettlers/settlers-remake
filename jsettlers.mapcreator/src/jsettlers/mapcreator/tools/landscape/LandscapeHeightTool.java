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

import javax.swing.Icon;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.FuzzyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.LineCircleShape;
import jsettlers.mapcreator.tools.shapes.NoisyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class LandscapeHeightTool implements Tool {

	public static final ShapeType[] LANDSCAPE_SHAPES = new ShapeType[] { new LineCircleShape(), new FuzzyLineCircleShape(),
			new NoisyLineCircleShape() };
	private ShortPoint2D start = new ShortPoint2D(0, 0);
	private byte[][] influences;
	private double[][] carry;

	public LandscapeHeightTool() {
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("changeheightdescr");
	}

	@Override
	public ShapeType[] getShapes() {
		return LANDSCAPE_SHAPES;
	}

	// TODO: this should me done in screen space!
	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D unused, ShortPoint2D unused2, double uidx) {

		double factor = uidx / 10000f;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				double dheight = factor * influences[x][y] + carry[x][y];
				int apply = (int) dheight;
				carry[x][y] = dheight - apply;

				if (apply == 0) {
					continue;
				}

				int newheight = (apply + map.getLandscapeHeight(x, y));
				map.setHeight(x, y, newheight);
			}
		}
	}

	@Override
	public void start(MapData map, ShapeType shape, ShortPoint2D pos) {
		start = pos;
		influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, start);
		carry = new double[map.getWidth()][map.getHeight()];
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
