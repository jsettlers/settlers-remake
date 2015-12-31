package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;

/**
 * Panel to open an existing map
 * 
 * @author Andreas Butti
 *
 */
public class OpenPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * List with the Maps to select
	 */
	private JList<MapLoader> mapList;

	/**
	 * Constructor
	 */
	public OpenPanel() {
		setLayout(new BorderLayout());

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
		mapList.setCellRenderer(new MapListCellRenderer());
		add(new JScrollPane(mapList));

		if (maps.size() > 0) {
			mapList.setSelectedIndex(0);
		}
	}

	/**
	 * @return The selected map
	 */
	public MapLoader getSelectedMap() {
		return mapList.getSelectedValue();
	}

}
