package jsettlers.mapcreator.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.main.swing.ResourceProvider;

public class Main {
	private static final MapFileHeader DEFAULT = new MapFileHeader(
	        MapType.NORMAL, "new map", "", (short) 300, (short) 300, (short) 1,
	        (short) 10, null);
	private static final String[] GROUND_TYPES = new String[] {
	        ELandscapeType.WATER8.toString(),
	        ELandscapeType.GRASS.toString(),
	        ELandscapeType.DRY_GRASS.toString(),
	        ELandscapeType.SNOW.toString(),
	        ELandscapeType.DESERT.toString(),
	};
	private JFrame selectMapFrame;

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File(
		        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		ResourceManager.setProvider(new ResourceProvider());
	}

	public static void main(String[] args) {
		new Main();
	}

	private Main() {
		JPanel newMap = createNewMapPanel();
		JPanel open = createOpenMapPanel();
		JPanel root = new JPanel();
		root.add(newMap);
		root.add(open);

		selectMapFrame = new JFrame("Select map");
		selectMapFrame.add(root);
		selectMapFrame.pack();
		selectMapFrame.setVisible(true);

	}

	private JPanel createOpenMapPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Open map"));
		ArrayList<MapLoader> maps = MapList.getDefaultList().getFreshMaps();
		final JList mapList = new JList(maps.toArray());
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
		headerEditor
		        .setBorder(BorderFactory.createTitledBorder("Map settings"));

		JPanel ground = new JPanel();
		ground.setBorder(BorderFactory.createTitledBorder("Ground type"));
		panel.add(ground);

		final SpinnerListModel groundType =
		        new SpinnerListModel(Arrays.asList(GROUND_TYPES));
		JSpinner groundTypes = new JSpinner(groundType);
		ground.add(groundTypes);

		JButton createMapButton = new JButton("Create map");
		panel.add(createMapButton);
		createMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditorWindow(headerEditor.getHeader(), ELandscapeType
				        .valueOf(groundType.getValue().toString()));
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
