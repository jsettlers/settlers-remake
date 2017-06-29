/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.logic.map.loading.newmap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.common.menu.ILoadableMapPlayer;
import jsettlers.common.menu.UIState;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.player.PlayerSetting;

/**
 * This is the main map loader.
 * <p>
 * It loads a map file.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public abstract class RemakeMapLoader extends MapLoader {

	private final IListedMap file;

	public RemakeMapLoader(IListedMap file, MapFileHeader header) {
		this.file = file;
		this.header = header;
	}

	@Override
	public MapFileHeader getFileHeader() {
		return header;
	}

	public static MapFileHeader loadHeader(IListedMap file) throws MapLoadException {
		try (InputStream stream = getMapInputStream(file)) {
			return MapFileHeader.readFromStream(stream);
		} catch (IOException e) {
			throw new MapLoadException("Error during header request for map " + file + " exception: ", e);
		}
	}

	/**
	 * 
	 * @return Returns a stream of the file without the header. So you can directly start reading the data of the map.
	 * @throws IOException
	 */
	public final InputStream getMapDataStream() throws IOException {
		InputStream inputStream = getMapInputStream(file);
		MapFileHeader.readFromStream(inputStream);
		return inputStream;
	}

	public static InputStream getMapInputStream(IListedMap file) throws IOException {
		InputStream inputStream = new BufferedInputStream(file.getInputStream());
		try {
			if (file.isCompressed()) {
				ZipInputStream zipInputStream = new ZipInputStream(inputStream);
				ZipEntry zipEntry = zipInputStream.getNextEntry();
				if (!zipEntry.getName().endsWith(MapLoader.MAP_EXTENSION)) {
					zipInputStream.close();
					throw new IOException("Invalid compressed map format!");
				}
				inputStream = zipInputStream;
			}
			return inputStream;
		} catch (Exception ex) {
			inputStream.close();
			throw ex;
		}
	}

	@Override
	public String getMapName() {
		return header.getName();
	}

	@Override
	public int getMinPlayers() {
		return header.getMinPlayers();
	}

	@Override
	public int getMaxPlayers() {
		return header.getMaxPlayers();
	}

	@Override
	public Date getCreationDate() {
		return header.getCreationDate();
	}

	@Override
	public String toString() {
		return "MapLoader: mapName: " + file.getFileName() + " mapId: " + getMapId();
	}

	@Override
	public String getMapId() {
		return header.getUniqueId();
	}

	@Override
	public String getDescription() {
		return header.getDescription();
	}

	@Override
	public short[] getImage() {
		return header.getPreviewImage();
	}

	@Override
	public List<ILoadableMapPlayer> getPlayers() { // TODO @Andreas Eberle: supply saved players information.
		return new ArrayList<>();
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		return loadMainGrid(playerSettings, EMapStartResources.HIGH_GOODS);
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings, EMapStartResources startResources) throws MapLoadException {
		MilliStopWatch watch = new MilliStopWatch();
		IMapData mapData = getMapData();
		watch.stop("Loading map data required");

		byte numberOfPlayers = (byte) getMaxPlayers();

		if (playerSettings == null || CommonConstants.ACTIVATE_ALL_PLAYERS) {
			playerSettings = new PlayerSetting[numberOfPlayers];
			for (int i = 0; i < numberOfPlayers; i++) {
				playerSettings[i] = new PlayerSetting((byte) i);
			}
		}

		MainGrid mainGrid = new MainGrid(getMapId(), getMapName(), mapData, playerSettings);

		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) {
			playerStates[playerId] = new PlayerState(playerId, new UIState(mapData.getStartPoint(playerId)));
		}

		return new MainGridWithUiSettings(mainGrid, playerStates);
	}

	@Override
	public IListedMap getListedMap() {
		return file;
	}
}
