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
 * Algorithm: find all possible construction points within the borders of the player - calculates a score (0% bad position till 100% bad position) and
 * take the position with the lowest bad score - score is affected by distance to the most near lumberjack (0% bad position means the most near lumber
 * jack is next to this position, 100% bad position means the most near lumber jack is in comparison to the other positions most far away) - score is
 * affected by distance to other foresters in order to build new foresters to needily lumberjacks (0% bad position means that the most nearest
 * forester's distance is greater than 25, 100% bad position means that the most nearest forester is just next to the construction position)
 * 
 * @author codingberlin
 */
public class BestForesterConstructionPositionFinder implements IBestConstructionPositionFinder {

	private static final Double MIN_FORESTER_DISTANCE = new Double(25);
	EBuildingType buildingType;

	public BestForesterConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> lumberJacks = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.LUMBERJACK, playerId);
		List<ShortPoint2D> foresters = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.FORESTER, playerId);

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)) {

				double minLumberJackDistance = Double.MAX_VALUE;
				double maxLumberJackDistance = 0;
				for (ShortPoint2D lumberJack : lumberJacks) {
					double currentLumberJackDistance = Math.sqrt((lumberJack.x - point.x) * (lumberJack.x - point.x) + (lumberJack.y - point.y)
							* (lumberJack.y - point.y));
					if (currentLumberJackDistance < minLumberJackDistance) {
						minLumberJackDistance = currentLumberJackDistance;
					}
					if (currentLumberJackDistance > maxLumberJackDistance) {
						maxLumberJackDistance = currentLumberJackDistance;
					}
				}
				double foresterDistance = MIN_FORESTER_DISTANCE;
				for (ShortPoint2D forester : foresters) {
					double currentForesterDistance = Math.sqrt((forester.x - point.x) * (forester.x - point.x)
							+ (forester.y - point.y)
							* (forester.y - point.y));
					if (currentForesterDistance < foresterDistance) {
						foresterDistance = currentForesterDistance;
					}
				}
				double score = 1 - (foresterDistance / MIN_FORESTER_DISTANCE) + (minLumberJackDistance / maxLumberJackDistance);
				scoredConstructionPositions.add(new ScoredConstructionPosition(new ShortPoint2D(point.x, point.y), score));

			}
		}

		ScoredConstructionPosition winnerPosition = null;
		for (ScoredConstructionPosition scoredConstructionPosition : scoredConstructionPositions) {
			if (winnerPosition == null) {
				winnerPosition = scoredConstructionPosition;
			} else if (winnerPosition.score > scoredConstructionPosition.score) {
				winnerPosition = scoredConstructionPosition;
			}
		}

		if (winnerPosition == null) {
			return null;
		}

		return winnerPosition.point;
	}
}
