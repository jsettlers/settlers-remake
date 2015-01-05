package jsettlers.common.statistics;

import jsettlers.common.material.EMaterialType;

/**
 * This is a job that consumes materials.
 * 
 * @author michael
 */
public interface IConsuming {
	/**
	 * Gets the thing the material is consumed for. If this method returns {@link EConsumingType#WORKING_BUILDING}, you can safely cast this object to
	 * {@link IConsumingBuildingType}
	 * 
	 * @return The type
	 */
	public EConsumingType getConsumingType();

	/**
	 * Gets the type of material that is consumed.
	 * 
	 * @return The material
	 */
	public EMaterialType getMaterialType();

	/**
	 * Sets the priority that should be used when deciding to which consumers the material is brought. If the priority is out of range, it is clamped
	 * silently.
	 * 
	 * @param priority
	 *            The priority to use. 0 means no goods, 1 means highest priority.
	 */
	public void setPriority(float priority);

	/**
	 * Gets the priority used for us,
	 * 
	 * @return The priority, in range 0..1.
	 */
	public float getPriority();
}
