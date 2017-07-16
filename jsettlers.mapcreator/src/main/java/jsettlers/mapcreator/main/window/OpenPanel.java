/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
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
	 * @param doubleClickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 */
	public OpenPanel(final ActionListener doubleClickListener) {
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
		this.mapList = new JList<>(listModelFiltered);
		mapList.setCellRenderer(new MapListCellRenderer());
		mapList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (doubleClickListener != null) {
						doubleClickListener.actionPerformed(new ActionEvent(e, 0, "dblclick"));
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
		String search = txtSearch.getText().toLowerCase(Locale.ENGLISH);

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
		if (m.getMapName().toLowerCase(Locale.ENGLISH).contains(search)) {
			return true;
		}
		if (m.getDescription().toLowerCase(Locale.ENGLISH).contains(search)) {
			return true;
		}
		return m.getMapId().toLowerCase(Locale.ENGLISH).contains(search);

	}

	/**
	 * Order the maps
	 */
	protected void sortMaps() {
		Collections.sort(maps, (mapLoader1, mapLoader2) -> {
			int nameComp = mapLoader1.getMapName().compareTo(mapLoader2.getMapName());
			if (nameComp != 0) {
				return nameComp;
			} else {
				return mapLoader1.toString().compareTo(mapLoader2.toString());
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
