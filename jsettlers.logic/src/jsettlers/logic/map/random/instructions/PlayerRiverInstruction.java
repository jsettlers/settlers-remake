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
import java.util.LinkedList;
import java.util.Random;

import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.geometry.Point2D;
import jsettlers.logic.map.random.landscape.AndSiteCriterium;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;
import jsettlers.logic.map.random.landscape.SiteBorderCriterium;
import jsettlers.logic.map.random.landscape.SiteCriterium;
import jsettlers.logic.map.random.landscape.SiteDistanceCriterium;
import jsettlers.logic.map.random.landscape.SiteLandscapeCriterium;
import jsettlers.logic.map.random.landscape.Vertex;

public class PlayerRiverInstruction extends LandInstruction {

	private static Hashtable<String, String> defaults =
			new Hashtable<String, String>();

	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("distance", "0-200");
		defaults.put("length", "200");
		defaults.put("on", "");
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

	@Override
	public void execute(LandscapeMesh landscape, PlayerStart[] starts,
			Random random) {
		for (PlayerStart start : starts) {
			int x = start.x + getIntParameter("dx", random);
			int y = start.y + getIntParameter("dy", random);

			MeshSite sea = findSeaSite(x, y, landscape, random);

			Vertex borderVertex = getSeaBorder(sea);

			buildRiver(borderVertex, random);
		}
	}

	private void buildRiver(Vertex vertex, Random random) {
		int length = getIntParameter("length", random);
		while (length > 10) {
			buildRiverWithLength(vertex, random, length);
			length /= 2;
		}
	}

	private boolean buildRiverWithLength(Vertex vertex, Random random,
			int length) {
		LinkedList<Vertex> trace = new LinkedList<Vertex>();
		trace.add(vertex);
		if (buildRiver(vertex, trace, random, length)) {
			Vertex current = trace.poll();
			while (current != null) {
				Vertex next = trace.poll();
				for (MeshEdge e : current.getEdges()) {
					if (e.getOppositePoint(current) == next) {
						// edge found
						e.setRiver(true);
					}
				}

				current = next;
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean buildRiver(Vertex vertex, LinkedList<Vertex> trace,
			Random random, double restlength) {
		for (MeshEdge e : vertex.getEdges()) {
			Vertex opposite = e.getOppositePoint(vertex);
			if (trace.contains(opposite)) {
				continue;
			}
			double distance = Math.sqrt(opposite.distanceSquared(vertex));
			double length = Math.max(distance, 10);

			trace.add(opposite);
			if (length > restlength
					|| buildRiver(opposite, trace, random, restlength - length)) {
				return true;
			} else {
				trace.removeLast();
			}
		}
		return false;
	}

	private static Vertex getSeaBorder(MeshSite sea) {
		if (sea != null && sea.getEdges().length > 0) {
			Vertex current = sea.getEdges()[0].getStart();
			double direction = sea.getCenter().getDirectionTo(current);

			int trys = 0;
			while (trys < 10) {
				for (MeshSite site : current.getNeighbourSites()) {
					if (site.getLandscape() != MeshLandscapeType.SEA) {
						return current;
					}
				}

				double bestdiff = Float.POSITIVE_INFINITY;
				Vertex bestOpposite = null;
				for (MeshEdge edge : current.getEdges()) {
					Vertex opposite = edge.getOppositePoint(current);
					double odirection = opposite.getDirectionTo(current);
					double diff = Math.abs(direction - odirection);
					if (diff > Math.PI) {
						diff = 2 * Math.PI - diff;
					}
					if (diff < bestdiff) {
						bestOpposite = opposite;
						bestdiff = diff;
					}
				}

				if (bestOpposite != null) {
					current = bestOpposite;
				} else {
					break;
				}

				trys++;
			}
			return null;
		} else {
			return null;
		}
	}

	private MeshSite findSeaSite(int x, int y, LandscapeMesh landscape,
			Random random) {
		Point2D point = new Point2D(x, y);
		SiteCriterium criterium =
				new SiteDistanceCriterium(point, getParameter("distance",
						random));
		MeshLandscapeType onLandscape =
				MeshLandscapeType.parse(getParameter("on", random), null);
		if (onLandscape != null) {
			criterium =
					new AndSiteCriterium(criterium, new SiteBorderCriterium(
							new SiteLandscapeCriterium(onLandscape)));
		}

		MeshSite[] sites =
				landscape.getSitesWithCriterium(criterium, random, 1);

		if (sites.length <= 0) {
			return null;
		} else {
			return sites[0];
		}
	}
}
