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
			final AiStatistics aiStatistics, final AbstractConstructionMarkableMap constructionMap, final byte playerId) {
		AiPositions landToBuildOn = aiStatistics.getLandForPlayer(playerId);
		for (ShortPoint2D threatenedBorder : threatenedBorders) {
			ShortPoint2D constructionPosition = landToBuildOn.getNearestPoint(threatenedBorder, CommonConstants.TOWER_RADIUS, new AiPositions
					.AiPositionFilter() {
				@Override public boolean contains(int x, int y) {
					return constructionMap.canConstructAt((short) x, (short) y, EBuildingType.TOWER, playerId)
							&& !aiStatistics.blocksWorkingAreaOfOtherBuilding(new ShortPoint2D(x, y), playerId, EBuildingType.TOWER);
				}
			});
			if (constructionPosition != null) {
				return constructionPosition;
			}
		}
		return null;
	}
}
