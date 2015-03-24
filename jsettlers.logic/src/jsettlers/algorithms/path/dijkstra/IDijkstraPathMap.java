package jsettlers.algorithms.path.dijkstra;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.material.ESearchType;

public interface IDijkstraPathMap {

	boolean fitsSearchType(int x, int y, ESearchType type, IPathCalculatable requester);

	void setDijkstraSearched(int x, int y);

}
