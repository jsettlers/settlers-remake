package jsettlers.ai.construction;

import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.position.ShortPoint2D;

public interface IBestConstructionPositionFinder {
	
	public abstract ShortPoint2D findBestConstructionPosition(AbstractConstructionMarkableMap constructionMap, byte playerId);
}
