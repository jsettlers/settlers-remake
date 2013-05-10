package networklib.client;

import networklib.NetworkConstants;
import networklib.channel.AsyncChannel;
import networklib.channel.GenericDeserializer;
import networklib.channel.IChannelClosedListener;
import networklib.channel.packet.EmptyPacket;
import networklib.channel.packet.Packet;
import networklib.channel.reject.RejectPacket;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.receiver.BufferingPacketReceiver;
import networklib.client.receiver.IPacketReceiver;
import networklib.server.game.EPlayerState;
import networklib.server.packets.ArrayOfMatchInfosPacket;
import networklib.server.packets.ChatMessagePacket;
import networklib.server.packets.MapInfoPacket;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.MatchInfoUpdatePacket;
import networklib.server.packets.MatchStartPacket;
import networklib.server.packets.OpenNewMatchPacket;
import networklib.server.packets.PlayerInfoPacket;

/**
 * The {@link NetworkClient} class offers an interface to the servers methods. All methods of the {@link NetworkClient} class will never block. All
 * calls to the server are done by an asynchronous Thread.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClient {

	private final AsyncChannel channel;

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
	}

	public void logIn(String id, String name) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.CHANNEL_CONNECTED);

		playerInfo = new PlayerInfoPacket(id, name);

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
	 * @throws InvalidStateException
	 */
	public void requestOpenNewMatch(String matchName, byte maxPlayers, MapInfoPacket mapInfo, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver);

		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH, new OpenNewMatchPacket(matchName, maxPlayers, mapInfo));
	}

	public void requestLeaveMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_LEAVE_MATCH, new EmptyPacket());
	}

	public void reqeustJoinMatch(MatchInfoPacket match, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		registerMatchStartListeners(matchStartedListener, matchInfoUpdatedListener, chatMessageReceiver);

		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_JOIN_MATCH, match);
	}

	public void requestStartMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);

		channel.sendPacketAsync(NetworkConstants.Keys.REQUEST_START_MATCH, new EmptyPacket());
	}

	public void sendChatMessage(String message) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		channel.sendPacketAsync(NetworkConstants.Keys.CHAT_MESSAGE, new ChatMessagePacket(playerInfo.getId(), message));
	}

	private void registerMatchStartListeners(IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver) {
		channel.registerListener(new MatchInfoUpdatedListener(this, matchInfoUpdatedListener));
		channel.registerListener(new MatchStartedListener(this, matchStartedListener));
		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.CHAT_MESSAGE, ChatMessagePacket.class, chatMessageReceiver));
	}

	private <T extends Packet> DefaultClientPacketListener<T> generateDefaultListener(int key, Class<T> classType, IPacketReceiver<T> listener) {
		return new DefaultClientPacketListener<T>(key, new GenericDeserializer<T>(classType), listener);
	}

	public EPlayerState getState() {
		return state;
	}

	public void close() {
		state = EPlayerState.DISCONNECTED;
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

	public void registerRejectReceiver(BufferingPacketReceiver<RejectPacket> rejectListener) {
		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.REJECT_PACKET, RejectPacket.class, rejectListener));
	}

}
