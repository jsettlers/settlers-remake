package jsettlers.logic.map.original;

import jsettlers.common.CommonConstants;
import jsettlers.common.logging.MilliStopWatch;
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
			System.out.println("Checksum of original map was not valid!");
			return;
		}

		mapContent.loadMapResources();
		mapContent.readBasicMapInformation();
	}

	//---------------------------//
	//-- Interface MapLoader --//
	//-------------------------//
	@Override
	public MapFileHeader getFileHeader() {
		return new MapFileHeader(
				MapFileHeader.MapType.ORIGINAL,
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
		return fileName;
	}

	@Override 
	public int getMinPlayers() {
		return 1;
	}

	@Override
	public int getMaxPlayers() {
		return mapContent.players.length;
	}

	@Override 
	public Date getCreationDate() {
		return creationDate;
	}
	
	@Override
	public String getDescription() {
		return ""; //- TODO
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
		OriginalMapFileContent MapData = mapContent.readMapData();
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


}
