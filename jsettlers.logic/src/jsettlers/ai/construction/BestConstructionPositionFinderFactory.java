package jsettlers.ai.construction;

import jsettlers.common.buildings.EBuildingType;

public class BestConstructionPositionFinderFactory {

	public final IBestConstructionPositionFinder getBestConstructionPositionFinderFor(EBuildingType type) {
		if (type == EBuildingType.STONECUTTER) {
			return new BestStoneCutterConstructionPositionFinder(type);
		}	/*
		if (type == EBuildingType.LUMBERJACK) {
			return new BestLumberJackConstructionPositionFinder(type);
		}*/
		return new NearDiggersConstructionPositionFinder(type);
	}
	
}
