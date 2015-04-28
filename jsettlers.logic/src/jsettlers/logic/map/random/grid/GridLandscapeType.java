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
package jsettlers.logic.map.random.grid;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;

public class GridLandscapeType {
	private GridLandscapeType() {
	}

	public static ELandscapeType convert(MeshLandscapeType landscape) {
		if (landscape == null) {
			return ELandscapeType.GRASS;
		}
		switch (landscape) {
		case DESERT:
			return ELandscapeType.DESERT;

		case MOUNTAIN:
			return ELandscapeType.MOUNTAIN;

		case SAND:
			return ELandscapeType.SAND;

		case SEA:
			return ELandscapeType.WATER1;

		case GRASS:
		default:
			return ELandscapeType.GRASS;
		}
	}

	public static ELandscapeType forEdge(MeshEdge edge) {
		if (edge.isRiver()) {
			System.out.println("River found!");
			return ELandscapeType.RIVER2;
		}

		if (edge.getLeft() == null) {
			return convert(edge.getRight().getLandscape());
		} else if (edge.getRight() == null) {
			return convert(edge.getLeft().getLandscape());
		}
		MeshLandscapeType left = edge.getLeft().getLandscape();
		MeshLandscapeType right = edge.getRight().getLandscape();
		if (left == right) {
			return convert(left);
		} else if ((left == MeshLandscapeType.GRASS && right == MeshLandscapeType.MOUNTAIN)
				|| (right == MeshLandscapeType.GRASS && left == MeshLandscapeType.MOUNTAIN)) {
			return ELandscapeType.MOUNTAINBORDER;

		} else if ((left == MeshLandscapeType.SEA && right == MeshLandscapeType.GRASS)
				|| (right == MeshLandscapeType.SEA && left == MeshLandscapeType.GRASS)) {
			return ELandscapeType.SAND;
		} else {
			return convert(left);
		}
	}
}
