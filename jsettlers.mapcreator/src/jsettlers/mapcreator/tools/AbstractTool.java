package jsettlers.mapcreator.tools;

import javax.swing.Icon;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.icons.ToolIcon;
import jsettlers.mapcreator.tools.shapes.ShapeType;

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
	protected Icon icon;

	/**
	 * Translated name of the tool
	 */
	protected String translatedName;

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

	/**
	 * Constructor, provide icon and name
	 * 
	 * @param icon
	 *            Icon
	 * @param translatedName
	 *            Translated name
	 */
	public AbstractTool(Icon icon, String translatedName) {
		this.icon = icon;
		this.translatedName = translatedName;
	}

	@Override
	public String getName() {
		return translatedName;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
		// can be overridden, but is mostly empty
	}

}
