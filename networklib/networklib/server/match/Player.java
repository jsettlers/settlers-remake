package networklib.server.match;

import java.util.Timer;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.common.packets.TimeSyncPacket;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.packet.Packet;
import networklib.infrastructure.log.LoggerManager;
import networklib.server.exceptions.NotAllPlayersReadyException;
import networklib.server.match.lockstep.TaskCollectingListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Player {
	private final PlayerInfoPacket playerInfo;
	private final Channel channel;

	private EPlayerState state = EPlayerState.LOGGED_IN;
	private Match match;

	public Player(PlayerInfoPacket playerInfo, Channel channel) {
		this.playerInfo = playerInfo;
		this.channel = channel;
	}

	public PlayerInfoPacket getPlayerInfo() {
		return playerInfo;
	}

	public String getId() {
		return playerInfo.getId();
	}

	public synchronized void leaveMatch() throws IllegalStateException {
		if (match != null) {
			match.playerLeft(this);
			match = null;

			state = EPlayerState.LOGGED_IN;
			channel.removeListener(ENetworkKey.SYNCHRONOUS_TASK);
			channel.setLogger(LoggerManager.ROOT_LOGGER);
		}
	}

	public synchronized void joinMatch(Match match) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		this.match = match;
		match.join(this);
		state = EPlayerState.IN_MATCH;
		channel.setLogger(match.getMatchLogger());
	}

	public Channel getChannel() {
		return channel;
	}

	public void sendPacket(ENetworkKey key, Packet packet) {
		channel.sendPacket(key, packet);
	}

	public synchronized boolean isInMatch() {
		return state == EPlayerState.IN_MATCH || state == EPlayerState.IN_RUNNING_MATCH;
	}

	public void startMatch(Timer timer) throws IllegalStateException, NotAllPlayersReadyException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		match.startMatch(timer);
	}

	void matchStarted(TaskCollectingListener taskListener) {
		state = EPlayerState.IN_RUNNING_MATCH;
		channel.registerListener(taskListener);
	}

	public void forwardChatMessage(ChatMessagePacket packet) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);
		match.broadcastMessage(ENetworkKey.CHAT_MESSAGE, packet);
	}

	public void distributeTimeSync(TimeSyncPacket packet) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_RUNNING_MATCH);
		match.distributeTimeSync(this, packet);
	}

	public void setReady(boolean ready) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		if (playerInfo.isReady() != ready) { // only update if there is a real change
			playerInfo.setReady(ready);
			match.sendMatchInfoUpdate(NetworkConstants.ENetworkMessage.READY_STATE_CHANGED, this.getPlayerInfo());
		}
	}

	public EPlayerState getState() {
		return state;
	}
}
