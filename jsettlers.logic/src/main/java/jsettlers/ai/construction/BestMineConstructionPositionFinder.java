/*******************************************************************************
 * Copyright (c) 2015 - 2018
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

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the amount of resource
 *
 * @author codingberlin
 */
public class BestMineConstructionPositionFinder implements IBestConstructionPositionFinder {
	private static final float DISTANCE_PENALTY_FACTOR = 0.01f;

	private final EBuildingType buildingType;
	private final EResourceType resourceType;

	public BestMineConstructionPositionFinder(EBuildingType buildingType, EResourceType resourceType) {
		this.buildingType = buildingType;
		this.resourceType = resourceType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)) {
				int resourceAmount = 0;
				LandscapeGrid landscapeGrid = aiStatistics.getMainGrid().getLandscapeGrid();
				for (RelativePoint relativePoint : buildingType.getBlockedTiles()) {
					int x = point.x + relativePoint.getDx();
					int y = point.y + relativePoint.getDy();
					if (landscapeGrid.getResourceTypeAt(x, y) == resourceType) {
						resourceAmount += landscapeGrid.getResourceAmountAt(x, y);
					}
				}

				if (resourceAmount != 0) {
					int distanceToCenter = aiStatistics.getPositionOfPartition(playerId).getOnGridDistTo(point);
					int score = resourceAmount - (int) (distanceToCenter * DISTANCE_PENALTY_FACTOR);
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, -score));
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}
}
