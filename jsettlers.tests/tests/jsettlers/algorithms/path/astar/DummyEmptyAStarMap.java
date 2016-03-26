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
package jsettlers.algorithms.path.astar;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.astar.IAStarPathMap;
import jsettlers.common.Color;

/**
 * Dummy map for testing purposes of AStar.
 * 
 * @author Andreas Eberle
 * 
 */
public class DummyEmptyAStarMap implements IAStarPathMap {

	private final boolean[][] blocked;

	public DummyEmptyAStarMap(short width, short height) {
		this.blocked = new boolean[width][height];
	}

	@Override
	public boolean isBlocked(IPathCalculatable requester, int x, int y) {
		return blocked[x][y];
	}

	@Override
	public float getCost(int sx, int sy, int tx, int ty) {
		return 1;
	}

	@Override
	public void markAsOpen(int x, int y) {

	}

	@Override
	public void markAsClosed(int x, int y) {

	}

	public void setBlocked(int x, int y, boolean b) {
		blocked[x][y] = b;
	}

	@Override
	public void setDebugColor(int x, int y, Color color) {
	}

	@Override
	public short getBlockedPartition(int x, int y) {
		return 1;
	}

	@Override
	public boolean isBlockedByMovable(IPathCalculatable requester, int x, int y) {
		return false;
	}
}
