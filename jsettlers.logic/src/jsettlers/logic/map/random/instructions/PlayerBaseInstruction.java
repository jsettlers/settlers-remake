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
import jsettlers.logic.map.random.geometry.Point2D;
import jsettlers.logic.map.random.landscape.AndSiteCriterium;
import jsettlers.logic.map.random.landscape.HillPolicy;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;
import jsettlers.logic.map.random.landscape.SiteBorderCriterium;
import jsettlers.logic.map.random.landscape.SiteCriterium;
import jsettlers.logic.map.random.landscape.SiteDistanceCriterium;
import jsettlers.logic.map.random.landscape.SiteLandscapeCriterium;

/**
 * Fills parts of the land around a player with a given material
 * 
 * @author michael
 */
public class PlayerBaseInstruction extends LandInstruction {

	private static Hashtable<String, String> defaults =
			new Hashtable<String, String>();

	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("fix", "true");
		defaults.put("distance", "0-20");
		defaults.put("type", "grass");
		defaults.put("on", "");
		defaults.put("size", "100");
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

	@Override
	public void execute(LandscapeMesh landscape, PlayerStart[] starts,
			Random random) {
		MeshLandscapeType type =
				MeshLandscapeType.parse(getParameter("type", random));
		boolean fix = getParameter("fix", random).equals("true");
		MeshLandscapeType onLandscape =
				MeshLandscapeType.parse(getParameter("on", random), null);

		for (PlayerStart start : starts) {
			Point2D point = new Point2D(start.x, start.y);

			SiteCriterium criterium =
					new SiteDistanceCriterium(point, getParameter("distance",
							random));
			if (onLandscape != null) {
				criterium =
						new AndSiteCriterium(criterium,
								new SiteBorderCriterium(new SiteLandscapeCriterium(onLandscape)));
			}

			MeshSite[] sites =
					landscape.getSitesWithCriterium(criterium, random,
							getIntParameter("size", random));
			for (MeshSite site : sites) {
				site.setLandscape(type, HillPolicy.FLAT);
				site.setFixed(fix);
			}
		}
	}
}
