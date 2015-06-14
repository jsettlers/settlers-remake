package jsettlers.ai.construction;

import java.util.ArrayList;
import java.util.List;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

public class BestForesterConstructionPositionFinder implements IBestConstructionPositionFinder {

	private static final Double MIN_FORESTER_DISTANCE = new Double(25);
	EBuildingType buildingType;

	public BestForesterConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		List<ShortPoint2D> lumberJacks = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.LUMBERJACK, playerId);
		List<ShortPoint2D> foresters = aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.FORESTER, playerId);

		List<ScoredConstructionPosition> scoredConstructionPositions = new ArrayList<ScoredConstructionPosition>();
		for (ShortPoint2D point : aiStatistics.getLandForPlayer(playerId)) {
			if (constructionMap.canConstructAt(point.x, point.y, buildingType, playerId)) {

				double minLumberJackDistance = Double.MAX_VALUE;
				double maxLumberJackDistance = 0;
				for (ShortPoint2D lumberJack : lumberJacks) {
					double currentLumberJackDistance = Math.sqrt((lumberJack.x - point.x) * (lumberJack.x - point.x) + (lumberJack.y - point.y)
							* (lumberJack.y - point.y));
					if (currentLumberJackDistance < minLumberJackDistance) {
						minLumberJackDistance = currentLumberJackDistance;
					}
					if (currentLumberJackDistance > maxLumberJackDistance) {
						maxLumberJackDistance = currentLumberJackDistance;
					}
				}
				double foresterDistance = MIN_FORESTER_DISTANCE;
				for (ShortPoint2D forester : foresters) {
					double currentForesterDistance = Math.sqrt((forester.x - point.x) * (forester.x - point.x)
							+ (forester.y - point.y)
							* (forester.y - point.y));
					if (currentForesterDistance < foresterDistance) {
						foresterDistance = currentForesterDistance;
					}
				}
				double score = 1 - (foresterDistance / MIN_FORESTER_DISTANCE) + (minLumberJackDistance / maxLumberJackDistance);
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
