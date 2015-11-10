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

		public WorkAreaPositionRater(AbstractConstructionMarkableMap constructionMap, AiStatistics aiStatistics, byte playerId, AiPositions objects,
				EBuildingType buildingType) {
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
				if (aiStatistics.blocksWorkingAreaOfOtherBuilding(p, playerId, buildingType)) {
					score += BLOCKS_WORK_AREA_MALUS;
				}

				if (score >= currentBestRating) {
					return RATE_INVALID;
				}

				short workradius = buildingType.getWorkradius();
				ShortPoint2D nearestTreePosition = objects.getNearestPoint(p, Math.min(workradius, currentBestRating - score), null);
				if (nearestTreePosition == null) {
					return RATE_INVALID;
				}
				int treeDistance = nearestTreePosition.getOnGridDistTo(p);
				if (treeDistance >= workradius) {
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