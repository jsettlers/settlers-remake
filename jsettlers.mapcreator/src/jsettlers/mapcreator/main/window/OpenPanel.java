package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	 * List with all maps
	 */
	protected List<MapLoader> maps = MapList.getDefaultList().getFreshMaps().getItems();

	/**
	 * Constructor
	 * 
	 * @param doubleclickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 */
	public OpenPanel(final ActionListener doubleclickListener) {
		setLayout(new BorderLayout());

		sortMaps();

		this.mapList = new JList<MapLoader>(maps.toArray(new MapLoader[maps.size()]));
		mapList.setCellRenderer(new MapListCellRenderer());
		mapList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (doubleclickListener != null) {
						doubleclickListener.actionPerformed(new ActionEvent(e, 0, "dblclick"));
					}
				}
			}
		});
		add(new JScrollPane(mapList));

		if (maps.size() > 0) {
			mapList.setSelectedIndex(0);
		}
	}

	/**
	 * Order the maps
	 */
	protected void sortMaps() {
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
	}

	/**
	 * @return The selected map
	 */
	public MapLoader getSelectedMap() {
		return mapList.getSelectedValue();
	}

}
