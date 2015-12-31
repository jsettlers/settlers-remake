package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Dialog to open an existing map
 * 
 * @author Andreas Butti
 */
public class OpenExistingDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * List with the Maps to select
	 */
	private JList<MapLoader> mapList;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public OpenExistingDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("openfile.header"));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		List<MapLoader> maps = MapList.getDefaultList().getFreshMaps().getItems();
		Collections.sort(maps, new Comparator<MapLoader>() {
			@Override
			public int compare(MapLoader mapLoader1, MapLoader mapLoader2) {
				int nameComp = mapLoader1.getMapName().compareTo(mapLoader2.getMapName());
				if (nameComp != 0) {
					return nameComp;
				} else {
					return mapLoader1.toString().compareTo(mapLoader2.toString());
				}
			}
		});

		this.mapList = new JList<MapLoader>(maps.toArray(new MapLoader[maps.size()]));
		mapList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				IMapDefinition map = (IMapDefinition) value;
				String longMapId = map.getMapId();
				String shortMapId = longMapId == null ? "no map id" : longMapId.substring(0, Math.min(longMapId.length(), 8));
				String displayName = map.getMapName() + " \t   (" + shortMapId + ")      created: " + map.getCreationDate();
				return super.getListCellRendererComponent(mapList, displayName, index, isSelected, cellHasFocus);
			}
		});
		panel.add(mapList);

		add(panel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return The selected map ID
	 */
	public String getSelectedMapId() {
		return mapList.getSelectedValue().getMapId();
	}
}
