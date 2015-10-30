package jsettlers.logic.map.original;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.IGameCreator.MainGridWithUiSettings;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.IMapLoader;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.original.OriginalMapFileContent;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;


/**
 * @author codingberlin
 * @author Thomas Zeugner
 */
public class OriginalMapLoader extends IMapLoader 
{
	
	private OriginalMapFileContentReader _mapContent;
	
	private Date _CreationDate;
	private String _FileName;
	
	public OriginalMapLoader(IListedMap listedMap)
	{
		_FileName = listedMap.getFileName();
		
		try
		{
			File file = new File(_FileName);
			_CreationDate = new Date(file.lastModified());
		}
		catch (Exception  e)
		{
			_CreationDate = new Date(); //- use now date
		}
		
		
		try
		{
			InputStream originalMapFile = listedMap.getInputStream();
			
			_mapContent = new OriginalMapFileContentReader(originalMapFile);		
			
			if (!_mapContent.isChecksumValid())
			{
				System.out.println("Checksum of original map was not valid!");
				return;
			}
			
			_mapContent.loadMapResources();
			
			_mapContent.readBasicMapInformation();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//------------------------------//
	//-- Interface IMapLoader --//
	@Override
	public MapFileHeader getFileHeader()
	{
		return new MapFileHeader(MapFileHeader.MapType.ORIGINAL, getMapName(), getMapId(), getDescription(), (short)_mapContent.WidthHeight, (short)_mapContent.WidthHeight, (short)getMinPlayers(), (short)getMaxPlayers(), getCreationDate(), getImage());
	}
	
	@Override
	public int compareTo(MapLoader o) {
		return (int) (_CreationDate.getTime()/1000);
	}
	
	
	//------------------------------//
	//-- Interface IMapDefinition --//
	@Override
	public String getMapName()
	{
		return _FileName;
	}

	@Override 
	public int getMinPlayers() {
		return _mapContent.MinPlayers;
	}

	@Override
	public int getMaxPlayers()
	{
		return _mapContent.MinPlayers;
	}

	@Override 
	public Date getCreationDate()
	{
		return _CreationDate;
	}
	
	@Override
	public String getDescription()
	{
		return ""; //- TODO
	}

	@Override
	public short[] getImage()
	{
		//- TODO 
		short[] tmp = new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE];
		return tmp;
	}
	
	@Override
	public String getMapId()
	{
		return Integer.toString(_mapContent.fileChecksum);
	}
	
	@Override
	public List<ILoadableMapPlayer> getPlayers()
	{
		return new ArrayList<ILoadableMapPlayer>(); //- ToDo
	}
	//------------------------------//
	
	
	
	//------------------------------//
	//-- Interface IGameCreator --//
	
	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException
	{
		MilliStopWatch watch = new MilliStopWatch();
		OriginalMapFileContent MapData = _mapContent.readMapData();
		watch.stop("Loading original map data required");

		byte numberOfPlayers = (byte) getMaxPlayers();

		if (playerSettings == null || CommonConstants.ACTIVATE_ALL_PLAYERS)
		{
			playerSettings = new PlayerSetting[numberOfPlayers];
			
			for (int i = 0; i < numberOfPlayers; i++)
			{
				playerSettings[i] = new PlayerSetting(true, false);
			}
		}

		MainGrid mainGrid = new MainGrid(getMapId(), getMapName(), MapData, playerSettings);

		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) 
		{
			playerStates[playerId] = new PlayerState(playerId, new UIState(MapData.getStartPoint(playerId)));
		}

		return new MainGridWithUiSettings(mainGrid, playerStates);
	}


}
