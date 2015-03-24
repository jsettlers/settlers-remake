package jsettlers.algorithms.path.area;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.material.ESearchType;

public interface IInAreaFinderMap {

	boolean isBlocked(IPathCalculatable requester, int tileX, int tileY);

	boolean fitsSearchType(int tileX, int tileY, ESearchType searched, IPathCalculatable requester);

}
