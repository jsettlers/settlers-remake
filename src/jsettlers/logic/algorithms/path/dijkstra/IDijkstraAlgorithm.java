package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IDijkstraAlgorithm {
	public ISPosition2D find(IPathCalculateable requester, final short cX, final short cY, final short searchRadius, final ESearchType type);
}
