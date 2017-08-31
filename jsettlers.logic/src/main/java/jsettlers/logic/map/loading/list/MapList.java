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
package jsettlers.logic.map.loading.list;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.input.PlayerState;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.GameSerializer;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.list.IMapLister.IMapListerCallable;
import jsettlers.logic.map.loading.newmap.FreshMapSerializer;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.newmap.MapFileHeader.MapType;
import jsettlers.logic.map.loading.newmap.RemakeMapLoader;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * This is the main map list.
 * <p>
 * It lists all available maps, and it can be used to add maps to the game.
 * <p>
 * TODO: load maps before they are needed, to decrease startup time.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public class MapList implements IMapListerCallable {

	/**
	 * Gives the currently used map extension for saving a map.
	 * 
	 * @return
	 */
	public static String getMapExtension() {
		return CommonConstants.USE_SAVEGAME_COMPRESSION ? MapLoader.MAP_EXTENSION_COMPRESSED : MapLoader.MAP_EXTENSION;
	}

	private static IMapListFactory mapListFactory = new DefaultMapListFactory();

	private static MapList defaultList;

	private final ArrayList<IMapLister> mapDirectories;
	private final IMapLister saveDirectory;

	private final ChangingList<MapLoader> freshMaps = new ChangingList<>();
	private final ChangingList<RemakeMapLoader> savedMaps = new ChangingList<>();

	private boolean fileListLoaded = false;

	public MapList(Collection<IMapLister> mapDirectories, IMapLister saveDirectory) {
		this.mapDirectories = new ArrayList<>(mapDirectories);
		this.saveDirectory = saveDirectory;
	}

	private void loadFileList() {
		freshMaps.clear();
		savedMaps.clear();

		for (IMapLister dir : mapDirectories) {
			dir.listMaps(this);
		}
	}

	@Override
	public synchronized void foundMap(IListedMap map) {
		MapLoader loader;

		try {
			loader = MapLoader.getLoaderForListedMap(map);
		} catch (Exception e) {
			System.err.println("Cought exception while loading header for " + map.getFileName());
			e.printStackTrace();
			return;
		}

		MapFileHeader mapHead = loader.getFileHeader();

		// - if the map can't be load (e.g. caused by wrong format) the mapHead gets NULL! -> hide/ignore this map from user
		if (mapHead != null) {
			MapType type = loader.getFileHeader().getType();

			if ((type == MapType.SAVED_SINGLE)) {
				savedMaps.add((RemakeMapLoader) loader);
			} else {
				freshMaps.add(loader);
			}
		}
	}

	public synchronized ChangingList<RemakeMapLoader> getSavedMaps() {
		if (!fileListLoaded) {
			loadFileList();
			fileListLoaded = true;
		}
		return savedMaps;
	}

	public synchronized ChangingList<MapLoader> getFreshMaps() {
		if (!fileListLoaded) {
			loadFileList();
			fileListLoaded = true;
		}
		return freshMaps;
	}

	/**
	 * Gives the {@link MapLoader} for the map with the given id.
	 * 
	 * @param id
	 *            The id of the map to be found.
	 * @return Returns the corresponding {@link MapLoader}<br>
	 *         or null if no map with the given id has been found.
	 */
	public MapLoader getMapById(String id) {
		ArrayList<MapLoader> maps = new ArrayList<>();
		maps.addAll(getFreshMaps().getItems());
		maps.addAll(getSavedMaps().getItems());

		for (MapLoader curr : maps) {
			if (curr.getMapId().equals(id)) {
				return curr;
			}
		}
		return null;

	}

	public MapLoader getMapByName(String mapName) {
		ArrayList<MapLoader> maps = new ArrayList<>();
		maps.addAll(getFreshMaps().getItems());
		maps.addAll(getSavedMaps().getItems());

		for (MapLoader curr : maps) {
			if (curr.getMapName().equals(mapName)) {
				return curr;
			}
		}
		return null;
	}

	/**
	 * saves a static map to the given directory.
	 * 
	 * @param header
	 *            The header to use.
	 * @param data
	 *            The data to save.
	 * @param out
	 *            This parameter is optional. If it is not null, the stream is used to save the map to this location. If it is null, the map is saved in the default location.
	 * @throws IOException
	 *             If any IO error occurred.
	 */
	public synchronized void saveNewMap(jsettlers.logic.map.loading.newmap.MapFileHeader header, IMapData data, OutputStream out) throws IOException {
		try {
			if (out == null) {
				out = mapDirectories.iterator().next().getOutputStream(header);
			}
			header.writeTo(out);
			FreshMapSerializer.serialize(data, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		loadFileList();
	}

	/**
	 * Saves a map to disk. The map logic should be paused while calling this method.
	 * 
	 * @param playerStates
	 * @param grid
	 * @throws IOException
	 */
	public synchronized void saveMap(PlayerState[] playerStates, MapFileHeader header, MainGrid grid) throws IOException {
		MilliStopWatch watch = new MilliStopWatch();
		OutputStream outStream = saveDirectory.getOutputStream(header);

		header.writeTo(outStream);

		ObjectOutputStream oos = new ObjectOutputStream(outStream);
		MatchConstants.serialize(oos);
		oos.writeObject(playerStates);
		GameSerializer gameSerializer = new GameSerializer();
		gameSerializer.save(grid, oos);
		RescheduleTimer.saveTo(oos);

		oos.close();
		watch.stop("Writing savegame required");

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
	public static synchronized MapList getDefaultList() {
		if (defaultList == null) {
			defaultList = mapListFactory.getMapList();
		}
		return defaultList;
	}

	public static void setDefaultListFactory(IMapListFactory factory) {
		mapListFactory = factory;
		defaultList = null;
	}

	public static class DefaultMapListFactory implements IMapListFactory {
		protected ArrayList<IMapLister> directories = new ArrayList<>();
		protected IMapLister saveDirectory = null;

		public void addMapDirectory(String directory, boolean create) {
			directories.add(new DirectoryMapLister(new File(directory), create));
		}

		public void addSaveDirectory(IMapLister mapLister) {
			saveDirectory = mapLister;
			addMapDirectory(mapLister);
		}

		@Override
		public MapList getMapList() {
			IMapLister save = getSave();
			if (saveDirectory == null) {
				throw new RuntimeException("Savegame directory not set.");
			}
			return new MapList(getMapListers(), saveDirectory);
		}

		public void addResourcesDirectory(File resources) {
			addMapDirectory(new DirectoryMapLister(new File(resources, "maps"), true));
			saveDirectory = new DirectoryMapLister(new File(resources, "save"), true);
			addMapDirectory(saveDirectory);
		}

		protected IMapLister getSave() {
			return saveDirectory;
		}

		public Collection<IMapLister> getMapListers() {
			return directories;
		}

		public void addMapDirectory(IMapLister dir) {
			this.directories.add(dir);
		}
	}

	public static class ListedResourceMap implements IListedMap {
		private String path;

		public ListedResourceMap(String path) {
			super();
			this.path = path;
		}

		@Override
		public boolean isCompressed() {
			return path.endsWith(MapLoader.MAP_EXTENSION_COMPRESSED);
		}

		@Override
		public InputStream getInputStream() throws IOException {
			InputStream stream = getClass().getResourceAsStream(path);
			if (stream == null) {
				throw new IOException("Map not found in " + path);
			}
			return stream;
		}

		@Override
		public String getFileName() {
			return path.replaceFirst(".*/", "");
		}

		@Override
		public File getFile() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void delete() {
			throw new UnsupportedOperationException();
		}
	}

}
