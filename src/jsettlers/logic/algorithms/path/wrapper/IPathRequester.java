package jsettlers.logic.algorithms.path.wrapper;

import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;

public interface IPathRequester extends IPathCalculateable {
	/**
	 * sets the path that has been calculated to the requester
	 * 
	 * @param path
	 */
	void setCalculatedPath(Path path);

	void pathRequestFailed();
}
