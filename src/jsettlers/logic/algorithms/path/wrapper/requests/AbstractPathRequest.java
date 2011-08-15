package jsettlers.logic.algorithms.path.wrapper.requests;

import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;

public abstract class AbstractPathRequest {

	private final IPathRequester requester;

	protected AbstractPathRequest(IPathRequester requester) {
		this.requester = requester;
	}

	public short getSx() {
		return requester.getPos().getX();
	}

	public short getSy() {
		return requester.getPos().getY();
	}

	public IPathRequester getRequester() {
		return requester;
	}

	public void setFoundPath(Path path) {
		requester.setCalculatedPath(path);
	}

	public void setFailed() {
		requester.pathRequestFailed();
	}

}
