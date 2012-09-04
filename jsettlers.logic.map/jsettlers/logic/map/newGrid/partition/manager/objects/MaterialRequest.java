package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

public final class MaterialRequest implements Comparable<MaterialRequest>, Serializable, ILocatable {
	private static final long serialVersionUID = -3427364937835501076L;

	public final IMaterialRequester requester;
	public final EMaterialType requested;
	public byte priority = 100;

	public MaterialRequest(IMaterialRequester requester, EMaterialType requested, byte priority) {
		this.requester = requester;
		this.requested = requested;
		this.priority = priority;
	}

	@Override
	public int compareTo(MaterialRequest other) {
		return other.priority - this.priority;
	}

	@Override
	public String toString() {
		return requested + "   " + requester.getPos() + "    " + priority;
	}

	@Override
	public ShortPoint2D getPos() {
		return requester.getPos();
	}
}