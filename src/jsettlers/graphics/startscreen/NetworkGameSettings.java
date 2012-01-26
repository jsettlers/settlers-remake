package jsettlers.graphics.startscreen;

import jsettlers.common.network.IMatchSettings;
import jsettlers.common.network.INetworkableMap;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;

public class NetworkGameSettings implements IMatchSettings {

	private final IMapItem map;
	private final String name;
	private final int players;

	public NetworkGameSettings(IMapItem map, String name) {
		this(map, name, 3);
    }

	public NetworkGameSettings(IMapItem map, String name, int players) {
		this.map = map;
		this.name = name;
		this.players = players;
    }

	@Override
	public String getMatchName() {
		return name;
	}

	@Override
	public int getMaxPlayers() {
		return map.getMaxPlayers();
	}

	@Override
	public long getRandomSeed() {
		return 0; //TODO: define one.
	}

	@Override
	public INetworkableMap getMap() {
		return map.getNetworkableMap();
	}

}
