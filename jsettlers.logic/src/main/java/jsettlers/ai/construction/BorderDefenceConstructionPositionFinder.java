package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author codingberlin
 */
public class BorderDefenceConstructionPositionFinder implements IBestConstructionPositionFinder {
	private final List<ShortPoint2D> threatenedBorders;

	public BorderDefenceConstructionPositionFinder(List<ShortPoint2D> threatenedBorders) {
		this.threatenedBorders = threatenedBorders;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(
			AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		AiPositions borderLandNextToFreeLandForPlayer = aiStatistics.getThreatenedBorderLandForPlayer(playerId);
		if (borderLandNextToFreeLandForPlayer.size() == 0) {
			return null;
		}

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<>();
		for (ShortPoint2D point : borderLandNextToFreeLandForPlayer) {
			if (constructionMap.canConstructAt(point.x, point.y, EBuildingType.TOWER, playerId)
					&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(point, playerId, EBuildingType.TOWER)) {
				int score = 0;
				for (ShortPoint2D threatenedBorder : threatenedBorders) {
					if (threatenedBorder.getOnGridDistTo(point) <= CommonConstants.TOWER_RADIUS) {
						score++;
					}

				}
				scoredConstructionPositions.add(new ScoredConstructionPosition(point, score));
			}
		}
		return ScoredConstructionPosition.detectPositionWithLowestScore(scoredConstructionPositions);

	}
}
