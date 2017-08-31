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

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

/**
 * Assumptions: foresters are placed after lumberjacks were placed
 * 
 * Algorithm: find all possible construction points within the borders of the player - calculates a score and take the position with the best score - score is affected by distance to the most near
 * lumberjack - score is affected by distance to other foresters in order to not build new foresters next to other foresters
 * 
 * @author codingberlin
 */
public class BestForesterConstructionPositionFinder implements IBestConstructionPositionFinder {

	private EBuildingType buildingType;

	public BestForesterConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> lumberJacks = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.LUMBERJACK, playerId);
		List<ShortPoint2D> foresters = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.FORESTER, playerId);

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point.x, point.y, playerId, buildingType)) {
				int foresterDistance = 0;
				int lumberJackDistance = 0;
				ShortPoint2D nearestLumberJackPoint = AiStatistics.detectNearestPointFromList(point, lumberJacks);
				if (nearestLumberJackPoint != null) {
					lumberJackDistance = point.getOnGridDistTo(nearestLumberJackPoint);
				}
				ShortPoint2D nearestForesterPoint = AiStatistics.detectNearestPointFromList(point, foresters);
				if (nearestForesterPoint != null) {
					foresterDistance = point.getOnGridDistTo(nearestForesterPoint);
				}

				scoredConstructionPositions.add(new ScoredConstructionPosition(new ShortPoint2D(point.x, point.y), lumberJackDistance - foresterDistance));
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}
}
