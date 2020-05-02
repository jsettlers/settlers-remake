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
package jsettlers.logic.map.loading.original.data;

import java.util.HashMap;
import java.util.Map;

import jsettlers.common.landscape.ELandscapeType;

/**
 * The landscape types for the map
 * @author Thomas Zeugner
 * @author codingberlin
 */
public class OriginalLandscape {
	private static final Map<Integer, ELandscapeType> originalLandscapeType = new HashMap<>();
	static {
		originalLandscapeType.put(0, ELandscapeType.WATER1);
		originalLandscapeType.put(1, ELandscapeType.WATER2);
		originalLandscapeType.put(2, ELandscapeType.WATER3);
		originalLandscapeType.put(3, ELandscapeType.WATER4);
		originalLandscapeType.put(4, ELandscapeType.WATER5);
		originalLandscapeType.put(5, ELandscapeType.WATER6);
		originalLandscapeType.put(6, ELandscapeType.WATER7);
		originalLandscapeType.put(7, ELandscapeType.WATER8);

		originalLandscapeType.put(16, ELandscapeType.GRASS);
		originalLandscapeType.put(17, ELandscapeType.MOUNTAINBORDER);

		originalLandscapeType.put(20, ELandscapeType.DESERTBORDEROUTER);
		originalLandscapeType.put(21, ELandscapeType.MOORBORDEROUTER);
		originalLandscapeType.put(23, ELandscapeType.MUDBORDEROUTER);

		originalLandscapeType.put(32, ELandscapeType.MOUNTAIN);
		originalLandscapeType.put(33, ELandscapeType.MOUNTAINBORDER);
		originalLandscapeType.put(35, ELandscapeType.SNOWBORDEROUTER);

		originalLandscapeType.put(48, ELandscapeType.SAND);

		originalLandscapeType.put(64, ELandscapeType.DESERT);
		originalLandscapeType.put(65, ELandscapeType.DESERTBORDER);

		originalLandscapeType.put(80, ELandscapeType.MOOR);
		originalLandscapeType.put(81, ELandscapeType.MOORBORDER);


		originalLandscapeType.put(96, ELandscapeType.RIVER1);
		originalLandscapeType.put(97, ELandscapeType.RIVER2);
		originalLandscapeType.put(98, ELandscapeType.RIVER3);
		originalLandscapeType.put(99, ELandscapeType.RIVER4);


		originalLandscapeType.put(128, ELandscapeType.SNOW);
		originalLandscapeType.put(129, ELandscapeType.SNOWBORDER);

		originalLandscapeType.put(144, ELandscapeType.MUD);
		originalLandscapeType.put(145, ELandscapeType.MUDBORDER);
	}

	public static ELandscapeType getTypeByInt(int type) {
		return originalLandscapeType.get(type);
	}
}