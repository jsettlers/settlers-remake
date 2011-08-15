package jsettlers.logic.algorithms.path.wrapper.requests;

import jsettlers.logic.algorithms.path.wrapper.IPathRequester;

public abstract class AbstractAStarRequest extends AbstractPathRequest {

	public AbstractAStarRequest(IPathRequester requester) {
		super(requester);
	}

	public abstract short getAStarTx();

	public abstract short getAStarTy();

}
