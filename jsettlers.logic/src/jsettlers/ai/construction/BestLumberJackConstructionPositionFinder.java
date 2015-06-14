package jsettlers.ai.construction;

import java.util.ArrayList;
import java.util.List;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

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
