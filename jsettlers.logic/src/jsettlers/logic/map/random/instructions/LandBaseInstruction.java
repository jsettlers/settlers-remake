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

import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.landscape.HillPolicy;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;

/**
 * Creates a base land everywhere.
 * <p>
 * Only needs to be used once
 * 
 * @author michael
 */
public class LandBaseInstruction extends LandInstruction {
	private static Hashtable<String, String> defaults =
			new Hashtable<String, String>();

	static {
		defaults.put("type", "grass");
		defaults.put("on", "");
	}

	@Override
	public void execute(LandscapeMesh landscape, PlayerStart[] starts,
			Random random) {
		MeshLandscapeType onLandscape =
				MeshLandscapeType.parse(getParameter("on", random), null);
		MeshLandscapeType type =
				getParameter("type", random, MeshLandscapeType.class);
		for (MeshSite site : landscape.getSites()) {
			if (!site.isFixed()
					&& (onLandscape == null || site.getLandscape() == onLandscape)) {
				site.setLandscape(type, HillPolicy.HILLY);
			}
		}
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
