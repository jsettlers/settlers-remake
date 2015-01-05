package jsettlers.common.statistics;

import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

/**
 * This class represents the statistics for a given player. It also contains the possibility to access and change player settings taht affect the
 * game.
 * 
 * @author michael
 */
public interface IPlayerStatistics {
	/**
	 * Gets a list of things the given material is needed for.
	 * 
	 * @param type
	 *            The material we want to know about.
	 * @return The list of consumers for this material.
	 */
	public List<IConsuming> getConsumers(EMaterialType type);

	/**
	 * Gets the number of materials that are available in the current partition. This is just used for information purposes.
	 * 
	 * @return The number of materials.
	 */
	public int getMaterialCount(EMaterialType material);

	/**
	 * Gets the number of movables that have the given type.
	 * 
	 * @param movable
	 *            The movable type to count
	 * @return The number of movables of that type.
	 */
	public int getMovableCount(EMovableType movable);

	/**
	 * Gets the rate settings for converting bearers to bricklayers.
	 * 
	 * @return
	 */
	public IConversionRate getBricklayerConverstionRate();

	/**
	 * Gets the rate settings for converting bearers to diggers.
	 * 
	 * @return
	 */
	public IConversionRate getDiggerConverstionRate();

	/**
	 * Gets the minimum rate for all bearers. At least this part of the overall population needs to be bearers.
	 * 
	 * @return The rate from 0..1.
	 */
	public float getMinimumBearerRate();

	/**
	 * Sets the bearer rate. The rate may be clamped internally by the logic.
	 * 
	 * @param rate
	 *            The rate, ranging from 0..1.
	 */
	public void setMinimumBearerRate(float rate);
}
