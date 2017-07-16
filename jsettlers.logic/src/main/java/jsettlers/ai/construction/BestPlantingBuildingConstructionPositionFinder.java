/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.MainGrid;

/**
 * @author codingberlin
 */
abstract public class BestPlantingBuildingConstructionPositionFinder implements IBestConstructionPositionFinder {

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<>();

		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, myBuildingType(), playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point.x, point.y, playerId, myBuildingType())) {
				int score = calculateScoreFor(point, aiStatistics.getMainGrid(), playerId);
				if (score > 0) {
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, -score));
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}

	private int calculateScoreFor(ShortPoint2D point, MainGrid mainGrid, byte playerId) {
		int score = 0;
		for (RelativePoint relativePoint : myRelativeWorkAreaPoints()) {
			ShortPoint2D workAreaPoint = relativePoint.calculatePoint(point);
			if (mainGrid.isInBounds(workAreaPoint.x, workAreaPoint.y)
					&& mainGrid.getPartitionsGrid().getPlayerIdAt(workAreaPoint.x, workAreaPoint.y) == playerId
					&& isMyPlantPlantable(mainGrid, workAreaPoint)) {
				score++;
			}
		}
		return score;
	}

	protected static RelativePoint[] calculateMyRelativeWorkAreaPoints(EBuildingType myBuildingType) {
		List<RelativePoint> workAreaPoints = new ArrayList<>();
		RelativePoint center = myBuildingType.getDefaultWorkcenter();
		short workRadius = myBuildingType.getWorkRadius();
		for (short x = (short) -workRadius; x < workRadius; x++) {
			for (short y = (short) -workRadius; y < workRadius; y++) {
				if (Math.sqrt(x * x + y * y) <= workRadius) {
					workAreaPoints.add(new RelativePoint(center.getDx() + x, center.getDy() + y));
				}
			}
		}

		RelativePoint[] relativeWorkAreaPoints = new RelativePoint[workAreaPoints.size()];
		relativeWorkAreaPoints = workAreaPoints.toArray(relativeWorkAreaPoints);
		return relativeWorkAreaPoints;
	}

	abstract protected EBuildingType myBuildingType();

	abstract protected RelativePoint[] myRelativeWorkAreaPoints();

	abstract protected boolean isMyPlantPlantable(MainGrid mainGrid, ShortPoint2D position);
}
