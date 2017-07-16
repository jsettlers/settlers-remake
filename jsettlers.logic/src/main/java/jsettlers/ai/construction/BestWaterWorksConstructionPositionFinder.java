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
package jsettlers.ai.construction;

import java.util.ArrayList;
import java.util.List;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

/**
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the distance from the most near river of the possible construction position - takes
 * the position with the best score (lowest distance to the most near river)
 * 
 * @author codingberlin
 */
public class BestWaterWorksConstructionPositionFinder implements IBestConstructionPositionFinder {

	private EBuildingType buildingType;

	public BestWaterWorksConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		AiPositions rivers = aiStatistics.getRiversForPlayer(playerId);
		if (rivers.size() == 0) {
			return null;
		}
		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point.x, point.y, playerId, buildingType)) {
				ShortPoint2D nearestRiverPosition = rivers.getNearestPoint(point, buildingType.getWorkRadius(), null);
				if (nearestRiverPosition != null) {
					int riverDistance = point.getOnGridDistTo(nearestRiverPosition);
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, riverDistance));
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}

}
