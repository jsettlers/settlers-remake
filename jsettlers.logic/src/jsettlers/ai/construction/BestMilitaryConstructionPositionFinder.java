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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;

/**
 * Assumptions: the most needed land are mountains with resources for military production
 *
 * Algorithm: find all possible construction points within the borders of the player - calculates a score and take the position with the best score -
 * score is affected by distance of the most important resource at the moment. The most important resource is: first trees, then: stones, rivers, coal
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
		WINE_MOUNTAIN,
		RIVER,
		FISH
	}

	public BestMilitaryConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		AiPositions borderLandNextToFreeLandForPlayer = aiStatistics.getBorderLandNextToFreeLandForPlayer(playerId);
		if (borderLandNextToFreeLandForPlayer.size() == 0) {
			return null;
		}

		Set<ImportantResource> importantResources = detectMostImportantResourcePoints(aiStatistics, playerId,
				borderLandNextToFreeLandForPlayer.iterator().next());

		int nearestResourcePointsDistance = Integer.MAX_VALUE;
		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		ShortPoint2D centerOfPlayer = calculateCenterOfPlayer(aiStatistics, playerId);
		for (ShortPoint2D point : borderLandNextToFreeLandForPlayer) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId) && !aiStatistics.blocksWorkingAreaOfOtherBuilding(point,
					playerId, buildingType)) {
				if (importantResources.isEmpty()) {
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, centerOfPlayer.getOnGridDistTo(point)));
				} else {
					List<ShortPoint2D> nearestResourcePoints = new ArrayList<ShortPoint2D>();
					if (importantResources.contains(ImportantResource.TREES)) {
						ShortPoint2D nearestTree = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(point, EMapObjectType.TREE_ADULT,
								nearestResourcePointsDistance);
						if (nearestTree != null) {
							nearestResourcePoints.add(nearestTree);
						}
					}
					if (importantResources.contains(ImportantResource.STONES)) {
						ShortPoint2D nearestStone = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(point, EMapObjectType.STONE,
								nearestResourcePointsDistance);
						if (nearestStone != null) {
							nearestResourcePoints.add(nearestStone);
						}
					}
					if (importantResources.contains(ImportantResource.RIVER)) {
						ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(point, nearestResourcePointsDistance);
						if (nearestRiver != null) {
							nearestResourcePoints.add(nearestRiver);
						}
					}
					if (importantResources.contains(ImportantResource.GOLD)) {
						ShortPoint2D nearestGold = aiStatistics.getNearestResourcePointInDefaultPartitionFor(point, EResourceType.GOLDORE,
								nearestResourcePointsDistance);
						if (nearestGold != null) {
							nearestResourcePoints.add(nearestGold);
						}
					}
					if (importantResources.contains(ImportantResource.IRON)) {
						ShortPoint2D nearestIron = aiStatistics.getNearestResourcePointInDefaultPartitionFor(point, EResourceType.IRONORE,
								nearestResourcePointsDistance);
						if (nearestIron != null) {
							nearestResourcePoints.add(nearestIron);
						}
					}
					if (importantResources.contains(ImportantResource.COAL)) {
						ShortPoint2D nearestCoal = aiStatistics.getNearestResourcePointInDefaultPartitionFor(point, EResourceType.COAL,
								nearestResourcePointsDistance);
						if (nearestCoal != null) {
							nearestResourcePoints.add(nearestCoal);
						}
					}
					if (importantResources.contains(ImportantResource.FISH)) {
						ShortPoint2D nearestFish = aiStatistics.getNearestResourcePointInDefaultPartitionFor(point, EResourceType.FISH,
								nearestResourcePointsDistance);
						if (nearestFish != null) {
							nearestResourcePoints.add(nearestFish);
						}
					}
					if (nearestResourcePoints.size() == importantResources.size()) {
						int maximumDistanceOfResourcesOfThisPoint = 0;
						int score = 0;
						for (ShortPoint2D currentPoint : nearestResourcePoints) {
							int distance = currentPoint.getOnGridDistTo(point);
							score += distance;
							maximumDistanceOfResourcesOfThisPoint = Math.max(maximumDistanceOfResourcesOfThisPoint, distance);
						}
						nearestResourcePointsDistance = maximumDistanceOfResourcesOfThisPoint;
						scoredConstructionPositions.add(new ScoredConstructionPosition(point, score));
					}
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}

	private ShortPoint2D calculateCenterOfPlayer(AiStatistics aiStatistics, byte playerId) {
		List<ShortPoint2D> towers = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.TOWER, playerId);
		int centerX = 0;
		int centerY = 0;
		for (ShortPoint2D tower : towers) {
			centerX += tower.x;
			centerY += tower.y;
		}
		return new ShortPoint2D(centerX / towers.size(), centerY / towers.size());
	}

	private Set<ImportantResource> detectMostImportantResourcePoints(AiStatistics aiStatistics, byte playerId, ShortPoint2D referencePoint) {
		Set<ImportantResource> importantResources = EnumSet.noneOf(ImportantResource.class);
		AiPositions trees = aiStatistics.getTreesForPlayer(playerId);
		AiPositions stones = aiStatistics.getStonesForPlayer(playerId);
		AiPositions rivers = aiStatistics.getRiversForPlayer(playerId);
		if (trees.size() < 30) {
			importantResources.add(ImportantResource.TREES);
		}
		if (stones.size() < 15) {
			importantResources.add(ImportantResource.STONES);
		}
		if (importantResources.size() > 0) {
			// trees and stone are essential for survival. Build them first with high priority and ignore the rest!
			return importantResources;
		}
		if (rivers.size() < 15) {
			importantResources.add(ImportantResource.RIVER);
		}
		if (importantResources.size() < 3 &&
				aiStatistics.getNearestResourcePointForPlayer(referencePoint, EResourceType.COAL, playerId, Integer.MAX_VALUE) == null &&
				aiStatistics.getNearestResourcePointInDefaultPartitionFor(referencePoint, EResourceType.COAL, Integer.MAX_VALUE) != null) {
			importantResources.add(ImportantResource.COAL);
		}
		if (importantResources.size() < 3 &&
				aiStatistics.getNearestResourcePointForPlayer(referencePoint, EResourceType.IRONORE, playerId, Integer.MAX_VALUE) == null &&
				aiStatistics.getNearestResourcePointInDefaultPartitionFor(referencePoint, EResourceType.IRONORE, Integer.MAX_VALUE) != null) {
			importantResources.add(ImportantResource.IRON);
		}
		if (importantResources.size() < 3 &&
				aiStatistics.getNearestResourcePointForPlayer(referencePoint, EResourceType.FISH, playerId, Integer.MAX_VALUE) == null &&
				aiStatistics.getNearestResourcePointInDefaultPartitionFor(referencePoint, EResourceType.FISH, Integer.MAX_VALUE) != null) {
			importantResources.add(ImportantResource.FISH);
		}
		if (importantResources.size() < 3 &&
				aiStatistics.getNearestResourcePointForPlayer(referencePoint, EResourceType.GOLDORE, playerId, Integer.MAX_VALUE) == null &&
				aiStatistics.getNearestResourcePointInDefaultPartitionFor(referencePoint, EResourceType.GOLDORE, Integer.MAX_VALUE) != null) {
			importantResources.add(ImportantResource.GOLD);
		}
		// 50 : 50 chance to spread the land or to go for more resources
		if (importantResources.size() == 0 && MatchConstants.aiRandom().nextBoolean() == true) {
			importantResources.add(ImportantResource.GOLD);
			importantResources.add(ImportantResource.IRON);
			importantResources.add(ImportantResource.COAL);
		}
		return importantResources;
	}
}
