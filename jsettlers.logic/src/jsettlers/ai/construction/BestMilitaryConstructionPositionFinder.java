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
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
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

	private final EBuildingType buildingType;

	private enum ImportantResource {
		TREES,
		STONES,
		COAL,
		IRON,
		GOLD,
		WINE_MOUNTAIN
	}

	public BestMilitaryConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		ImportantResource importantResource = detectMostImportantResourcePoints(aiStatistics, playerId);
		System.out.println("importantResource:" + importantResource + " " + playerId);

		List<ShortPoint2D> militaryBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(TOWER, playerId);
		militaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(BIG_TOWER, playerId));
		militaryBuildings.addAll(aiStatistics.getBuildingPositionsOfTypeForPlayer(CASTLE, playerId));

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getBorderLandNextToFreeLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId) && !aiStatistics.blocksWorkingAreaOfOtherBuilding(point)) {
				ShortPoint2D nearestResourcePoint;
				switch (importantResource) {
				case TREES:
					nearestResourcePoint = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(point, EMapObjectType.STONE);
					break;
				default:
					nearestResourcePoint = aiStatistics.getNearestResourcePointInDefaultPartitionFor(point, EResourceType.COAL);
				}
				double nearestResourcePointDistance = aiStatistics.getDistance(nearestResourcePoint, point);
				ShortPoint2D nearestMilitairyBuildingPosition = aiStatistics.detectNearestPointFromList(point, militaryBuildings);
				double militairyBuildingDistance = aiStatistics.getDistance(point, nearestMilitairyBuildingPosition);
				scoredConstructionPositions.add(new ScoredConstructionPosition(point, nearestResourcePointDistance - militairyBuildingDistance));
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}

	private ImportantResource detectMostImportantResourcePoints(AiStatistics aiStatistics, byte playerId) {
		List<ShortPoint2D> trees = aiStatistics.getTreesForPlayer(playerId);

		if (trees.size() < 30) {
			return ImportantResource.TREES;
		}

		return ImportantResource.COAL;
	}
}
