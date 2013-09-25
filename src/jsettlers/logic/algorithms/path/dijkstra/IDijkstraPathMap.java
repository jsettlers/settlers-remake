package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculatable;

public interface IDijkstraPathMap {

	boolean fitsSearchType(int x, int y, ESearchType type, IPathCalculatable requester);

	void setDijkstraSearched(int x, int y);

}
