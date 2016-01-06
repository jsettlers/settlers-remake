package jsettlers.mapcreator.mapvalidator.result.fix;

import javax.swing.JPopupMenu;

/**
 * Interface to fix found errors
 * 
 * @author Andreas Butti
 *
 */
public abstract class AbstractFix {

	/**
	 * Fix data helper
	 */
	protected FixData data;

	/**
	 * Constructor
	 */
	public AbstractFix() {
	}

	/**
	 * @param data
	 *            Fix data helper
	 */
	public void setData(FixData data) {
		this.data = data;
	}

	/**
	 * Automatically fix the error, if clear what to do
	 */
	public void autoFix() {
	}

	/**
	 * @return true to show the fix menu
	 */
	public abstract boolean isFixAvailable();

	/**
	 * @return Popup Menu
	 */
	public abstract JPopupMenu getPopupMenu();

}
