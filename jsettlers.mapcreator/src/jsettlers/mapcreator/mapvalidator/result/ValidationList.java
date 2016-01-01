package jsettlers.mapcreator.mapvalidator.result;

import javax.swing.DefaultListModel;

import jsettlers.common.position.ShortPoint2D;

/**
 * List with validation errors
 * 
 * @author Andreas Butti
 *
 */
public class ValidationList extends DefaultListModel<AbstarctErrorEntry> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public ValidationList() {
	}

	/**
	 * Add a header entry, will be automatically removed if there are no followed tasks
	 * 
	 * @param header
	 *            Header
	 */
	public void addHeader(String header) {
		addElement(new ErrorHeader(header));
	}

	/**
	 * Add an error entry
	 * 
	 * @param text
	 *            Text to display
	 * @param pos
	 *            Position
	 */
	public void addError(String text, ShortPoint2D pos) {
		addElement(new ErrorEntry(text, pos));
	}

	/**
	 * Remove duplicate header entries etc.
	 */
	public void prepareToDisplay() {
		boolean lastWasHeader = true;
		for (int i = getSize() - 1; i >= 0; i--) {
			AbstarctErrorEntry e = getElementAt(i);

			if (lastWasHeader && e instanceof ErrorHeader) {
				removeElementAt(i);
				i--;

				if (i < 0) {
					return;
				}

				lastWasHeader = getElementAt(i) instanceof ErrorHeader;
				continue;
			}

			lastWasHeader = e instanceof ErrorHeader;
		}
	}
}
