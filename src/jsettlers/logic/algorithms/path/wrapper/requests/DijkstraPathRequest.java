package jsettlers.logic.algorithms.path.wrapper.requests;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;

public class DijkstraPathRequest extends AbstractAStarRequest {

	private final short searchRadius;
	private final ESearchType type;
	private final short centerX;
	private final short centerY;

	private ISPosition2D aStarPos;

	public DijkstraPathRequest(IPathRequester requester, short centerX, short centerY, short searchRadius, ESearchType type) {
		super(requester);
		this.centerX = centerX;
		this.centerY = centerY;

		this.searchRadius = searchRadius;
		this.type = type;
	}

	public short getSearchRadius() {
		return searchRadius;
	}

	public ESearchType getType() {
		return type;
	}

	public short getCenterX() {
		return centerX;
	}

	public short getCenterY() {
		return centerY;
	}

	@Override
	public short getAStarTx() {
		return aStarPos.getX();
	}

	@Override
	public short getAStarTy() {
		return aStarPos.getY();
	}

	public void setAStartTarget(ISPosition2D aStarPos) {
		this.aStarPos = aStarPos;
	}

}
