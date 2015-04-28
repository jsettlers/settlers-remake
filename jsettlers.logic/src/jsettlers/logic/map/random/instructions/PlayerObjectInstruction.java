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

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.logic.map.random.generation.PlayerStart;

public class PlayerObjectInstruction extends ObjectInstruction {

	private static Hashtable<String, String> defaults =
			new Hashtable<String, String>();

	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "100");
		defaults.put("tight", "1");
		defaults.put("distance", "0");
		defaults.put("on", "grass");
		defaults.put("type", "tree");
		defaults.put("capacity", "0");
	}

	@Override
	protected MapObject getObject(PlayerStart start, Random random) {
		MapObject object;
		String type = getParameter("type", random).toUpperCase();
		if ("TREE".equals(type)) {
			object = MapTreeObject.getInstance();
		} else if ("STONE".equals(type)) {
			object = MapStoneObject.getInstance(getIntParameter("capacity", random));
		} else {
			throw new IllegalArgumentException("type " + type + " unknown");
		}
		return object;
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
