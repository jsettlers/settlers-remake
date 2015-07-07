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
 * Assumptions: trees are placed as groups or as a single tree on the map
 * 
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the distance from the most near
 * tree of the possible construction position - takes the position with the best score (lowest distance to the most near tree)
 * 
 * @author codingberlin
 */
public class BestLumberJackConstructionPositionFinder implements IBestConstructionPositionFinder {

	private EBuildingType buildingType;
	private final short workingRadius;

	public BestLumberJackConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
		workingRadius = buildingType.getWorkradius();
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> trees = aiStatistics.getTreesForPlayer(playerId);
		if (trees.size() == 0) {
			return null;
		}
		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId) && aiStatistics.southIsFreeForPlayer(point, playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point)) {
				ShortPoint2D nearestTreePosition = aiStatistics.detectNearestPointFromList(point, trees);
				double treeDistance = point.calculateDistanceTo(nearestTreePosition);
				if (treeDistance < workingRadius) {
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, treeDistance));
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}

}
