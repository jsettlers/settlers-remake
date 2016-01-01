package jsettlers.mapcreator.tools.icons;

import java.net.URL;

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
	 * Load an icon, no chache!
	 * 
	 * @param name
	 *            Name
	 * @return Icon
	 */
	public static Icon loadIcon(String name) {
		URL url = ToolIcon.class.getResource(name);
		if (url == null) {
			return null;
		}
		return new ImageIcon(url);
	}

}
