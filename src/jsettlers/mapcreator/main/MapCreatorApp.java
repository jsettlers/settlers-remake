package jsettlers.mapcreator.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.JoglLibraryPathInitializer;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;

public class MapCreatorApp {
	private static final MapFileHeader DEFAULT = new MapFileHeader(MapType.NORMAL, "new map", "", (short) 300, (short) 300, (short) 1, (short) 10,
			null, new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE ]);
	private static final String[] GROUND_TYPES = new String[] { ELandscapeType.WATER8.toString(), ELandscapeType.GRASS.toString(),
			ELandscapeType.DRY_GRASS.toString(), ELandscapeType.SNOW.toString(), ELandscapeType.DESERT.toString(), };
	private JFrame selectMapFrame;

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		ResourceManager.setProvider(new SwingResourceProvider());
	}

	public static void main(String[] args) {
		new MapCreatorApp();
	}

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
		ArrayList<MapLoader> maps = MapList.getDefaultList().getFreshMaps();
		Object[] array = maps.toArray();
		Arrays.sort(array, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				if (arg0 instanceof MapLoader && arg1 instanceof MapLoader) {
					MapLoader mapLoader1 = (MapLoader) arg0;
					MapLoader mapLoader2 = (MapLoader) arg1;
					int nameComp = mapLoader1.getName().compareTo(mapLoader2.getName());
					if (nameComp != 0) {
						return nameComp;
					} else {
						return mapLoader1.toString().compareTo(mapLoader2.toString());
					}
				} else {
					return 0;
				}
			}
		});
		final JList mapList = new JList(array);
		panel.add(mapList);

		JButton button = new JButton("Open");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object value = mapList.getSelectedValue();
				if (value instanceof MapLoader) {
					loadMap((MapLoader) value);
				}
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
