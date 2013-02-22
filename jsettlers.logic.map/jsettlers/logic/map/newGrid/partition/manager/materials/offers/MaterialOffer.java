package jsettlers.logic.map.newGrid.partition.manager.materials.offers;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.MaterialsManager;

/**
 * This class is used by {@link MaterialsManager} to store offers of materials.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialOffer implements Serializable, ILocatable {
	private static final long serialVersionUID = 8516955442065220998L;

	private final ShortPoint2D position;
	private byte amount = 0;

	MaterialOffer(ShortPoint2D position, byte amount) {
		this.position = position;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Offer: " + position + "    " + amount;
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	/**
	 * Increases the amount and returns the new value.
	 * 
	 * @return
	 */
	public byte incAmount() {
		return ++amount;
	}

	/**
	 * Decreases the amount and returns the new value.
	 * 
	 * @return
	 */
	public byte decAmount() {
		return --amount;
	}

	public byte getAmount() {
		return amount;
	}

}
