package jsettlers.logic.algorithms.path.astar;

import jsettlers.logic.algorithms.path.IPathMap;

public interface IAStarPathMap extends IPathMap {

	float getHeuristicCost(short sx, short sy, short tx, short ty);

}
