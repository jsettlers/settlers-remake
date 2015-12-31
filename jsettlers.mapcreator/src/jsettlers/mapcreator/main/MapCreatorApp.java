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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.utils.MainUtils;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.main.window.MapHeaderEditorPanel;

/**
 * Entry point for Map Editor application
 */
public class MapCreatorApp {
	private static final MapFileHeader DEFAULT = new MapFileHeader(MapType.NORMAL, "new map", null, "", (short) 300, (short) 300, (short) 1,
			(short) 10, null, new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE]);
	private static final String[] GROUND_TYPES = new String[] { ELandscapeType.WATER8.toString(), ELandscapeType.GRASS.toString(),
			ELandscapeType.DRY_GRASS.toString(), ELandscapeType.SNOW.toString(), ELandscapeType.DESERT.toString(), };
	private final JFrame selectMapFrame;

	private MapCreatorApp() {
		JPanel newMap = createNewMapPanel();
		JPanel open = createOpenMapPanel();
		JPanel root = new JPanel();
		root.add(newMap);
		root.add(open);

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
		panel.add(mapList);

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
		final MapHeaderEditorPanel headerEditor = new MapHeaderEditorPanel(DEFAULT, true);
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
				new EditorControl(headerEditor.getHeader(), ELandscapeType.valueOf(groundType.getValue().toString()));
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
			new EditorControl(value);
			close();
		} catch (MapLoadException e) {
			JOptionPane.showMessageDialog(selectMapFrame, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Sets the look and feel to "Nimbus", looks the same for all platforms, and should be available on all plattforms
	 */
	private static void loadLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// could not be loaded, ignore error
		}
	}

	/**
	 * Set Up an exception handler for uncatcht exception
	 */
	private static void setupDefaultExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ErrorDisplay.displayError(e, "Unhandled error in Thread " + t.getName());
					}
				});
			}
		});
	}

	/**
	 * Startup from action properties
	 * 
	 * @param options
	 *            Options
	 * @return true on success
	 */
	private static boolean startupFromAction(OptionableProperties options) {
		try {
			String actionconfig = options.getProperty("actionconfig");
			if (actionconfig != null) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(actionconfig));
				String action = prop.getProperty("action");

				if (!"open".equals(action) && !"new".equals(action)) {
					return false;
				}

				if (Boolean.parseBoolean(options.getProperty("delete-actionconfig"))) {
					new File(actionconfig).delete();
				}

				if ("new".equals(action)) {
					// Sample property file:
					// action=new
					// map-name=OpenTest 1
					// lanscape-type=GRASS
					// width=100
					// height=100
					// min-player=1
					// max-player=3

					String mapName = prop.getProperty("map-name");
					String lanscapeType = prop.getProperty("lanscape-type");
					int width = Integer.parseInt(prop.getProperty("width"));
					int height = Integer.parseInt(prop.getProperty("height"));
					int minPlayer = Integer.parseInt(prop.getProperty("min-player"));
					int maxPlayer = Integer.parseInt(prop.getProperty("max-player"));
					MapFileHeader header = new MapFileHeader(MapType.NORMAL, mapName, null, "", (short) width, (short) height, (short) minPlayer,
							(short) maxPlayer, null, new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE]);

					new EditorControl(header, ELandscapeType.valueOf(lanscapeType));

				} else {
					String mapId = prop.getProperty("map-id");
					List<MapLoader> maps = MapList.getDefaultList().getFreshMaps().getItems();
					MapLoader toOpenMap = null;
					for (MapLoader m : maps) {
						if (mapId.equals(m.getMapId())) {
							toOpenMap = m;
							break;
						}
					}

					if (toOpenMap == null) {
						JOptionPane.showMessageDialog(null, "Could not find map with ID \"" + mapId + "\"", "JSettler", JOptionPane.ERROR_MESSAGE,
								null);
						return false;
					}

					new EditorControl(toOpenMap);
				}

				return true;
			}
		} catch (Exception e) {
			System.err.println("Could not read action properties");
			ErrorDisplay.displayError(e, "Failed to execute startup action");
		}
		return false;
	}

	/**
	 * Main
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			setupDefaultExceptionHandler();

			OptionableProperties options = MainUtils.loadOptions(args);
			SwingManagedJSettlers.setupResourceManagers(options, "config.prp");
			loadLookAndFeel();

			if (startupFromAction(options)) {
				return;
			}

			new MapCreatorApp();
		} catch (Exception e) {
			ErrorDisplay.displayError(e, "Error launching application");
		}
	}

}
