package networklib.client;

import java.util.Timer;

import networklib.NetworkConstants;
import networklib.channel.AsyncChannel;
import networklib.channel.GenericDeserializer;
import networklib.channel.IChannelClosedListener;
import networklib.channel.packet.EmptyPacket;
import networklib.channel.packet.Packet;
import networklib.channel.reject.RejectPacket;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.packets.TaskPacket;
import networklib.client.receiver.IPacketReceiver;
import networklib.client.task.ITaskScheduler;
import networklib.client.task.TaskPacketListener;
import networklib.client.time.ISynchronizableClock;
import networklib.client.time.TimeSyncSenderTimerTask;
import networklib.client.time.TimeSynchronizationListener;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.OpenNewMatchPacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.common.packets.ReadyStatePacket;
import networklib.server.game.EPlayerState;

/**
 * The {@link NetworkClient} class offers an interface to the servers methods. All methods of the {@link NetworkClient} class will never block. All
 * calls to the server are done by an asynchronous Thread.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClient {

	private final AsyncChannel channel;
	private final Timer timer;

	private EPlayerState state = EPlayerState.CHANNEL_CONNECTED;
	private PlayerInfoPacket playerInfo;

	private MatchInfoPacket matchInfo;

	/**
	 * 
	 * @param channel
	 * @param channelClosedListener
	 *            The listener to be called when the channel is closed<br>
	 *            or null, if no listener should be registered.
	 */
	public NetworkClient(AsyncChannel channel, final IChannelClosedListener channelClosedListener) {
		this.channel = channel;
		channel.setChannelClosedListener(new IChannelClosedListener() {
			@Override
			public void channelClosed() {
				close();

				if (channelClosedListener != null)
					channelClosedListener.channelClosed();
			}
		});

		timer = new Timer("NetworkClientTimer");
	}

	public void logIn(String id, String name) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.CHANNEL_CONNECTED);

		playerInfo = new PlayerInfoPacket(id, name, false);

		channel.registerListener(new IdentifiedUserListener(this));
		channel.sendPacketAsync(NetworkConstants.Keys.IDENTIFY_USER, playerInfo);
	}

	public void requestMatches(IPacketReceiver<ArrayOfMatchInfosPacket> listener) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.ARRAY_OF_MATCHES, ArrayOfMatchInfosPacket.class, listener));
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_MATCHES, new EmptyPacket());
	}

	public void requestPlayersRunningMatches(IPacketReceiver<ArrayOfMatchInfosPacket> listener) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.ARRAY_OF_MATCHES, ArrayOfMatchInfosPacket.class, listener));
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES, new EmptyPacket());
	}

	/**
	 * 
	 * @param matchName
	 * @param maxPlayers
	 * @param mapInfo
	 * @param matchStartedListener
	 * @param matchInfoUpdatedListener
	 *            This listener will receive all further updates on the match.
	 * @param chatMessageReceiver
	 * @param taskScheduler
	 * @throws InvalidStateException
	 */
	public void requestOpenNewMatch(String matchName, byte maxPlayers, MapInfoPacket mapInfo, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver,
			ITaskScheduler taskScheduler)
			throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);
		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver, taskScheduler);
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH, new OpenNewMatchPacket(matchName, maxPlayers, mapInfo));
	}

	public void requestLeaveMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_LEAVE_MATCH, new EmptyPacket());
	}

	public void requestJoinMatch(MatchInfoPacket match, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver,
			ITaskScheduler taskScheduler)
			throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);
		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver, taskScheduler);
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_JOIN_MATCH, match);
	}

	public void requestStartMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_START_MATCH, new EmptyPacket());
	}

	public void setReadyState(boolean ready) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		channel.sendPacketAsync(NetworkConstants.Keys.READY_STATE_CHANGE, new ReadyStatePacket(ready));
	}

	public void sendChatMessage(String message) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);
		channel.sendPacketAsync(NetworkConstants.Keys.CHAT_MESSAGE, new ChatMessagePacket(playerInfo.getId(), message));
	}

	public void submitTask(TaskPacket task) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_RUNNING_MATCH);
		channel.sendPacketAsync(NetworkConstants.Keys.SYNCHRONOUS_TASK, task);
	}

	public void registerRejectReceiver(IPacketReceiver<RejectPacket> rejectListener) {
		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.REJECT_PACKET, RejectPacket.class, rejectListener));
	}

	public void startTimeSynchronization(ISynchronizableClock clock) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_RUNNING_MATCH);

		channel.registerListener(new TimeSynchronizationListener(channel, clock));
		TimeSyncSenderTimerTask timeSyncSender = new TimeSyncSenderTimerTask(channel, clock);
		timer.schedule(timeSyncSender, 0, NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL);
	}

	private void registerMatchStartListeners(IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver,
			ITaskScheduler taskScheduler) {
		channel.registerListener(new MatchInfoUpdatedListener(this, matchInfoUpdatedListener));
		channel.registerListener(new MatchStartedListener(this, matchStartedListener));
		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.CHAT_MESSAGE, ChatMessagePacket.class, chatMessageReceiver));
		channel.registerListener(new TaskPacketListener(taskScheduler));
	}

	private <T extends Packet> DefaultClientPacketListener<T> generateDefaultListener(int key, Class<T> classType, IPacketReceiver<T> listener) {
		return new DefaultClientPacketListener<T>(key, new GenericDeserializer<T>(classType), listener);
	}

	public EPlayerState getState() {
		return state;
	}

	public void close() {
		state = EPlayerState.DISCONNECTED;
		timer.cancel();
		channel.close();
	}

	void identifiedUserEvent() {
		this.state = EPlayerState.LOGGED_IN;
		channel.removeListener(NetworkConstants.Keys.IDENTIFY_USER);
	}

	private void playerJoinedEvent(MatchInfoPacket matchInfo) {
		if (this.matchInfo == null) { // only if we joined.
			this.state = EPlayerState.IN_MATCH;
			this.matchInfo = matchInfo;
		}
	}

	private void playerLeftEvent(MatchInfoPacket matchInfo) {
		assert matchInfo != null && matchInfo.getId().equals(matchInfo.getId()) : "received match info for wrong match! " + matchInfo.getId();

		boolean stillInGame = false;
		for (PlayerInfoPacket currPlayer : matchInfo.getPlayers()) {
			if (currPlayer.getId().equals(playerInfo.getId())) {
				stillInGame = true;
				break;
			}
		}

		if (!stillInGame) {
			state = EPlayerState.LOGGED_IN;
			matchInfo = null;

			channel.removeListener(NetworkConstants.Keys.MATCH_INFO_UPDATE);
			channel.removeListener(NetworkConstants.Keys.CHAT_MESSAGE);
		}
	}

	void matchStartedEvent() {
		this.state = EPlayerState.IN_RUNNING_MATCH;
		channel.removeListener(NetworkConstants.Keys.MATCH_STARTED);
	}

	void matchInfoUpdated(MatchInfoUpdatePacket matchInfoUpdate) {
		switch (matchInfoUpdate.getUpdateReason()) {
		case NetworkConstants.Messages.PLAYER_LEFT:
			playerLeftEvent(matchInfoUpdate.getMatchInfo());
			return;

		case NetworkConstants.Messages.PLAYER_JOINED:
			playerJoinedEvent(matchInfoUpdate.getMatchInfo());
			break;
		}

		matchInfo = matchInfoUpdate.getMatchInfo();
	}

	/**
	 * @return the playerInfo
	 */
	public PlayerInfoPacket getPlayerInfo() {
		return playerInfo;
	}

	/**
	 * @return the matchInfo
	 */
	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

}
