package jsettlers.main.network;

import jsettlers.common.network.IMatch;
import networklib.common.packets.MatchInfoPacket;

/**
 * This class acts as an adapter to fulfill the {@link IMatch} interface. The class gets the data from a given {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoPacketAdapter implements IMatch {

	private final MatchInfoPacket matchInfoPacket;

	public MatchInfoPacketAdapter(MatchInfoPacket matchInfoPacket) {
		this.matchInfoPacket = matchInfoPacket;
	}

	@Override
	public String getMatchID() {
		return getMatchInfoPacket().getId();
	}

	@Override
	public String getMatchName() {
		return getMatchInfoPacket().getMatchName();
	}

	@Override
	public int getMaxPlayers() {
		return getMatchInfoPacket().getMaxPlayers();
	}

	@Override
	public String getMapID() {
		return getMatchInfoPacket().getMapInfo().getId();
	}

	public MatchInfoPacket getMatchInfoPacket() {
		return matchInfoPacket;
	}
}
