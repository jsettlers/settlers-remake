/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.INetworkClient;
import jsettlers.network.client.interfaces.INetworkConnector;
import jsettlers.network.client.interfaces.ITaskScheduler;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.client.task.TaskPacketListener;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.client.time.ISynchronizableClock;
import jsettlers.network.client.time.TimeSyncSenderTimerTask;
import jsettlers.network.client.time.TimeSynchronizationListener;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.common.packets.BooleanMessagePacket;
import jsettlers.network.common.packets.ChatMessagePacket;
import jsettlers.network.common.packets.IdPacket;
import jsettlers.network.common.packets.MapInfoPacket;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.common.packets.MatchStartPacket;
import jsettlers.network.common.packets.OpenNewMatchPacket;
import jsettlers.network.common.packets.PlayerInfoPacket;
import jsettlers.network.infrastructure.channel.AsyncChannel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.IChannelClosedListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.match.EPlayerState;
import jsettlers.network.synchronic.timer.NetworkTimer;

/**
 * The {@link NetworkClient} class offers an interface to the servers methods. All methods of the {@link NetworkClient} class will never block. All
 * calls to the server are done by an asynchronous Thread.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClient implements ITaskScheduler, INetworkConnector, INetworkClient {

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
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.CHANGE_READY_STATE, new BooleanMessagePacket(ready));
	}

	@Override
	public void setStartFinished(boolean startFinished) throws IllegalStateException {
		EPlayerState.assertState(state, EPlayerState.IN_RUNNING_MATCH);
		channel.sendPacketAsync(NetworkConstants.ENetworkKey.CHANGE_START_FINISHED, new BooleanMessagePacket(startFinished));
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

	@Override
	public ITaskScheduler getTaskScheduler() {
		return this;
	}

	@Override
	public boolean haveAllPlayersStartFinished() {
		boolean allStartFinished = true;
		for (PlayerInfoPacket currPlayer : matchInfo.getPlayers()) {
			allStartFinished = allStartFinished && currPlayer.isStartFinished();
		}
		return allStartFinished;
	}

	@Override
	public INetworkConnector getNetworkConnector() {
		return this;
	}

}
