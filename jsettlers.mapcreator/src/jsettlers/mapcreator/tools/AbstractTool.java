package jsettlers.mapcreator.tools;

import javax.swing.Icon;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.icons.ToolIcon;

/**
 * Base class for tools, loads the name and the Icon
 * 
 * @author Andreas Butti
 *
 */
public abstract class AbstractTool implements Tool {

	/**
	 * Icon of the tool
	 */
	private final Icon icon;

	/**
	 * Translated name of the tool
	 */
	private final String translatedName;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the tool, to load translation and icon
	 */
	public AbstractTool(String name) {
		icon = ToolIcon.loadIcon(name + ".png");
		translatedName = EditorLabels.getLabel("tool." + name);
	}

	@Override
	public String getName() {
		return translatedName;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

}
