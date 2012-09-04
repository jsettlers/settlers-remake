package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

public final class MaterialOffer implements Serializable {
	private static final long serialVersionUID = 8516955442065220998L;

	public ShortPoint2D position;
	public EMaterialType materialType;
	public byte amount = 0;

	public MaterialOffer(ShortPoint2D position, EMaterialType materialType, byte amount) {
		this.position = position;
		this.materialType = materialType;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Offer: " + position + "   " + materialType + "    " + amount;
	}

}