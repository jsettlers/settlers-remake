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
package jsettlers.logic.map.original;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.logic.map.grid.MainGrid;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author codingberlin
 * @author Thomas Zeugner
 */
public class OriginalMapLoader extends MapLoader 
{
	private final IListedMap listedMap;
	private final OriginalMapFileContentReader mapContent;
	private final Date creationDate;
	private final String fileName;
	
	public OriginalMapLoader(IListedMap listedMap) throws IOException {
		this.listedMap = listedMap;
		fileName = listedMap.getFileName();
		creationDate = new Date(new File(fileName).lastModified());
		mapContent = new OriginalMapFileContentReader(listedMap.getInputStream());

		if (!mapContent.isChecksumValid()) {
			System.out.println("Checksum of original map ("+ fileName +") was not valid!");
			return;
		}
		
		//- read all important information from file
		mapContent.loadMapResources();
		mapContent.readBasicMapInformation();
		
		//- free the DataBuffer
		mapContent.FreeBuffer();
	}

	//---------------------------//
	//-- Interface MapLoader --//
	//-------------------------//
	@Override
	public MapFileHeader getFileHeader() {
		return new MapFileHeader(
				MapFileHeader.MapType.NORMAL,
				getMapName(),
				getMapId(),
				getDescription(),
				(short) mapContent.widthHeight,
				(short) mapContent.widthHeight,
				(short)getMinPlayers(),
				(short)getMaxPlayers(),
				getCreationDate(),
				getImage());
	}
	
	@Override
	public IListedMap getListedMap() {
		return listedMap;
	}

	//------------------------------//
	//-- Interface IMapDefinition --//
	//------------------------------//
	@Override
	public String getMapName() {
		return fileName; //.replaceFirst("[.][^.]+$", "").replace('_', ' ');
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
		return mapContent.readMapQuestText();
	}

	@Override
	public short[] getImage() {
		//- TODO 
		short[] tmp = new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE];
		return tmp;
	}
	
	@Override
	public String getMapId() {
		return Integer.toString(mapContent.fileChecksum);
	}
	
	@Override
	public List<ILoadableMapPlayer> getPlayers() {
		return new ArrayList<ILoadableMapPlayer>(); //- ToDo
	}

	//----------------------------//
	//-- Interface IGameCreator --//
	//----------------------------//
	
	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		MilliStopWatch watch = new MilliStopWatch();
		
		try
		{
			//- the map buffer of the class may is closed and need to reopen! 
			mapContent.reOpen(this.listedMap.getInputStream());
		}
		catch (Exception e)
		{
			System.err.println("Error: "+ e.getMessage());
		}
		
		//- load all common map information
		mapContent.loadMapResources();
		mapContent.readBasicMapInformation();
		
		//- read the landscape
		mapContent.readMapData();
		//- read Stacks
		mapContent.readStacks();
		//- read Settlers
		mapContent.readSettlers();
		//- read the buildings
		mapContent.readBuildings();
		//- add player resources
		mapContent.addStartTowerMaterialsAndSettlers();
		
		OriginalMapFileContent MapData = mapContent.mapData;
		MapData.calculateBlockedPartitions();

		
		watch.stop("Loading original map data required");

		byte numberOfPlayers = (byte) getMaxPlayers();

		if (playerSettings == null || CommonConstants.ACTIVATE_ALL_PLAYERS) {
			playerSettings = new PlayerSetting[numberOfPlayers];
			
			for (int i = 0; i < numberOfPlayers; i++) {
				playerSettings[i] = new PlayerSetting(true, null);
			}
		}

		MainGrid mainGrid = new MainGrid(getMapId(), getMapName(), MapData, playerSettings);

		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) {
			playerStates[playerId] = new PlayerState(playerId, new UIState(MapData.getStartPoint(playerId)));
		}

		return new MainGridWithUiSettings(mainGrid, playerStates);
	}

	
	@Override
	public IMapData getMapData() throws MapLoadException {
		
		try
		{
			//- the map buffer of the class may is closed and need to reopen! 
			mapContent.reOpen(this.listedMap.getInputStream());
		}
		catch (Exception e)
		{
			throw new MapLoadException(e);
		}
		
		//- load all common map information
		mapContent.loadMapResources();
		mapContent.readBasicMapInformation();
		
		//- read the landscape
		mapContent.readMapData();
		//- read Stacks
		mapContent.readStacks();
		//- read Settlers
		mapContent.readSettlers();
		//- read the buildings
		mapContent.readBuildings();
		//- add player resources
		mapContent.addStartTowerMaterialsAndSettlers();
		
		OriginalMapFileContent MapData = mapContent.mapData;
		MapData.calculateBlockedPartitions();

		return MapData;
		
	}
	
}
