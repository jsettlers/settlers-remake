package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public class BestBigTempleConstructionPositionFinder extends NearDiggersConstructionPositionFinder implements IBestConstructionPositionFinder {

	public BestBigTempleConstructionPositionFinder() {
		super(EBuildingType.BIG_TEMPLE);
	}
	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, byte playerId) {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.TEMPLE, playerId) < 1) {
			return null; // do not construct big temple - you don't need it before small temples produce the remaining 2 mana for first level2
		} else {
			return super.findBestConstructionPosition(aiStatistics, constructionMap, playerId);
		}
	}
}
