package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IDijkstraPathMap {

	short getHeight();

	short getWidth();

	boolean fitsSearchType(short x, short y, ESearchType type, IPathCalculateable requester);

	void setDijkstraSearched(short x, short y);

}
