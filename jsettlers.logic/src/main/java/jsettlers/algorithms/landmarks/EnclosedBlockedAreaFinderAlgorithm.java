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
package jsettlers.algorithms.landmarks;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * Algorithm to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public final class EnclosedBlockedAreaFinderAlgorithm {

	public static void checkLandmark(IEnclosedBlockedAreaFinderGrid grid, int startX, int startY) {
		if (grid.isPioneerBlockedAndWithoutTowerProtection(startX, startY)) {
			return;
		}

		final IContainingProvider containingProvider = grid::isPioneerBlockedAndWithoutTowerProtection;
		final byte startPlayer = grid.getPlayerIdAt(startX, startY);

		for (EDirection currDir : EDirection.VALUES) {
			ShortPoint2D currPos = currDir.getNextHexPoint(startX, startY);

			if (grid.isPioneerBlockedAndWithoutTowerProtection(currPos.x, currPos.y)) {
				if (needsRelabel(grid, containingProvider, currPos, startPlayer)) {
					destroyBuildingsOrTakeOver(grid, containingProvider, currPos, startPlayer);
				}
			}
		}
	}

	private static void destroyBuildingsOrTakeOver(IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D blockedStartPos, byte newPlayer) {
		AreaTraversingAlgorithm.traverseArea(containingProvider, grid.getDestroyBuildingOrTakeOverVisitor(newPlayer), blockedStartPos, grid.getWidth(), grid.getHeight());
	}

	/**
	 * Checks if the blocked partition given by the coordinates blockedX and blockedY is surrounded by the given partition.
	 * 
	 * @param grid
	 * @param containingProvider
	 * @param blockedStartPos
	 * @return
	 */
	private static boolean needsRelabel(IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D blockedStartPos, byte player) {
		return BorderTraversingAlgorithm.traverseBorder(containingProvider, blockedStartPos,
				(insideX, insideY, outsideX, outsideY) -> grid.isOfPlayerOrBlocked(outsideX, outsideY, player), true);
	}
}
