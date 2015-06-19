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

import static jsettlers.common.buildings.EBuildingType.BIG_TOWER;
import static jsettlers.common.buildings.EBuildingType.CASTLE;
import static jsettlers.common.buildings.EBuildingType.TOWER;

import java.util.ArrayList;
import java.util.List;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

/**
 * Assumptions: the most needed land are mountains with resources for military production
 * 
 * Algorithm: find all possible construction points within the borders of the player - calculates a score and take the position with the best score -
 * score is affected by the distance to of other militairy buildings to get the most land out of the less militairy buildings - score is affected by
 * distance of resources
 * 
 * @author codingberlin
 */
public class BestMilitaryConstructionPositionFinder implements IBestConstructionPositionFinder {

	EBuildingType buildingType;

	public BestMilitaryConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> militaryBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(TOWER, playerId);
		militaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(BIG_TOWER, playerId));
		militaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(CASTLE, playerId));

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getBorderLandNextToFreeLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId) && !aiStatistics.blocksWorkingAreaOfOtherBuilding(point)) {

				double militairyBuildingDistance = Double.MAX_VALUE;
				for (ShortPoint2D militairyBuilding : militaryBuildings) {
					double currentMilitaryBuildingDistance = Math.sqrt((militairyBuilding.x - point.x) * (militairyBuilding.x - point.x)
							+ (militairyBuilding.y - point.y)
							* (militairyBuilding.y - point.y));
					if (currentMilitaryBuildingDistance < militairyBuildingDistance) {
						militairyBuildingDistance = currentMilitaryBuildingDistance;
					}
				}
				ShortPoint2D nearestResourcePoint = aiStatistics.getNearestResourcePointFor(point);
				double nearestResourcePointDistance = aiStatistics.getDistance(nearestResourcePoint, point);
				scoredConstructionPositions.add(new ScoredConstructionPosition(new ShortPoint2D(point.x, point.y), militairyBuildingDistance
						- nearestResourcePointDistance));
			}
		}

		ScoredConstructionPosition winnerPosition = null;
		for (ScoredConstructionPosition currentPosition : scoredConstructionPositions) {
			if (winnerPosition == null) {
				winnerPosition = currentPosition;
			} else if (currentPosition.score > winnerPosition.score) {
				winnerPosition = currentPosition;
			}
		}

		if (winnerPosition == null) {
			return null;
		}

		return winnerPosition.point;
	}
}
