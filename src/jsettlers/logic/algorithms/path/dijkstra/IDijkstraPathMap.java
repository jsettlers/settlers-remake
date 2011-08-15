package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.IPathMap;

public interface IDijkstraPathMap extends IPathMap {

	boolean fitsSearchType(short x, short y, ESearchType type, IPathCalculateable requester);

}
