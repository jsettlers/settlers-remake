package jsettlers.main.network;

import jsettlers.graphics.INetworkScreenAdapter.INetworkPlayer;
import jsettlers.network.server.match.MatchPlayer;

final class NetworkPlayer implements INetworkPlayer {
	private final MatchPlayer matchPlayer;

	public NetworkPlayer(MatchPlayer matchPlayer) {
		this.matchPlayer = matchPlayer;
	}

	@Override
	public String getPlayerName() {
		return matchPlayer.getName();
	}

	@Override
	public boolean isReady() {
		return matchPlayer.isReady();
	}

	public String getUniqueID() {
		return matchPlayer.getUniqueId();
	}
}