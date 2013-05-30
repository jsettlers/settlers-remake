package networklib.server;

import java.util.Timer;

import networklib.NetworkConstants;
import networklib.client.exceptions.InvalidStateException;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.OpenNewMatchPacket;
import networklib.common.packets.TimeSyncPacket;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.reject.RejectPacket;
import networklib.server.db.IDBFacade;
import networklib.server.exceptions.NotAllPlayersReadyException;
import networklib.server.game.Match;
import networklib.server.game.MatchSendingTimerTask;
import networklib.server.game.Player;
import networklib.server.listeners.ChatMessageForwardingListener;
import networklib.server.listeners.IdentifyUserListener;
import networklib.server.listeners.ReadyStatePacketListener;
import networklib.server.listeners.ServerChannelClosedListener;
import networklib.server.listeners.TimeSyncForwardingListener;
import networklib.server.listeners.matches.RequestJoinMatchListener;
import networklib.server.listeners.matches.RequestLeaveMatchListener;
import networklib.server.listeners.matches.RequestOpenNewMatchListener;
import networklib.server.listeners.matches.RequestStartMatchListener;

/**
 * This class is the central access point to the servers externally reachable functions.
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerManager implements IServerManager {

	private final IDBFacade db;
	private final Timer sendMatchListTimer = new Timer("SendMatchListTimer", true);
	private final Timer matchesTaskDistributionTimer = new Timer("MatchesTaskDistributionTimer", true);
	private final MatchSendingTimerTask matchSendingTask;

	public ServerManager(IDBFacade db) {
		this.db = db;
		matchSendingTask = new MatchSendingTimerTask(db);
	}

	public synchronized void start() {
		sendMatchListTimer.schedule(matchSendingTask, 0, NetworkConstants.Server.OPEN_MATCHES_SEND_INTERVAL_MS);
	}

	public synchronized void shutdown() {
		sendMatchListTimer.cancel();
		matchesTaskDistributionTimer.cancel();
	}

	public void identifyNewChannel(Channel channel) {
		channel.registerListener(new IdentifyUserListener(channel, this));
	}

	@Override
	public boolean acceptNewPlayer(Player player) {
		if (db.isAcceptedPlayer(player.getId())) {
			db.storePlayer(player);

			Channel channel = player.getChannel();
			channel.removeListener(NetworkConstants.Keys.IDENTIFY_USER);

			channel.setChannelClosedListener(new ServerChannelClosedListener(this, player));
			channel.registerListener(new RequestOpenNewMatchListener(this, player));
			channel.registerListener(new RequestLeaveMatchListener(this, player));
			channel.registerListener(new RequestStartMatchListener(this, player));
			channel.registerListener(new RequestJoinMatchListener(this, player));
			channel.registerListener(new ChatMessageForwardingListener(this, player));
			channel.registerListener(new TimeSyncForwardingListener(this, player));
			channel.registerListener(new ReadyStatePacketListener(this, player));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void channelClosed(Player player) {
		db.removePlayer(player);

		if (player.isInMatch()) {
			try {
				player.leaveMatch();
			} catch (InvalidStateException e) {
				assert false : "This may never happen here!";
			}
		}
	}

	@Override
	public void createNewMatch(OpenNewMatchPacket matchInfo, Player player) {
		Match match = new Match(matchInfo.getMatchName(), matchInfo.getMaxPlayers(), matchInfo.getMapInfo(), matchInfo.getRandomSeed());
		db.storeMatch(match);

		joinMatch(match, player);
	}

	private void joinMatch(Match match, Player player) {
		try {
			player.joinMatch(match);
		} catch (InvalidStateException e) {
			e.printStackTrace();
			player.sendPacket(NetworkConstants.Keys.REJECT_PACKET,
					new RejectPacket(NetworkConstants.Messages.INVALID_STATE_ERROR, NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH));
		}
	}

	@Override
	public void leaveMatch(Player player) {
		try {
			player.leaveMatch();
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startMatch(Player player) {
		try {
			player.startMatch(matchesTaskDistributionTimer);
		} catch (InvalidStateException e) {
			e.printStackTrace();
			player.sendPacket(NetworkConstants.Keys.REJECT_PACKET,
					new RejectPacket(NetworkConstants.Messages.INVALID_STATE_ERROR, NetworkConstants.Keys.REQUEST_START_MATCH));
		} catch (NotAllPlayersReadyException e) {
			player.sendPacket(NetworkConstants.Keys.REJECT_PACKET,
					new RejectPacket(NetworkConstants.Messages.NOT_ALL_PLAYERS_READY, NetworkConstants.Keys.REQUEST_START_MATCH));
		}
	}

	@Override
	public void forwardChatMessage(Player player, ChatMessagePacket packet) {
		try {
			player.forwardChatMessage(packet);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void distributeTimeSync(Player player, TimeSyncPacket packet) {
		try {
			player.distributeTimeSync(packet);
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void joinMatch(MatchInfoPacket packet, Player player) {
		Match match = db.getMatchById(packet.getId());
		try {
			player.joinMatch(match);
		} catch (InvalidStateException e) {
			player.sendPacket(NetworkConstants.Keys.REJECT_PACKET,
					new RejectPacket(NetworkConstants.Messages.INVALID_STATE_ERROR, NetworkConstants.Keys.REQUEST_JOIN_MATCH));
		}
	}

	@Override
	public void setReadyStateForPlayer(Player player, boolean ready) {
		try {
			player.setReady(ready);
		} catch (InvalidStateException e) {
			player.sendPacket(NetworkConstants.Keys.REJECT_PACKET,
					new RejectPacket(NetworkConstants.Messages.INVALID_STATE_ERROR, NetworkConstants.Keys.READY_STATE_CHANGE));
		}
	}

	@Override
	public void sendMatchesToPlayer(Player player) {
		matchSendingTask.sendMatchesTo(player);
	}

}
