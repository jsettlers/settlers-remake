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
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the distance from the 5 most near
 * trees of the possible construction position (the 5 most near to not build a lumberjack next to a single tree) - takes the position with the best
 * score (lowest distance to the most 5 near trees)
 * 
 * @author codingberlin
 */
public class BestLumberJackConstructionPositionFinder implements IBestConstructionPositionFinder {

	EBuildingType buildingType;

	public BestLumberJackConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> trees = aiStatistics.getTreesForPlayer(playerId);

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)) {
				double[] treeDistance = new double[5];
				treeDistance[0] = Double.MAX_VALUE / 5;
				treeDistance[1] = Double.MAX_VALUE / 5;
				treeDistance[2] = Double.MAX_VALUE / 5;
				treeDistance[3] = Double.MAX_VALUE / 5;
				treeDistance[4] = Double.MAX_VALUE / 5;
				for (ShortPoint2D tree : trees) {
					double currentTreeDistance = Math.sqrt((tree.x - point.x) * (tree.x - point.x) + (tree.y - point.y) * (tree.y - point.y));
					for (int i = 0; i < treeDistance.length; i++) {
						if (currentTreeDistance < treeDistance[i]) {
							for (int ii = i + 1; ii < treeDistance.length; ii++) {
								treeDistance[ii] = treeDistance[ii - 1];
							}
							treeDistance[i] = currentTreeDistance;
							break;
						}
					}
				}
				double score = 0;
				for (int i = 0; i < treeDistance.length; i++) {
					score += treeDistance[i];
				}
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
