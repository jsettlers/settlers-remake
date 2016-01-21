package jsettlers.main.components.mainmenu;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.logic.map.EMapStartResources;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.network.common.packets.MatchInfoPacket;

import java.util.Date;
import java.util.List;

/**
 * @author codingberlin
 */
public class NetworkGameMapLoader extends MapLoader {

	private final MapLoader mapLoader;
	private final String gameName;
	private final IJoinableGame joinableGame;

	public NetworkGameMapLoader(IJoinableGame joinableGame) {
		this.joinableGame = joinableGame;
		this.mapLoader = MapList.getDefaultList().getMapById(joinableGame.getMap().getMapId());
		this.gameName = joinableGame.getName();
	}

	public IJoinableGame getJoinableGame() {
		return joinableGame;
	}

	@Override public MapFileHeader getFileHeader() {
		return mapLoader.getFileHeader();
	}

	@Override public IListedMap getListedMap() {
		return mapLoader.getListedMap();
	}

	@Override public IMapData getMapData() throws MapLoadException {
		return mapLoader.getMapData();
	}

	@Override public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		return mapLoader.loadMainGrid(playerSettings);
	}

	@Override public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings, EMapStartResources startResources)
			throws MapLoadException {
		return mapLoader.loadMainGrid(playerSettings, startResources);
	}

	@Override public String getMapName() {
		return gameName + "(" + mapLoader.getMapName() + ")";
	}

	@Override public String getDescription() {
		return mapLoader.getDescription();
	}

	@Override public short[] getImage() {
		return mapLoader.getImage();
	}

	@Override public int getMinPlayers() {
		return mapLoader.getMinPlayers();
	}

	@Override public int getMaxPlayers() {
		return mapLoader.getMaxPlayers();
	}

	@Override public List<ILoadableMapPlayer> getPlayers() {
		return mapLoader.getPlayers();
	}

	@Override public Date getCreationDate() {
		return mapLoader.getCreationDate();
	}

	@Override public String getMapId() {
		return mapLoader.getMapId();
	}
}
