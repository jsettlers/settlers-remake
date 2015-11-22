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
package jsettlers.logic.map.save;

import java.io.*;
import java.util.ArrayList;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.IMapData;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.grid.GameSerializer;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.save.IMapLister.IMapListerCallable;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.MapLoader;
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

	private static File originalSettlersFolder;

	/**
	 * Gives the currently used map extension for saving a map.
	 * 
	 * @return
	 */
	public static String getMapExtension() {
		return CommonConstants.USE_SAVEGAME_COMPRESSION ? MapLoader.MAP_EXTENSION_COMPRESSED : MapLoader.MAP_EXTENSION;
	}

	private static IMapListFactory mapListFactory = new IMapListFactory() {
		@Override
		public MapList getMapList(File originalSettlersFolder) {
			return new MapList(ResourceManager.getSaveDirectory(), originalSettlersFolder);
		}
	};

	private static MapList defaultList;

	private final IMapLister mapsDir;
	private final IMapLister saveDir;
	private final IMapLister originalMultiDir;
	private final IMapLister originalSingleDir;
	private final IMapLister originalUserDir;

	private final ChangingList<MapLoader> freshMaps = new ChangingList<>();
	private final ChangingList<MapLoader> savedMaps = new ChangingList<>();

	private boolean fileListLoaded = false;

	public MapList(File dir, File originalSettlersDir) {
		this(new DirectoryMapLister(new File(dir, "maps")),
			new DirectoryMapLister(new File(dir, "save")),
			new DirectoryMapLister(new File(originalSettlersDir, "Map/MULTI")),
			new DirectoryMapLister(new File(originalSettlersDir, "Map/SINGLE")),
			new DirectoryMapLister(new File(originalSettlersDir, "Map/User")));
	}

	public MapList(IMapLister mapsDir, IMapLister saveDir) {
		this(mapsDir, saveDir, null, null, null);
	}

	public MapList(IMapLister mapsDir, IMapLister saveDir, IMapLister originalMultiDir, IMapLister originalSingleDir, IMapLister originalUserDir) {
		this.mapsDir = mapsDir;
		this.saveDir = saveDir;
		this.originalMultiDir = originalMultiDir;
		this.originalSingleDir = originalSingleDir;
		this.originalUserDir = originalUserDir;
	}

	private void loadFileList() {
		freshMaps.clear();
		savedMaps.clear();

		if (originalMultiDir != null) {
			originalMultiDir.listMaps(this);
		}
		if (originalUserDir != null) {
			originalUserDir.listMaps(this);
		}
		if (originalSingleDir != null) {
			originalSingleDir.listMaps(this);
		}
		mapsDir.listMaps(this);
		saveDir.listMaps(this);
	}

	@Override
	public synchronized void foundMap(IListedMap map) {
		try {
			MapLoader loader = MapLoader.getLoaderForListedMap(map);
			MapType type = loader.getFileHeader().getType();

			if ((type == MapType.SAVED_SINGLE)) {
				savedMaps.add(loader);
			} else {
				freshMaps.add(loader);
			}
		} catch (Exception e) {
			System.err.println("Cought exception while loading header for " + map.getFileName());
			e.printStackTrace();
		}
	}

	public synchronized ChangingList<MapLoader> getSavedMaps() {
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
		ArrayList<MapLoader> maps = new ArrayList<MapLoader>();
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
		ArrayList<MapLoader> maps = new ArrayList<MapLoader>();
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
	 *            This parameter is optional. If it is not null, the stream is used to save the map to this location. If it is null, the map is saved
	 *            in the default location.
	 * @throws IOException
	 *             If any IO error occurred.
	 */
	public synchronized void saveNewMap(MapFileHeader header, IMapData data, OutputStream out) throws IOException {
		try {
			if (out == null) {
				out = mapsDir.getOutputStream(header);
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
	 * @param state
	 * @param grid
	 * @throws IOException
	 */
	public synchronized void saveMap(PlayerState[] playerStates, MainGrid grid) throws IOException {
		MilliStopWatch watch = new MilliStopWatch();
		MapFileHeader header = grid.generateSaveHeader();
		OutputStream outStream = saveDir.getOutputStream(header);

		header.writeTo(outStream);

		ObjectOutputStream oos = new ObjectOutputStream(outStream);
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
	 * @param originalSettlersFolder
	 */
	public static synchronized MapList getDefaultList() {
		if (defaultList == null) {
			defaultList = mapListFactory.getMapList(MapList.originalSettlersFolder);
		}
		return defaultList;
	}

	public static void setOriginalSettlersFolder(File originalSettlersFolder) {
		MapList.originalSettlersFolder = originalSettlersFolder;
	}

	public void deleteLoadableGame(MapLoader game) {
		game.getListedMap().delete();
		savedMaps.remove(game); //- TODO: or freshMaps.remove ?
		loadFileList();
	}

	public static void setDefaultListFactory(IMapListFactory factory) {
		mapListFactory = factory;
		defaultList = null;
	}

}
