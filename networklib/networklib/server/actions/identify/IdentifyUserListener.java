package networklib.server.actions.identify;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.actions.packets.AcknowledgePacket;
import networklib.server.actions.packets.PlayerInfoPacket;
import networklib.server.actions.packets.RejectPacket;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifyUserListener extends PacketChannelListener<PlayerInfoPacket> {

	private final Channel channel;
	private final IUserAcceptor userAcceptor;

	public IdentifyUserListener(Channel channel, IUserAcceptor userAcceptor) {
		super(NetworkConstants.Keys.IDENTIFY_USER, new GenericDeserializer<PlayerInfoPacket>(PlayerInfoPacket.class));
		this.channel = channel;
		this.userAcceptor = userAcceptor;
	}

	@Override
	protected void receivePacket(PlayerInfoPacket playerInfo) throws IOException {
		if (userAcceptor.acceptNewPlayer(new Player(playerInfo, channel))) {
			channel.sendPacket(new AcknowledgePacket(playerInfo.getKey()));
		} else {
			channel.sendPacket(new RejectPacket(NetworkConstants.Strings.UNAUTHORIZED, playerInfo.getKey()));
		}
	}

}
