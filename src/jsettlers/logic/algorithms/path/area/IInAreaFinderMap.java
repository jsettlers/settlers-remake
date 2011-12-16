package jsettlers.logic.algorithms.path.area;

import jsettlers.common.material.ESearchType;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IInAreaFinderMap {

	boolean isBlocked(IPathCalculateable requester, short tileX, short tileY);

	boolean fitsSearchType(short tileX, short tileY, ESearchType searched, IPathCalculateable requester);

}
