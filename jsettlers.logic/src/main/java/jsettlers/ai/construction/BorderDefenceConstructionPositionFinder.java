/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

import java.util.List;

/**
 * @author codingberlin
 */
public class BorderDefenceConstructionPositionFinder implements IBestConstructionPositionFinder {
	private final List<ShortPoint2D> threatenedBorders;

	public BorderDefenceConstructionPositionFinder(List<ShortPoint2D> threatenedBorders) {
		this.threatenedBorders = threatenedBorders;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(final AiStatistics aiStatistics, final AbstractConstructionMarkableMap constructionMap, final byte playerId) {
		AiPositions landToBuildOn = aiStatistics.getLandForPlayer(playerId);
		for (ShortPoint2D threatenedBorder : threatenedBorders) {
			ShortPoint2D constructionPosition = landToBuildOn.getNearestPoint(threatenedBorder, CommonConstants.TOWER_RADIUS,
					(x, y) -> constructionMap.canConstructAt((short) x, (short) y, EBuildingType.TOWER, playerId)
							&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(x, y, playerId, EBuildingType.TOWER));
			if (constructionPosition != null) {
				return constructionPosition;
			}
		}
		return null;
	}
}
