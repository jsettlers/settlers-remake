package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.mapcreator.localization.EditorLabels;

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
	 * Count of players
	 */
	private JLabel lbPlayerCount = new JLabel();

	/**
	 * ID of the Map and the creation date
	 */
	private JLabel lbMapId = new JLabel();

	/**
	 * Description of the Map
	 */
	private JLabel lbDescription = new JLabel();

	/**
	 * Format for date display
	 */
	private SimpleDateFormat df = new SimpleDateFormat(EditorLabels.getLabel("date.date-only"));

	/**
	 * Constructor
	 */
	public MapListCellRenderer() {
		JPanel pFirst = new JPanel();
		pFirst.setOpaque(false);
		pFirst.setLayout(new BorderLayout(5, 0));
		pFirst.add(lbName, BorderLayout.CENTER);
		pFirst.add(lbPlayerCount, BorderLayout.EAST);
		pFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
		pContents.add(pFirst);
		pContents.add(lbMapId);
		lbMapId.setAlignmentX(Component.LEFT_ALIGNMENT);
		pContents.add(lbDescription);
		lbDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

		pContents.setOpaque(true);
		pContents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		lbName.setFont(lbName.getFont().deriveFont(Font.BOLD));
		lbName.setAlignmentX(Component.LEFT_ALIGNMENT);

		lbName.setForeground(FOREGROUND);
		lbMapId.setForeground(FOREGROUND);
		lbDescription.setForeground(FOREGROUND);
		lbPlayerCount.setForeground(Color.BLUE);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MapLoader> list, MapLoader value, int index, boolean isSelected,
			boolean cellHasFocus) {
		lbName.setText(value.getMapName());
		String date = "???";
		if (value.getCreationDate() != null) {
			date = df.format(value.getCreationDate());
		}

		lbMapId.setText(date + " / " + value.getMapId());
		lbPlayerCount.setText("[" + value.getMinPlayers() + " - " + value.getMaxPlayers() + "]");

		if (value.getDescription() != null && !value.getDescription().isEmpty()) {
			lbDescription.setText(value.getDescription());
		} else {
			lbDescription.setText("<no description>");
		}
		// TODO image seems to be not implemented... may display it if available
		// value.getImage()

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
}
