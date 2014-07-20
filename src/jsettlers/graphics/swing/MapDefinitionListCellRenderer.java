package jsettlers.graphics.swing;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jsettlers.graphics.startscreen.interfaces.IMapDefinition;

/**
 * Renderer for IMapDefintion
 * 
 * @author Andreas Eberle
 * 
 */
public class MapDefinitionListCellRenderer implements ListCellRenderer<IMapDefinition> {

	@Override
	public Component getListCellRendererComponent(JList<? extends IMapDefinition> list, IMapDefinition map, int index, boolean isSelected,
			boolean hasFocus) {
		return new MapDefintionPanel(map.getName(), map.getDescription(), map.getCreationDate(), isSelected);
	}
}
