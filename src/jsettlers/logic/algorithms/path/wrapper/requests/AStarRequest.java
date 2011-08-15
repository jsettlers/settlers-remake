package jsettlers.logic.algorithms.path.wrapper.requests;

import jsettlers.logic.algorithms.path.wrapper.IPathRequester;

public class AStarRequest extends AbstractAStarRequest {

	private final short tx;
	private final short ty;

	public AStarRequest(IPathRequester requester, short tx, short ty) {
		super(requester);

		this.tx = tx;
		this.ty = ty;
	}

	@Override
	public short getAStarTx() {
		return tx;
	}

	@Override
	public short getAStarTy() {
		return ty;
	}

}
