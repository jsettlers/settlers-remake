package jsettlers.graphics.startscreen;

import jsettlers.common.network.IMatchSettings;
import jsettlers.common.network.INetworkableMap;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;

public class NetworkGameSettings implements IMatchSettings {

	private final IMapItem map;
	private final String name;
	private final int players;
	private final String server;

	/**
	 * Creates a new set of network settings.
	 * @see IMatchSettings
	 * @param map
	 * @param name
	 * @param players
	 * @param server
	 */
	public NetworkGameSettings(IMapItem map, String name, int players, String server) {
		this.map = map;
		this.name = name;
		this.players = players;
		this.server = server;
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

	@Override
    public String getServerAddress() {
	    return server;
    }

}
