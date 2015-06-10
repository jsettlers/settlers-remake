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
package jsettlers.logic.map.save.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;

/**
 * This is the main map loader.
 * <p>
 * It loads a map file.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public abstract class MapLoader implements IGameCreator, Comparable<MapLoader> {
	private final IListedMap file;
	private final MapFileHeader header;

	public MapLoader(IListedMap file, MapFileHeader header) {
		this.file = file;
		this.header = header;
	}

	public static MapLoader getLoaderForFile(IListedMap file) throws MapLoadException {
		MapFileHeader header = loadHeader(file);

		switch (header.getType()) {
		case NORMAL:
			return new FreshMapLoader(file, header);
		case SAVED_SINGLE:
			return new SavegameLoader(file, header);
		default:
			throw new MapLoadException("Unkown EMapType: " + header.getType());
		}

	}

	public MapFileHeader getFileHeader() {
		return header;
	}

	private static MapFileHeader loadHeader(IListedMap file) throws MapLoadException {
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(file.getInputStream());
			return MapFileHeader.readFromStream(stream);
		} catch (IOException e) {
			throw new MapLoadException("Error during header request: ", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 
	 * @return Returns a stream of the file without the header. So you can directly start reading the data of the map.
	 * @throws IOException
	 */
	protected final InputStream getMapDataStream() throws IOException {
		InputStream stream = new BufferedInputStream(file.getInputStream());
		MapFileHeader.readFromStream(stream);
		return stream;
	}

	@Override
	public String getMapName() {
		return header.getName();
	}

	public int getMinPlayers() {
		return header.getMinPlayer();
	}

	public int getMaxPlayers() {
		return header.getMaxPlayer();
	}

	public Date getCreationDate() {
		return header.getCreationDate();
	}

	/**
	 * Gets the map data for this loader, if the data is available.
	 * 
	 * @return
	 */
	public abstract IMapData getMapData() throws MapLoadException;

	@Override
	public String toString() {
		return "MapLoader: mapName: " + file.getFileName() + " mapId: " + getMapID();
	}

	@Override
	public String getMapID() {
		return header.getUniqueId();
	}

	public String getDescription() {
		return header.getDescription();
	}

	public short[] getImage() {
		return header.getBgimage();
	}

	@Override
	public int compareTo(MapLoader o) {
		MapFileHeader myHeader = header;
		MapFileHeader otherHeader = o.header;
		if (myHeader.getType() == MapType.SAVED_SINGLE) {
			return -myHeader.getCreationDate().compareTo(otherHeader.getCreationDate()); // order by date descending
		} else {
			return myHeader.getName().compareTo(otherHeader.getName()); // order by name ascending
		}
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(boolean[] availablePlayers) throws MapLoadException {
		IMapData mapData = getMapData();

		byte numberOfPlayers = (byte) getMaxPlayers();

		if (availablePlayers == null || CommonConstants.ACTIVATE_ALL_PLAYERS) {
			availablePlayers = new boolean[numberOfPlayers];
			for (int i = 0; i < numberOfPlayers; i++) {
				availablePlayers[i] = true;
			}
		}

		MainGrid mainGrid = new MainGrid(getMapID(), getMapName(), mapData, availablePlayers);

		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) {
			playerStates[playerId] = new PlayerState(playerId, new UIState(mapData.getStartPoint(playerId)));
		}

		return new MainGridWithUiSettings(mainGrid, playerStates);
	}

	public void delete() {
		file.delete();
	}
}
