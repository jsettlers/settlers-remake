package jsettlers.logic.map.save;

import java.io.BufferedOutputStream;
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
import jsettlers.logic.map.newGrid.GameSerializer;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapFileHeader.MapType;

/**
 * This is the main map list.
 * <p>
 * It lists all available maps, and it can be used to add maps to the game.
 * <p>
 * TODO: load maps before they are needed, to increase startup time.
 * 
 * @author michael
 */
public class MapList {
	private static final String MAP_EXTENSION = ".map";
	private final ArrayList<MapLoader> freshMaps = new ArrayList<MapLoader>();
	private final ArrayList<MapLoader> savedMaps = new ArrayList<MapLoader>();
	private final File dir;
	private boolean fileListLoaded = false;

	public MapList(File dir) {
		this.dir = dir;
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}

	private void loadFileList() {
		freshMaps.clear();
		savedMaps.clear();

		File[] files = dir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("map directory is not a directory.");
		}

		for (File file : files) {
			if (file.getName().endsWith(MAP_EXTENSION)) {
				addFileToList(file);
			}
		}
	}

	private synchronized void addFileToList(File file) {
		try {
			MapLoader loader = new MapLoader(file);
			MapType type = loader.getFileHeader().getType();
			if (type == MapType.SAVED_SINGLE) {
				savedMaps.add(loader);
			} else {
				freshMaps.add(loader);
			}
		} catch (MapLoadException e) {
			System.err.println("Cought exception while loading header for " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	public synchronized ArrayList<MapLoader> getSavedMaps() {
		if (!fileListLoaded) {
			loadFileList();
			fileListLoaded = true;
		}
		return savedMaps;
	}

	public synchronized ArrayList<MapLoader> getFreshMaps() {
		if (!fileListLoaded) {
			loadFileList();
			fileListLoaded = true;
		}
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
	public synchronized void saveMap(MapFileHeader header, IMapData data) throws IOException {
		OutputStream out = null;
		try {
			out = getOutputStream(header);
			MapSaver.saveMap(header, data, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		loadFileList();
	}

	/**
	 * Gets an output stream that can be used to store the map. The stream is to a file with a nice name and does not override any other file.
	 * 
	 * @param header
	 *            The header to create the file name from. It is not written to the stream.
	 * @return A output stream to a fresh generated file.
	 * @throws IOException
	 */
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
			return new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Saves a map to disk. The map logic should be paused while calling this method.
	 * 
	 * @param state
	 * @param grid
	 * @throws IOException
	 */
	public synchronized void saveMap(UIState state, MainGrid grid) throws IOException {
		MapFileHeader header = grid.generateSaveHeader();
		OutputStream out = getOutputStream(header);
		header.writeTo(out);
		state.writeTo(out);
		GameSerializer gameSerializer = new GameSerializer();
		gameSerializer.save(grid, out);

		loadFileList();
	}

	/**
	 * Saves a random map to the given file.
	 * 
	 * @param header
	 *            The header to save
	 * @param definition
	 *            The random map rule text.
	 * @throws IOException
	 */
	public synchronized void saveRandomMap(MapFileHeader header, String definition) throws IOException {
		OutputStream out = getOutputStream(header);
		MapSaver.saveRandomMap(header, definition, out);
		loadFileList();
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
		return new MapList(getDefaultFolder());
	}

	public static File getDefaultFolder() {
		return new File(ResourceManager.getSaveDirectory(), "maps");
	}
}
