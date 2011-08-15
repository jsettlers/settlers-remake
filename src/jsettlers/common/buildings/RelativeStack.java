package jsettlers.common.buildings;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.RelativePoint;

/**
 * This is a stack request that can be positioned relatively to a building.
 * 
 * @author michael
 */
public class RelativeStack extends RelativePoint {

	/**
     * 
     */
	private static final long serialVersionUID = -6513966510581344171L;

	private final EMaterialType type;

	private final short requiredForBuild;

	public RelativeStack(int dx, int dy, EMaterialType type, short requiredForBuild) {
		super(dx, dy);
		this.type = type;
		this.requiredForBuild = requiredForBuild;
	}

	public EMaterialType getType() {
		return type;
	}

	/**
	 * If this property is not zero, the specified amount of this material is needed to build the building. If it is 0, the material is needed after
	 * building the building.
	 * 
	 * @return 0 if the material is not for the building pahse, the required material else.
	 */
	public short requiredForBuild() {
		return requiredForBuild;
	}
}
