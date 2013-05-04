package networklib.server.game;

import networklib.channel.Channel;
import networklib.channel.Packet;
import networklib.client.exceptions.InvalidStateException;
import networklib.server.packets.PlayerInfoPacket;

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

	public void leaveMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		match.playerLeft(this);
		match = null;
	}

	public void joinMatch(Match match) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		this.match = match;
		match.join(this);
	}

	public void sendPacket(Packet packet) {
		channel.sendPacket(packet);
	}

	public Channel getChannel() {
		return channel;
	}
}
