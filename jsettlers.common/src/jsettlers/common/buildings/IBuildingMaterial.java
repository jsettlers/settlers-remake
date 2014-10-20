package jsettlers.common.buildings;

import jsettlers.common.material.EMaterialType;

/**
 * This is a material (stack) that is needed for a building.
 * 
 * @author michael
 */
public interface IBuildingMaterial {
	/**
	 * Gets the material type that this stack is for.
	 * 
	 * @return The type of material.
	 */
	public EMaterialType getMaterialType();

	/**
	 * Gets the amount of material this building has.
	 * 
	 * @return The number of material items on that given stack.
	 */
	public int getMaterialCount();

	/**
	 * If the current stack is offered, so that people can take material from
	 * here.
	 * 
	 * @return <code>true</code> if it is an offering stack, <code>false</code>
	 *         if it is an request stack.
	 */
	public boolean isOffering();
}
