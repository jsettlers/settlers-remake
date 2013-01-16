package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IDijkstraPathMap {

	boolean fitsSearchType(int x, int y, ESearchType type, IPathCalculateable requester);

	void setDijkstraSearched(int x, int y);

}
