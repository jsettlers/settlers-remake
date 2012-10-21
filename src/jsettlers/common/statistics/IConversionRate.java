package jsettlers.common.statistics;

/**
 * This class specifies how many bearers may be converted to a given type.
 * 
 * @author michael
 */
public interface IConversionRate {
	/**
	 * How many bearers may be converted to this type at maximum
	 * 
	 * @return A value from 0..1, defining a relative ammount to the sum of all
	 *         non-military people.
	 */
	public float getMaximumRate();

	/**
	 * Sets the rate of bearers that may be converted at maximum.
	 * 
	 * @param rate
	 */
	public void setMaximumRate(float rate);
}
