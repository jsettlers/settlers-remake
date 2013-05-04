package networklib.client;

import networklib.NetworkConstants;
import networklib.channel.AsyncChannel;
import networklib.channel.GenericDeserializer;
import networklib.channel.IChannelClosedListener;
import networklib.channel.Packet;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.receiver.IPacketReceiver;
import networklib.server.game.EPlayerState;
import networklib.server.packets.ArrayOfMatchInfosPacket;
import networklib.server.packets.KeyOnlyPacket;
import networklib.server.packets.MapInfoPacket;
import networklib.server.packets.MatchInfoPacket;
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

	public NetworkClient(AsyncChannel channel) {
		this.channel = channel;
		channel.setChannelClosedListener(new IChannelClosedListener() {
			@Override
			public void channelClosed() {
				close();
			}
		});
	}

	public void logIn(String id, String name) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.CHANNEL_CONNECTED);

		playerInfo = new PlayerInfoPacket(NetworkConstants.Keys.IDENTIFY_USER, id, name);

		channel.registerListener(new IdentifiedUserListener(this));
		channel.sendPacketAsync(playerInfo);
	}

	public void requestMatches(IPacketReceiver<ArrayOfMatchInfosPacket> listener) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.ARRAY_OF_MATCHES, ArrayOfMatchInfosPacket.class, listener));
		channel.sendPacketAsync(new KeyOnlyPacket(NetworkConstants.Keys.REQUEST_MATCHES));
	}

	public void requestPlayersRunningMatches(IPacketReceiver<ArrayOfMatchInfosPacket> listener) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		channel.registerListener(generateDefaultListener(NetworkConstants.Keys.ARRAY_OF_MATCHES, ArrayOfMatchInfosPacket.class, listener));
		channel.sendPacketAsync(new KeyOnlyPacket(NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES));
	}

	public void requestOpenNewMatch(IPacketReceiver<MatchInfoPacket> listener, String matchName, byte maxPlayers, MapInfoPacket mapInfo)
			throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		channel.registerListener(new OpenedMatchListener(this, listener));
		channel.sendPacketAsync(new OpenNewMatchPacket(matchName, maxPlayers, mapInfo));
	}

	public void requestLeaveMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		channel.sendPacketAsync(new KeyOnlyPacket(NetworkConstants.Keys.REQUEST_LEAVE_MATCH));
		state = EPlayerState.LOGGED_IN;
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

	void openedMatch(MatchInfoPacket matchInfo) {
		this.state = EPlayerState.IN_MATCH;
		this.matchInfo = matchInfo;
	}

	void identifiedUser() {
		this.state = EPlayerState.LOGGED_IN;
	}

}
