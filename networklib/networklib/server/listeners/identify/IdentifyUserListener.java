package networklib.server.listeners.identify;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.packet.EmptyPacket;
import networklib.server.IServerManager;
import networklib.server.game.Player;
import networklib.server.packets.PlayerInfoPacket;
import networklib.server.packets.RejectPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifyUserListener extends PacketChannelListener<PlayerInfoPacket> {

	private final Channel channel;
	private final IServerManager userAcceptor;

	public IdentifyUserListener(Channel channel, IServerManager userAcceptor) {
		super(NetworkConstants.Keys.IDENTIFY_USER, new GenericDeserializer<PlayerInfoPacket>(PlayerInfoPacket.class));
		this.channel = channel;
		this.userAcceptor = userAcceptor;
	}

	@Override
	protected void receivePacket(int key, PlayerInfoPacket playerInfo) throws IOException {
		if (userAcceptor.acceptNewPlayer(new Player(playerInfo, channel))) {
			channel.sendPacket(NetworkConstants.Keys.IDENTIFY_USER, new EmptyPacket());
		} else {
			channel.sendPacket(NetworkConstants.Keys.REJECT_PACKET, new RejectPacket(NetworkConstants.Messages.UNAUTHORIZED,
					NetworkConstants.Keys.IDENTIFY_USER));
		}
	}
}
