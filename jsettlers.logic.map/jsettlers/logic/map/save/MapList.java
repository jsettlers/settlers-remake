package jsettlers.logic.map.save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.UIState;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapFileHeader.MapType;

/**
 * This is the main map list.
 * <p>
 * It lists all available maps, and it can be used to add maps to the game.
 * 
 * @author michael
 */
public class MapList {
	private static final String MAP_EXTENSION = ".map";
	private final ArrayList<MapLoader> freshMaps = new ArrayList<MapLoader>();
	private final ArrayList<MapLoader> savedMaps = new ArrayList<MapLoader>();
	private final File dir;

	public MapList(File dir) {
		this.dir = dir;
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File[] files = dir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException(
			        "map directory is not a directory.");
		}

		for (File file : files) {
			if (file.getName().endsWith(MAP_EXTENSION)) {
				addFileToList(file);
			} else if (file.getName().endsWith(".randommap")) {
				// TODO: compile randommap.
			}
		}
	}

	private void addFileToList(File file) {
		try {
			MapLoader loader = new MapLoader(file);
			MapType type = loader.getFileHeader().getType();
			if (type == MapType.SAVED_SINGLE) {
				savedMaps.add(loader);
			} else {
				freshMaps.add(loader);
			}
		} catch (MapLoadException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<MapLoader> getSavedMaps() {
		return savedMaps;
	}

	public ArrayList<MapLoader> getFreshMaps() {
		return freshMaps;
	}

	/**
	 * saves a static map to the given directory.
	 * 
	 * @param header
	 *            The header to use.
	 * @param data
	 *            The data to save.
	 * @throws IOException
	 *             If any IO error occurred.
	 */
	public void saveMap(MapFileHeader header, IMapData data) throws IOException {
		OutputStream out = null;
		try {
			out = getOutputStream(header);
			MapSaver.saveMap(header, data, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private OutputStream getOutputStream(MapFileHeader header) throws IOException {
		String name = header.getName().toLowerCase().replaceAll("\\W+", "");
		if (name.isEmpty()) {
			name = "map";
		}

		Date date = header.getDate();
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("-yyyy-MM-dd");
			name += format.format(date);
		}

		File file = new File(dir, name + MAP_EXTENSION);
		int i = 1;
		while (file.exists()) {
			file = new File(dir, name + "-" + i + MAP_EXTENSION);
			i++;
		}
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}

	public void saveMap(UIState state, MainGrid grid) {
		// TODO: implement map saving.
	}

	public void saveRandomMap(MapFileHeader header, String definition)
	        throws IOException {
		OutputStream out = getOutputStream(header);
		MapSaver.saveRandomMap(header, definition, out);
	}

	public ArrayList<MapLoader> getSavedMultiplayerMaps() {
		// TODO: save multiplayer maps, so that we can load them.
		return null;
	}

	/**
	 * gets the list of the default directory.
	 * 
	 * @return
	 */
	public static MapList getDefaultList() {
		File dir = new File(ResourceManager.getSaveDirectory(), "maps");
		return new MapList(dir);
	}
}
