package jsettlers.network.server.match;

import java.util.List;
import java.util.TimerTask;

import jsettlers.network.NetworkConstants;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.server.db.IDBFacade;

/**
 * This {@link TimerTask} implementation gets the logged in players and sends them the open matches on every call to {@link #run()}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchesListSendingTimerTask extends TimerTask {
	private final IDBFacade db;

	public MatchesListSendingTimerTask(IDBFacade db) {
		this.db = db;
	}

	@Override
	public void run() {
		List<Player> loggedInPlayers = db.getPlayers(EPlayerState.LOGGED_IN);
		ArrayOfMatchInfosPacket packet = getArrayOfMatchInfosPacket();

		for (Player currPlayer : loggedInPlayers) {
			sendMatchesPacketToPlayer(currPlayer, packet);
		}
	}

	private void sendMatchesPacketToPlayer(Player player, ArrayOfMatchInfosPacket arrayOfMatchesPacket) {
		player.sendPacket(NetworkConstants.ENetworkKey.ARRAY_OF_MATCHES, arrayOfMatchesPacket);
	}

	private ArrayOfMatchInfosPacket getArrayOfMatchInfosPacket() {
		List<Match> matches = db.getJoinableMatches();

		MatchInfoPacket[] matchInfoPackets = new MatchInfoPacket[matches.size()];
		int i = 0;
		for (Match curr : matches) {
			matchInfoPackets[i] = new MatchInfoPacket(curr);
			i++;
		}

		return new ArrayOfMatchInfosPacket(matchInfoPackets);
	}

	public void sendMatchesTo(Player player) {
		ArrayOfMatchInfosPacket arrayOfMatchesPacket = getArrayOfMatchInfosPacket();
		sendMatchesPacketToPlayer(player, arrayOfMatchesPacket);
	}
}
