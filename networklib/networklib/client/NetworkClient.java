package networklib.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.client.interfaces.IGameClock;
import networklib.client.interfaces.INetworkClient;
import networklib.client.interfaces.ITaskScheduler;
import networklib.client.receiver.IPacketReceiver;
import networklib.client.task.TaskPacketListener;
import networklib.client.task.packets.TaskPacket;
import networklib.client.time.ISynchronizableClock;
import networklib.client.time.TimeSyncSenderTimerTask;
import networklib.client.time.TimeSynchronizationListener;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.IdPacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.OpenNewMatchPacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.common.packets.ReadyStatePacket;
import networklib.infrastructure.channel.AsyncChannel;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.IChannelClosedListener;
import networklib.infrastructure.channel.packet.EmptyPacket;
import networklib.infrastructure.channel.packet.Packet;
import networklib.infrastructure.channel.reject.RejectPacket;
import networklib.server.match.EPlayerState;
import networklib.synchronic.timer.NetworkTimer;

/**
 * The {@link NetworkClient} class offers an interface to the servers methods. All methods of the {@link NetworkClient} class will never block. All
 * calls to the server are done by an asynchronous Thread.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClient implements ITaskScheduler, INetworkClient {

	private final AsyncChannel channel;
	private final Timer timer;
	private final INetworkClientClock clock;

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
	public NetworkClient(AsyncChannel channel, IChannelClosedListener channelClosedListener) {
		this(channel, channelClosedListener, new NetworkTimer());
	}

	public NetworkClient(String serverAddress, IChannelClosedListener channelClosedListener) throws UnknownHostException, IOException {
		this(new AsyncChannel(serverAddress, NetworkConstants.Server.SERVER_PORT), channelClosedListener);
	}

	NetworkClient(AsyncChannel channel, final IChannelClosedListener channelClosedListener, INetworkClientClock gameClock) {
		this.channel = channel;
		channel.setChannelClosedListener(new IChannelClosedListener() {
			@Override
			public void channelClosed() {
				close();

				if (channelClosedListener != null)
					channelClosedListener.channelClosed();
			}
		});

		this.timer = new Timer("NetworkClientTimer");
		this.clock = gameClock;

		if (!channel.isStarted()) {
			channel.start();
		}
	}

	@Override
	public void logIn(String id, String name, IPacketReceiver<ArrayOfMatchInfosPacket> matchesReceiver) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.CHANNEL_CONNECTED);

		playerInfo = new PlayerInfoPacket(id, name, false);

		channel.registerListener(new IdentifiedUserListener(this));
		channel.registerListener(generateDefaultListener(NetworkConstants.ENetworkKey.ARRAY_OF_MATCHES, ArrayOfMatchInfosPacket.class,
				matchesReceiver));
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.IDENTIFY_USER, playerInfo);
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
	@Override
	public void openNewMatch(String matchName, int maxPlayers, MapInfoPacket mapInfo, long randomSeed,
			IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);
		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.REQUEST_OPEN_NEW_MATCH, new OpenNewMatchPacket(matchName, maxPlayers, mapInfo,
				randomSeed));
	}

	@Override
	public void joinMatch(String matchId, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);
		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.REQUEST_JOIN_MATCH, new IdPacket(matchId));
	}

	@Override
	public void leaveMatch() {
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.REQUEST_LEAVE_MATCH, new EmptyPacket());
	}

	@Override
	public void startMatch() throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.REQUEST_START_MATCH, new EmptyPacket());
	}

	@Override
	public void setReadyState(boolean ready) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.CHANGE_READY_STATE, new ReadyStatePacket(ready));
	}

	@Override
	public void sendChatMessage(String message) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.CHAT_MESSAGE, new ChatMessagePacket(playerInfo.getId(), message));
	}

	@Override
	public void registerRejectReceiver(IPacketReceiver<RejectPacket> rejectListener) {
		channel.registerListener(generateDefaultListener(NetworkConstants.ENetworkKey.REJECT_PACKET, RejectPacket.class, rejectListener));
	}

	private void registerMatchStartListeners(IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver) {
		channel.registerListener(new MatchInfoUpdatedListener(this, matchInfoUpdatedListener));
		channel.registerListener(new MatchStartedListener(this, matchStartedListener));
		channel.registerListener(generateDefaultListener(ENetworkKey.CHAT_MESSAGE, ChatMessagePacket.class, chatMessageReceiver));
		channel.registerListener(new TaskPacketListener(clock));
	}

	@Override
	public void scheduleTask(TaskPacket task) {
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, task);
	}

	private <T extends Packet> DefaultClientPacketListener<T> generateDefaultListener(ENetworkKey key, Class<T> classType, IPacketReceiver<T> listener) {
		return new DefaultClientPacketListener<T>(key, new GenericDeserializer<T>(classType), listener);
	}

	@Override
	public EPlayerState getState() {
		return state;
	}

	@Override
	public void close() {
		state = EPlayerState.DISCONNECTED;
		timer.cancel();
		channel.close();
		clock.stopExecution();
	}

	void identifiedUserEvent() {
		this.state = EPlayerState.LOGGED_IN;
		channel.removeListener(NetworkConstants.ENetworkKey.IDENTIFY_USER);
	}

	private void playerJoinedEvent(MatchInfoPacket matchInfo) {
		if (this.matchInfo == null) { // only if we joined.
			this.state = EPlayerState.IN_MATCH;
			this.matchInfo = matchInfo;
		}
	}

	private void playerLeftEvent(MatchInfoUpdatePacket matchInfoUpdate) {
		MatchInfoPacket updatedInfo = matchInfoUpdate.getMatchInfo();
		assert updatedInfo != null && updatedInfo.getId().equals(updatedInfo.getId()) : "received match info for wrong match! " + updatedInfo.getId();

		if (playerInfo.getId().equals(matchInfoUpdate.getUpdatedPlayer().getId())) { // if this client left the game
			state = EPlayerState.LOGGED_IN;
			this.matchInfo = null;

			channel.removeListener(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE);
			channel.removeListener(NetworkConstants.ENetworkKey.CHAT_MESSAGE);
		} else {
			this.matchInfo = updatedInfo;
		}
	}

	void matchStartedEvent() {
		this.state = EPlayerState.IN_RUNNING_MATCH;
		channel.removeListener(NetworkConstants.ENetworkKey.MATCH_STARTED);

		startTimeSynchronization(clock);
		channel.initPinging();
	}

	private void startTimeSynchronization(ISynchronizableClock clock) {
		channel.registerListener(new TimeSynchronizationListener(channel, clock));
		TimeSyncSenderTimerTask timeSyncSender = new TimeSyncSenderTimerTask(channel, clock);
		timer.schedule(timeSyncSender, 0, NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL);
	}

	void matchInfoUpdated(MatchInfoUpdatePacket matchInfoUpdate) {
		switch (matchInfoUpdate.getUpdateReason()) {
		case PLAYER_LEFT:
			playerLeftEvent(matchInfoUpdate);
			return; // this prevents that the matchInfo is set

		case PLAYER_JOINED:
			playerJoinedEvent(matchInfoUpdate.getMatchInfo());
			break;

		default:
			break;
		}

		matchInfo = matchInfoUpdate.getMatchInfo();
	}

	/**
	 * @return the playerInfo
	 */
	@Override
	public PlayerInfoPacket getPlayerInfo() {
		return playerInfo;
	}

	/**
	 * @return the matchInfo
	 */
	@Override
	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

	@Override
	public IGameClock getGameClock() {
		return clock;
	}

	@Override
	public int getRoundTripTimeInMs() {
		return channel.getRoundTripTime().getRtt();
	}

	@Override
	public void shutdown() {
		close();
	}
}
