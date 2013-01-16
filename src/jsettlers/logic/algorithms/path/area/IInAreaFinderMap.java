package jsettlers.logic.algorithms.path.area;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IInAreaFinderMap {

	boolean isBlocked(IPathCalculateable requester, int tileX, int tileY);

	boolean fitsSearchType(int tileX, int tileY, ESearchType searched, IPathCalculateable requester);

}
