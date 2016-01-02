package jsettlers.mapcreator.mapvalidator.result;

import javax.swing.DefaultListModel;

/**
 * Listmodel with validation errors
 * 
 * @author Andreas Butti
 *
 */
public class ValidationListModel extends DefaultListModel<AbstractErrorEntry> {
	private static final long serialVersionUID = 1L;

	/**
	 * Error count without headers
	 */
	private int errorCount;

	/**
	 * Constructor
	 */
	public ValidationListModel() {
	}

	/**
	 * Remove duplicate header, bring similar errors at the same location togther etc.
	 */
	public void prepareToDisplay() {
		errorCount = 0;
		for (int i = 0; i < getSize(); i++) {
			if (getElementAt(i) instanceof ErrorEntry) {
				errorCount++;
			}
		}
	}

	/**
	 * @return Error count without headers
	 */
	public int getErrorCount() {
		return errorCount;
	}
}
