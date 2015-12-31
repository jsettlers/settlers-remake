package jsettlers.mapcreator.main.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jsettlers.logic.map.save.loader.MapLoader;

/**
 * Render to open an existing map
 * 
 * @author Andreas Butti
 *
 */
public class MapListCellRenderer implements ListCellRenderer<MapLoader> {

	/**
	 * Selected background color
	 */
	private static final Color SELECTION_BACKGROUND = new Color(0x9EB1CD);

	/**
	 * Background even
	 */
	private static final Color BACKGROUND1 = Color.WHITE;

	/**
	 * Background odd
	 */
	private static final Color BACKGROUND2 = new Color(0xE0E0E0);

	/**
	 * Foreground
	 */
	private static final Color FOREGROUND = Color.BLACK;

	/**
	 * Main Panel
	 */
	private Box pContents = Box.createVerticalBox();

	/**
	 * Name of the Map
	 */
	private JLabel lbName = new JLabel();

	/**
	 * ID of the Map
	 */
	private JLabel lbMapId = new JLabel();

	/**
	 * Constructor
	 */
	public MapListCellRenderer() {
		pContents.add(lbName);
		pContents.add(lbMapId);

		pContents.setOpaque(true);
		pContents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		lbName.setFont(lbName.getFont().deriveFont(Font.BOLD));

		lbName.setForeground(FOREGROUND);
		lbMapId.setForeground(FOREGROUND);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MapLoader> list, MapLoader value, int index, boolean isSelected,
			boolean cellHasFocus) {
		lbName.setText(value.getMapName());
		lbMapId.setText(value.getMapId());
		// value.getCreationDate();
		// value.getDescription();
		// value.getImage()
		// value.getMinPlayers();
		// value.getMaxPlayers()

		if (isSelected) {
			pContents.setBackground(SELECTION_BACKGROUND);
		} else {
			if (index % 2 == 0) {
				pContents.setBackground(BACKGROUND1);
			} else {
				pContents.setBackground(BACKGROUND2);
			}
		}

		return pContents;
	}

	// @Override
	// public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	// IMapDefinition map = (IMapDefinition) value;
	// String longMapId = map.getMapId();
	// String shortMapId = longMapId == null ? "no map id" : longMapId.substring(0, Math.min(longMapId.length(), 8));
	// String displayName = map.getMapName() + " \t (" + shortMapId + ") created: " + map.getCreationDate();
	// return super.getListCellRendererComponent(mapList, displayName, index, isSelected, cellHasFocus);
	// }
}
