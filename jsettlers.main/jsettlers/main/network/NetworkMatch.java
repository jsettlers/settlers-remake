package jsettlers.main.network;

import jsettlers.common.network.IMatch;

/**
 * This is a implementation of {@link IMatch}.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkMatch implements IMatch {

	private final String matchID;
	private final String matchName;
	private final int maxPlayers;
	private final String mapID;

	public NetworkMatch(String matchID, String matchName, String mapID, int maxPlayers) {
		this.matchID = matchID;
		this.matchName = matchName;
		this.mapID = mapID;
		this.maxPlayers = maxPlayers;
	}

	@Override
	public String getMatchID() {
		return matchID;
	}

	@Override
	public String getMatchName() {
		return matchName;
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public String getMapID() {
		return mapID;
	}

}
