package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapList;
import jsettlers.mapcreator.main.window.search.SearchTextField;

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
	 * Unfiltered map list
	 */
	private MapLoader[] mapsAvailable;

	/**
	 * Filtered list model
	 */
	private DefaultListModel<MapLoader> listModelFiltered = new DefaultListModel<>();

	/**
	 * Search Textfield
	 */
	private SearchTextField txtSearch;

	/**
	 * Constructor
	 * 
	 * @param doubleclickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 */
	public OpenPanel(final ActionListener doubleclickListener) {
		setLayout(new BorderLayout());

		sortMaps();

		this.txtSearch = new SearchTextField();
		add(txtSearch, BorderLayout.NORTH);
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				searchChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				searchChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				searchChanged();
			}
		});

		this.mapsAvailable = maps.toArray(new MapLoader[maps.size()]);
		this.mapList = new JList<MapLoader>(listModelFiltered);
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
		add(new JScrollPane(mapList), BorderLayout.CENTER);

		searchChanged();

		if (maps.size() > 0) {
			mapList.setSelectedIndex(0);
		}
	}

	/**
	 * Search has changed, update the list
	 */
	protected void searchChanged() {
		String search = txtSearch.getText().toLowerCase();

		if (search.isEmpty()) {
			listModelFiltered.clear();
			for (MapLoader m : mapsAvailable) {
				listModelFiltered.addElement(m);
			}
		} else {
			listModelFiltered.clear();
			for (MapLoader m : mapsAvailable) {
				if (matchesSearch(m, search)) {
					listModelFiltered.addElement(m);
				}
			}
		}
	}

	/**
	 * Checks if a map matches the search criteria
	 * 
	 * @param m
	 *            Map
	 * @param search
	 *            Criteria
	 * @return true if yes, false if no
	 */
	private boolean matchesSearch(MapLoader m, String search) {
		if (m.getMapName().toLowerCase().contains(search)) {
			return true;
		}
		if (m.getDescription().toLowerCase().contains(search)) {
			return true;
		}
		if (m.getMapId().toLowerCase().contains(search)) {
			return true;
		}

		return false;
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
