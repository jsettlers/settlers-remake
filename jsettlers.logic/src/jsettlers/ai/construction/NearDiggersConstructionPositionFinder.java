package jsettlers.ai.construction;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;

public class NearDiggersConstructionPositionFinder implements IBestConstructionPositionFinder {

	EBuildingType buildingType;

	public NearDiggersConstructionPositionFinder(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Override
	public ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap, PartitionsGrid partitionsGrid, ObjectsGrid objectsGrid, byte playerId) {
		// this first iteration of the code just places the building anywhere at the first matching position
		short westOffset = 0;
		short eastOffset = 0;
		short northOffset = 0;
		short southOffset = 0;
		for (RelativePoint point : buildingType.getProtectedTiles()) {
			short x = point.getDx();
			short y = point.getDy();
			if (x < 0 && -1*x > westOffset) {
				westOffset = (short) (-1*x);
			} 
			if (x > eastOffset) {
				eastOffset = x;
			}
			if (y < 0 && -1*y > northOffset) {
				northOffset = (short) (-1*y);
			} 
			if (y > southOffset) {
				southOffset = y;
			}
		}
		
		for(short x=(short)(1+westOffset); x < constructionMap.getWidth()-1-eastOffset; x++) {
			for(short y=(short)(1+northOffset); y < constructionMap.getHeight()-1-southOffset; y++) {
				if (constructionMap.canConstructAt(x, y, buildingType, playerId)) {
					return new ShortPoint2D(x, y);
				}
			}	
		}
		return null;
	}

}
