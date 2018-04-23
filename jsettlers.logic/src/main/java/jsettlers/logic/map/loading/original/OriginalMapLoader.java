/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.logic.map.loading.original;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.player.PlayerSetting;

/**
 * @author codingberlin
 * @author Thomas Zeugner
 */
public class OriginalMapLoader extends MapLoader {
	private final IListedMap listedMap;
	private final OriginalMapFileContentReader mapContent;
	private final Date creationDate;
	private final String fileName;
	private Boolean isMapOK = false;

	public OriginalMapLoader(IListedMap listedMap) throws MapLoadException {
		this.listedMap = listedMap;
		fileName = listedMap.getFileName();
		creationDate = getCreationDateFrom(listedMap);
		try {
			mapContent = new OriginalMapFileContentReader(listedMap.getInputStream());
		} catch (IOException e) {
			throw new MapLoadException(e);
		}

		if (!CommonConstants.DISABLE_ORIGINAL_MAPS_CHECKSUM && !mapContent.isChecksumValid()) {
			throw new MapLoadException("Checksum of original map (" + fileName + ") is not valid!");
		}

		// - read all important information from file
		mapContent.loadMapResources();
		mapContent.readBasicMapInformation(MapFileHeader.PREVIEW_IMAGE_SIZE, MapFileHeader.PREVIEW_IMAGE_SIZE);

		// - free the DataBuffer
		mapContent.freeBuffer();

		isMapOK = true;
	}

	private Date getCreationDateFrom(IListedMap listedMap) {
		try {
			return new Date(listedMap.getFile().lastModified());
		} catch (UnsupportedOperationException e) {
			return new Date();
		}
	}

	// ---------------------------//
	// -- Interface MapLoader --//
	// -------------------------//
	@Override
	public MapFileHeader getFileHeader() {
		if (isMapOK) {
			return new MapFileHeader(
					MapFileHeader.MapType.NORMAL,
					getMapName(),
					getMapId(),
					getDescription(),
					(short) mapContent.widthHeight,
					(short) mapContent.widthHeight,
					(short) getMinPlayers(),
					(short) getMaxPlayers(),
					getCreationDate(),
					getImage());
		}
		return null;
	}

	@Override
	public IListedMap getListedMap() {
		return listedMap;
	}

	// ------------------------------//
	// -- Interface IMapDefinition --//
	// ------------------------------//
	@Override
	public String getMapName() {
		// - remove the extension {.map or .edm} of filename and replace all '_' with ' ' (filename is without path)
		if (fileName == null) {
			return "";
		}

		int pos = fileName.lastIndexOf('.');
		if (pos >= 0) {
			return fileName.substring(0, pos).replace('_', ' ');
		} else {
			return fileName.replace('_', ' ');
		}
	}

	@Override
	public int getMinPlayers() {
		return 1;
	}

	@Override
	public int getMaxPlayers() {
		return mapContent.mapData.getPlayerCount();
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public String getDescription() {
		try {
			return mapContent.readMapQuestText();
		} catch (MapLoadException e) {
			return "";
		}
	}

	@Override
	public short[] getImage() {
		return mapContent.getPreviewImage();
	}

	@Override
	public String getMapId() {
		return mapContent.getChecksum() + getMapName();
	}

	@Override
	public List<ILoadableMapPlayer> getPlayers() {
		return new ArrayList<>(); // - TODO
	}

	// ----------------------------//
	// -- Interface IGameCreator --//
	// ----------------------------//

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		return loadMainGrid(playerSettings, EMapStartResources.HIGH_GOODS);
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings, EMapStartResources startResources) throws MapLoadException {
		MilliStopWatch watch = new MilliStopWatch();

		loadMapContent(startResources, playerSettings);

		OriginalMapFileContent mapData = mapContent.mapData;
		mapData.calculateBlockedPartitions();

		watch.stop("Loading original map data required");

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
	public IMapData getMapData() throws MapLoadException {
		loadMapContent(EMapStartResources.HIGH_GOODS, null);

		OriginalMapFileContent mapData = mapContent.mapData;
		mapData.calculateBlockedPartitions();

		return mapData;
	}

	private void loadMapContent(EMapStartResources startResources, PlayerSetting[] playerSettings) throws MapLoadException {
		try {
			// - the map buffer of the class may is closed and need to reopen!
			mapContent.reOpen(this.listedMap.getInputStream());
		} catch (Exception e) {
			throw new MapLoadException(e);
		}

		// - load all common map information
		mapContent.loadMapResources();
		mapContent.readBasicMapInformation();

		// - read the landscape
		mapContent.readMapData();
		// - read Stacks
		mapContent.readStacks();
		// - read Settlers
		mapContent.readSettlers();
		// - read the buildings
		mapContent.readBuildings();
		// - add player resources
		mapContent.addStartTowerMaterialsAndSettlers(startResources, playerSettings);
	}
}
