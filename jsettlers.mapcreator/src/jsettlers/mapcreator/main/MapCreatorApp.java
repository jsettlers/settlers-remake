/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.mapcreator.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;

import jsettlers.main.swing.SwingManagedJSettlers;

public class MapCreatorApp {
	private static final MapFileHeader DEFAULT = new MapFileHeader(MapType.NORMAL, "new map", null, "", (short) 300, (short) 300, (short) 1,
			(short) 10, null, new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE]);
	private static final String[] GROUND_TYPES = new String[] { ELandscapeType.WATER8.toString(), ELandscapeType.GRASS.toString(),
			ELandscapeType.DRY_GRASS.toString(), ELandscapeType.SNOW.toString(), ELandscapeType.DESERT.toString(), };
	private final JFrame selectMapFrame;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		SwingManagedJSettlers.setupResourceManagers(MainUtils.loadOptions(args), "config.prp");
		new MapCreatorApp();
	}

	private MapCreatorApp() {
		JPanel newMap = createNewMapPanel();
		JPanel open = createOpenMapPanel();
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout());
		root.add(newMap, BorderLayout.WEST);
		root.add(open, BorderLayout.CENTER);

		selectMapFrame = new JFrame("Select map");
		selectMapFrame.add(root);
		selectMapFrame.pack();
		selectMapFrame.setVisible(true);
		selectMapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JPanel createOpenMapPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Open map"));

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

		final JList<MapLoader> mapList = new JList<MapLoader>(maps.toArray(new MapLoader[maps.size()]));
		mapList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 648829725137437178L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				IMapDefinition map = (IMapDefinition) value;
				String longMapId = map.getMapId();
				String shortMapId = longMapId == null ? "no map id" : longMapId.substring(0, Math.min(longMapId.length(), 8));
				String displayName = map.getMapName() + " \t   (" + shortMapId + ")      created: " + map.getCreationDate();
				return super.getListCellRendererComponent(mapList, displayName, index, isSelected, cellHasFocus);
			}
		});
		panel.add(new JScrollPane(mapList));

		JButton button = new JButton("Open");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadMap(mapList.getSelectedValue());
			}
		});
		panel.add(button, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createNewMapPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		final MapHeaderEditor headerEditor = new MapHeaderEditor(DEFAULT, true);
		panel.add(headerEditor);
		headerEditor.setBorder(BorderFactory.createTitledBorder("Map settings"));

		JPanel ground = new JPanel();
		ground.setBorder(BorderFactory.createTitledBorder("Ground type"));
		panel.add(ground);

		final SpinnerListModel groundType = new SpinnerListModel(Arrays.asList(GROUND_TYPES));
		JSpinner groundTypes = new JSpinner(groundType);
		ground.add(groundTypes);

		JButton createMapButton = new JButton("Create map");
		panel.add(createMapButton);
		createMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditorWindow(headerEditor.getHeader(), ELandscapeType.valueOf(groundType.getValue().toString()));
				close();
			}
		});
		panel.add(Box.createRigidArea(new Dimension(20, 20)));
		return panel;
	}

	private void close() {
		selectMapFrame.setVisible(false);
		selectMapFrame.dispose();
	}

	protected void loadMap(MapLoader value) {
		try {
			new EditorWindow(value);
			close();
		} catch (MapLoadException e) {
			JOptionPane.showMessageDialog(selectMapFrame, e.getMessage());
			e.printStackTrace();
		}
	}
}
