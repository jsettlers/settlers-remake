package jsettlers.mapcreator.mapvalidator;

import jsettlers.common.position.ShortPoint2D;

/**
 * Listener to get notified about the validation result
 * 
 * @author Andreas Butti
 */
public interface ValidationResultReceiver2 {

	/**
	 * Validation has finished, called from UI Thread
	 * 
	 * @param name
	 *            Error string, empty if no errors on the map
	 * @param successful
	 *            true if the map is valid
	 * @param resultPosition
	 *            Last error
	 */
	public void validationFinished(String name, boolean successful, ShortPoint2D resultPosition);
}
