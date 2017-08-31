/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import static jsettlers.common.buildings.EBuildingType.STONECUTTER;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.position.ShortPoint2D;

/**
 * Assumptions: stones are placed as groups at the map, never alone without other stones
 * 
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the distance from the most near stone of the possible construction position - takes
 * the position with the best score (lowest distance to the most near stone)
 * 
 * @author codingberlin
 */
public class BestStoneCutterConstructionPositionFinder implements IBestConstructionPositionFinder {

	public BestStoneCutterConstructionPositionFinder() {
	}

	public static class StoneCutterPositionRater implements AiPositions.PositionRater {
		private static final int BLOCKS_WORK_AREA_MALUS = 12;
		private static final int NEAR_OTHER_STONE_CUTTER_MALUS = 8;

		private final AbstractConstructionMarkableMap constructionMap;
		private final AiStatistics aiStatistics;
		private final byte playerId;
		private final AiPositions objects;

		public StoneCutterPositionRater(AbstractConstructionMarkableMap constructionMap, AiStatistics aiStatistics, byte playerId, AiPositions stones) {
			this.constructionMap = constructionMap;
			this.aiStatistics = aiStatistics;
			this.playerId = playerId;
			this.objects = stones;
		}

		@Override
		public int rate(int x, int y, int currentBestRating) {
			if (!constructionMap.canConstructAt((short) x, (short) y, STONECUTTER, playerId)) {
				return RATE_INVALID;
			} else {
				int score = 0;
				ShortPoint2D p = new ShortPoint2D(x, y);

				if (aiStatistics.blocksWorkingAreaOfOtherBuilding(p.x, p.y, playerId, STONECUTTER)) {
					score += BLOCKS_WORK_AREA_MALUS;
				}
				if (score >= currentBestRating) {
					return RATE_INVALID;
				}

				short workradius = STONECUTTER.getWorkRadius();
				for (ShortPoint2D otherStoneCutterPositions : aiStatistics.getBuildingPositionsOfTypeForPlayer(STONECUTTER, playerId)) {
					if (otherStoneCutterPositions.getOnGridDistTo(p) <= workradius) {
						score += NEAR_OTHER_STONE_CUTTER_MALUS;
						break;
					}
				}
				if (score >= currentBestRating) {
					return RATE_INVALID;
				}

				// ShortPoint2D nearestStonePosition = objects.getNearestPoint(p, Math.min(workradius, currentBestRating - score), null);
				ShortPoint2D nearestStonePosition = objects.getNearestPoint(p, workradius, null);
				if (nearestStonePosition == null) {
					return RATE_INVALID;
				}

				int treeDistance = nearestStonePosition.getOnGridDistTo(p);
				if (treeDistance >= workradius) {
					return RATE_INVALID;
				}
				score += treeDistance;
				return score;
			}
		}
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		AiPositions stones = aiStatistics.getStonesForPlayer(playerId);
		if (stones.size() == 0) {
			return null;
		}
		AiPositions.PositionRater rater = new StoneCutterPositionRater(constructionMap, aiStatistics, playerId, stones);

		return aiStatistics.getLandForPlayer(playerId).getBestRatedPoint(rater);
	}
}
