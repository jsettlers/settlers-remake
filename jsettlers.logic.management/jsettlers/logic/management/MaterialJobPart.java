package jsettlers.logic.management;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;

/**
 * request or offer for a material
 * 
 * TODO: If the stack is destroyed, this job is not notified.
 * 
 * @author Andreas Eberle
 * 
 */
public class MaterialJobPart extends AbstractJobPart {

	private final EMaterialType materialType;
	private final ISPosition2D pos;

	/**
	 * 
	 * @param materialType
	 * @param pos
	 * @param player
	 */
	public MaterialJobPart(EMaterialType materialType, ISPosition2D pos, byte player) {
		super(player);
		this.materialType = materialType;
		this.pos = pos;
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

}
