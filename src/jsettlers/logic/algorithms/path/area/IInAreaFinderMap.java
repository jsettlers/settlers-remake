package jsettlers.logic.algorithms.path.area;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculatable;

public interface IInAreaFinderMap {

	boolean isBlocked(IPathCalculatable requester, int tileX, int tileY);

	boolean fitsSearchType(int tileX, int tileY, ESearchType searched, IPathCalculatable requester);

}
