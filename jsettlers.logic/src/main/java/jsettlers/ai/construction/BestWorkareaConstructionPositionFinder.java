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

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiPositions.PositionRater;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

public abstract class BestWorkareaConstructionPositionFinder implements IBestConstructionPositionFinder {

	protected final EBuildingType buildingType;

	public static class WorkAreaPositionRater implements PositionRater {
		private static final int BLOCKS_WORK_AREA_MALUS = 12;
		private static final int NO_WORK_AREA_MALUS = 8;

		private final AbstractConstructionMarkableMap constructionMap;
		private final AiStatistics aiStatistics;
		private final byte playerId;
		private final AiPositions objects;
		private final EBuildingType buildingType;

		public WorkAreaPositionRater(AbstractConstructionMarkableMap constructionMap, AiStatistics aiStatistics, byte playerId, AiPositions objects, EBuildingType buildingType) {
			this.constructionMap = constructionMap;
			this.aiStatistics = aiStatistics;
			this.playerId = playerId;
			this.objects = objects;
			this.buildingType = buildingType;
		}

		@Override
		public int rate(int x, int y, int currentBestRating) {
			if (!constructionMap.canConstructAt((short) x, (short) y, buildingType, playerId)) {
				return RATE_INVALID;
			} else {
				int score = 0;
				ShortPoint2D p = new ShortPoint2D(x, y);
				if (!aiStatistics.southIsFreeForPlayer(p, playerId)) {
					score += NO_WORK_AREA_MALUS;
				}
				if (aiStatistics.blocksWorkingAreaOfOtherBuilding(p.x, p.y, playerId, buildingType)) {
					score += BLOCKS_WORK_AREA_MALUS;
				}

				if (score >= currentBestRating) {
					return RATE_INVALID;
				}

				short workRadius = buildingType.getWorkRadius();
				ShortPoint2D nearestTreePosition = objects.getNearestPoint(p, Math.min(workRadius, currentBestRating - score), null);
				if (nearestTreePosition == null) {
					return RATE_INVALID;
				}
				int treeDistance = nearestTreePosition.getOnGridDistTo(p);
				if (treeDistance >= workRadius) {
					return RATE_INVALID;
				}
				score += treeDistance;
				return score;
			}
		}
	}

	public BestWorkareaConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		AiPositions objects = getRelevantObjects(aiStatistics, playerId);
		if (objects.size() == 0) {
			return null;
		}
		PositionRater rater = new WorkAreaPositionRater(constructionMap, aiStatistics, playerId, objects, buildingType);

		return aiStatistics.getLandForPlayer(playerId).getBestRatedPoint(rater);
	}

	protected abstract AiPositions getRelevantObjects(AiStatistics aiStatistics, byte playerId);
}