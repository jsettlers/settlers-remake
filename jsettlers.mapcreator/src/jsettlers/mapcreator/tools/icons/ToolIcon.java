package jsettlers.mapcreator.tools.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Utility class to load Icons
 * 
 * @author Andreas Butti
 *
 */
public final class ToolIcon {

	/**
	 * Utility class
	 */
	private ToolIcon() {
	}

	/**
	 * Load an icon
	 * 
	 * @param name
	 *            Name
	 * @return Icon
	 */
	public static Icon loadIcon(String name) {
		return new ImageIcon(ToolIcon.class.getResource(name));
	}

}
