package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;

public final class DiggerRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = -3781604767367556333L;

	public final IDiggerRequester requester;
	public byte amount;
	public byte creationRequested = 0;

	public DiggerRequest(IDiggerRequester requester, byte amount) {
		this.requester = requester;
		this.amount = amount;
	}

	@Override
	public final ShortPoint2D getPos() {
		return requester.getPos();
	}
}