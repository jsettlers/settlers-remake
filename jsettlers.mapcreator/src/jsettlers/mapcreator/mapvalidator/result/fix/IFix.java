package jsettlers.mapcreator.mapvalidator.result.fix;

import javax.swing.JPopupMenu;

/**
 * Interface to fix found errors
 * 
 * @author Andreas Butti
 *
 */
public interface IFix {

	/**
	 * @return true to show the fix menu
	 */
	public boolean isFixAvailable();

	/**
	 * @return Popup Menu
	 */
	public JPopupMenu getPopupMenu();

}
