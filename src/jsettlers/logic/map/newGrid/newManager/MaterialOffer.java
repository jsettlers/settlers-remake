package jsettlers.logic.map.newGrid.newManager;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

public final class MaterialOffer implements Serializable, ILocatable {
	private static final long serialVersionUID = 8516955442065220998L;

	ShortPoint2D position;
	EMaterialType materialType;
	byte amount = 0;

	public MaterialOffer(ShortPoint2D position, EMaterialType materialType, byte amount) {
		this.position = position;
		this.materialType = materialType;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Offer: " + position + "   " + materialType + "    " + amount;
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public ShortPoint2D getPosition() {
		return position;
	}
}
