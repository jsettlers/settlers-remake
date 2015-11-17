package jsettlers.graphics.action;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} allows you to change the partition default settings for putting materials in stock.
 * 
 * @author Michael Zangl
 */
public class SetMaterialStockAcceptedAction extends PointAction {

	private EMaterialType material;
	private boolean accept;

	public SetMaterialStockAcceptedAction(ShortPoint2D mapPosition, EMaterialType material, boolean accept) {
		super(EActionType.SET_MATERIAL_STOCK_ACCEPTED, mapPosition);
		this.material = material;
		this.accept = accept;
	}

	/**
	 * @return Returns the position of the manager whose settings will be changed.
	 */
	@Override
	public ShortPoint2D getPosition() {
		return super.getPosition();
	}

	/**
	 * The material for which the setting should be changed.
	 * 
	 * @return The new material.
	 */
	public EMaterialType getMaterial() {
		return material;
	}

	/**
	 * The new setting for this material.
	 * 
	 * @return True if this material should be brought to stock buildings.
	 */
	public boolean shouldAccept() {
		return accept;
	}

	@Override
	public String toString() {
		return "SetMaterialStockAcceptedAction [material=" + material + ", accept=" + accept + ", getPosition()=" + getPosition() + "]";
	}
}
