package jsettlers.logic.algorithms.path.wrapper.requests;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;

public class InAreaRequest extends AbstractAStarRequest {

	private final ESearchType type;

	private final short cx;
	private final short cy;
	private final short tileRadius;

	private ISPosition2D aStarPos;

	/**
	 * constructor of class
	 * 
	 * @param requester
	 *            requesting actor
	 * @param sx
	 *            start position of requester
	 * @param sy
	 *            start position of requester
	 * @param cx
	 *            center position of circle to be searched
	 * @param cy
	 *            center position of circle to be searched
	 * @param tileRadius
	 *            radius of pixels to be searched
	 * @param type
	 *            SearchOptions object to be found
	 */
	public InAreaRequest(IPathRequester requester, short cx, short cy, short tileRadius, ESearchType type) {
		super(requester);

		this.cx = cx;
		this.cy = cy;
		this.tileRadius = tileRadius;
		this.type = type;
	}

	public ESearchType getType() {
		return type;
	}

	public short getCx() {
		return cx;
	}

	public short getCy() {
		return cy;
	}

	public short getTileRadius() {
		return tileRadius;
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
