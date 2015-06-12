package jsettlers.ai.construction;

import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;

public interface IBestConstructionPositionFinder {
	
	public abstract ShortPoint2D findBestConstructionPosition(AbstractConstructionMarkableMap constructionMap, PartitionsGrid partitionsGrid, ObjectsGrid objectsGrid, byte playerId);
}
