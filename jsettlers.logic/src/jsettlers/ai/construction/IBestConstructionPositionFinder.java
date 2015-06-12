package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.position.ShortPoint2D;

public interface IBestConstructionPositionFinder {

	public abstract ShortPoint2D findBestConstructionPosition(AiStatistics aiStatistics, AbstractConstructionMarkableMap constructionMap,
			byte playerId);
}
