package jsettlers.mapcreator.mapvalidator.result;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import jsettlers.common.position.ShortPoint2D;

/**
 * List with validation errors
 * 
 * @author Andreas Butti
 *
 */
public class ValidationList extends DefaultListModel<AbstractErrorEntry> {
	private static final long serialVersionUID = 1L;

	/**
	 * Error count without headers
	 */
	private int errorCount;

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
	 * @param typeId
	 *            Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public void addError(String text, ShortPoint2D pos, String typeId) {
		addElement(new ErrorEntry(text, pos, typeId));
	}

	/**
	 * Remove duplicate header
	 */
	public void removeDuplicateHeader() {
		boolean lastWasHeader = true;
		for (int i = getSize() - 1; i >= 0; i--) {
			AbstractErrorEntry e = getElementAt(i);

			if (lastWasHeader && e instanceof ErrorHeader) {
				removeElementAt(i);

				if (i < 1) {
					return;
				}

				lastWasHeader = getElementAt(i - 1) instanceof ErrorHeader;
				continue;
			}

			lastWasHeader = e instanceof ErrorHeader;
		}
	}

	/**
	 * Remove duplicate entries which are near together - this is only working, because there are always header between to different entries
	 */
	public void romoveDuplicateNear() {
		if (getSize() < 1) {
			return;
		}

		ArrayList<AbstractErrorEntry> toDelete = new ArrayList<>();

		AbstractErrorEntry lastEntryUnknown = null;

		for (int i = getSize() - 1; i >= 0; i--) {
			AbstractErrorEntry e = getElementAt(i);

			if (lastEntryUnknown == null || !(e instanceof ErrorEntry) || !(lastEntryUnknown instanceof ErrorEntry)) {
				lastEntryUnknown = e;
				continue;
			}

			ErrorEntry lastEntry = (ErrorEntry) lastEntryUnknown;
			ErrorEntry entry = (ErrorEntry) e;
			lastEntryUnknown = e;

			// two different error types
			if (!lastEntry.getTypeId().equals(entry.getTypeId())) {
				continue;
			}

			ShortPoint2D p1 = entry.getPos();
			ShortPoint2D p2 = lastEntry.getPos();

			if (p1.getOnGridDistTo(p2) < 10) {
				toDelete.add(lastEntry);
			}
		}

		// not so efficent...
		for (AbstractErrorEntry t : toDelete) {
			removeElement(t);
		}
	}

	/**
	 * Remove duplicate header, bring similar errors at the same location togther etc.
	 */
	public void prepareToDisplay() {
		romoveDuplicateNear();
		removeDuplicateHeader();

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
