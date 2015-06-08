package jsettlers.ai.construction;

import jsettlers.common.buildings.EBuildingType;

public class BestConstructionPositionFinderFactory {

	public final IBestConstructionPositionFinder getBestConstructionPositionFinderFor(EBuildingType type) {
		/*if (type == STONECUTTER) {
			return new BestStoneCutterConstructionPositionFinder();
		}*/
		return new NearDiggersConstructionPositionFinder(type);
	}
	
}
