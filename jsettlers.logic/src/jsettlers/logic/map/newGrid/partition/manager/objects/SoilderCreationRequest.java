package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;

public final class SoilderCreationRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = -3108188242025391145L;

	public final IBarrack barrack;

	public SoilderCreationRequest(IBarrack barrack) {
		this.barrack = barrack;
	}

	@Override
	public String toString() {
		return "SoilderCreationRequest[" + barrack + "|" + barrack.getDoor() + "]";
	}

	@Override
	public ShortPoint2D getPos() {
		return barrack.getDoor();
	}

	public IBarrack getBarrack() {
		return barrack;
	}
}