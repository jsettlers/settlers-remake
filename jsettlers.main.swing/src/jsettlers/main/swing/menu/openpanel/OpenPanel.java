/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.menu.openpanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.MapLoader;
import jsettlers.main.swing.lookandfeel.LFStyle;

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
	protected List<MapLoader> maps;

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
	 * Filter buttons
	 */
	private JPanel pFilter = new JPanel();

	/**
	 * Currently active filter
	 */
	private EMapFilter currentFilter = EMapFilter.ALL;

	/**
	 * Constructor
	 *
	 * @param maps
	 *            Maps to display
	 * @param doubleclickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 */
	public OpenPanel(final List<MapLoader> maps, final ActionListener doubleclickListener) {
		this(maps, doubleclickListener, new MapListCellRenderer());
	}

	/**
	 * Constructor
	 *
	 * @param maps
	 *            Maps to display
	 * @param doubleclickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 * @param cellRenderer
	 *            Cell renderer to use
	 */
	public OpenPanel(final List<MapLoader> maps, final ActionListener doubleclickListener, final ListCellRenderer<MapLoader> cellRenderer) {
		setMapLoadersWithoutSearchChanged(maps);
		setLayout(new BorderLayout());

		initFilter();

		this.txtSearch = new SearchTextField();
		txtSearch.putClientProperty(LFStyle.KEY, LFStyle.TEXT_DEFAULT);

		Box box = Box.createVerticalBox();
		box.add(pFilter);
		box.add(txtSearch);

		add(box, BorderLayout.NORTH);

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

		this.mapList = new JList<MapLoader>(listModelFiltered);
		mapList.setCellRenderer(cellRenderer);
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
		mapList.setOpaque(false);
		add(new JScrollPane(mapList), BorderLayout.CENTER);

		searchChanged();

		if (maps.size() > 0) {
			mapList.setSelectedIndex(0);
		}
	}

	public void setMapLoaders(final List<MapLoader> maps) {
		setMapLoadersWithoutSearchChanged(maps);
		searchChanged();
	}

	private void setMapLoadersWithoutSearchChanged(final List<MapLoader> maps) {
		this.maps = maps;
		this.mapsAvailable = maps.toArray(new MapLoader[maps.size()]);
		sortMaps();
	}

	/**
	 * Initialize the filter buttons
	 */
	private void initFilter() {
		JLabel filterLabel = new JLabel(Labels.getString("mapfilter.title"));
		filterLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		pFilter.add(filterLabel);

		boolean first = true;
		ButtonGroup group = new ButtonGroup();
		for (final EMapFilter filter : EMapFilter.values()) {
			JToggleButton bt = new JToggleButton(filter.getName());
			bt.putClientProperty(LFStyle.KEY, LFStyle.TOGGLE_BUTTON_STONE);
			bt.addActionListener(e -> {
				currentFilter = filter;
				searchChanged();
			});

			if (first) {
				first = false;
				bt.setSelected(true);
			}

			group.add(bt);
			pFilter.add(bt);
		}
	}

	/**
	 * Search has changed, update the list
	 */
	protected void searchChanged() {
		String search = txtSearch.getText().toLowerCase();

		listModelFiltered.clear();
		for (MapLoader m : mapsAvailable) {
			if (matchesSearch(m, search) && currentFilter.filter(m)) {
				listModelFiltered.addElement(m);
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
		if (search.isEmpty()) {
			return true;
		}

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

	/**
	 * @return true if there are no maps in the list
	 */
	public boolean isEmpty() {
		return maps.isEmpty();
	}
}
