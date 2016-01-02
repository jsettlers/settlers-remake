package jsettlers.mapcreator.mapvalidator;

import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;

/**
 * Listener to get notified about the validation result
 * 
 * @author Andreas Butti
 */
public interface ValidationResultListener {

	/**
	 * Validation finished, update UI. Called from UI Thread
	 * 
	 * @param list
	 *            List with errors / results
	 */
	public void validationFinished(ValidationListModel list);
}
