/**
 * ****************************************************************************
 * Copyright (c) 2015
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * *****************************************************************************
 */
package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

import java.util.List;
import java.util.Vector;

/**
 * This searches for positions where the most corn can grow withing the work area
 *
 * @author codingberlin
 */
public class BestFarmConstructionPositionFinder implements IBestConstructionPositionFinder {

	static List<RelativePoint> workAreaPoints;

	static {
		workAreaPoints = new Vector<RelativePoint>();
		RelativePoint center = EBuildingType.FARM.getWorkcenter();
		short workRadius = EBuildingType.FARM.getWorkradius();
		for (short x = (short) -workRadius; x < workRadius; x++) {
			for (short y = (short) -workRadius; y < workRadius; y++) {
				if (Math.sqrt(x*x + y*y) <= workRadius) {
					workAreaPoints.add(new RelativePoint(center.getDx() + x, center.getDy() + y));
				}
			}
		}
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(
			AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ScoredConstructionPosition> scoredConstructionPositions = new Vector<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, EBuildingType.FARM, playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point, playerId, EBuildingType.FARM)) {
				int score = 0;
				for (RelativePoint relativePoint : workAreaPoints) {
					ShortPoint2D workAreaPoint = relativePoint.calculatePoint(point);
					if (aiStatistics.getMainGrid().getPartitionsGrid().getPlayerIdAt(workAreaPoint.x, workAreaPoint.y) == playerId
							&& aiStatistics.getMainGrid().isCornPlantable(workAreaPoint)) {
						score++;
					}
				}
				if (score > 0) {
					scoredConstructionPositions.add(new ScoredConstructionPosition(point, -score));
				}
			}
		}

		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);
	}
}
