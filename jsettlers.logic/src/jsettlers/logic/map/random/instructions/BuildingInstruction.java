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
package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.grid.PlaceholderObject;

public class BuildingInstruction extends ObjectInstruction {
	private static Hashtable<String, String> defaults = new Hashtable<String, String>();
	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "1");
		defaults.put("distance", "0");
		defaults.put("on", "grass");
		defaults.put("type", "tower");
	}

	@Override
	protected void placeObject(MapGrid grid, PlayerStart start, int x, int y,
			Random random) {
		EBuildingType type = getParameter("type", random, EBuildingType.class);

		for (RelativePoint relative : type.getProtectedTiles()) {
			grid.setMapObject(x + relative.getDx(), y + relative.getDy(),
					PlaceholderObject.getInstance());
		}

		grid.setMapObject(x, y, new BuildingObject(type, start.getPlayerId()));
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
