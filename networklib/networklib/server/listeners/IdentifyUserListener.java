package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.PlayerInfoPacket;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.infrastructure.channel.packet.EmptyPacket;
import networklib.infrastructure.channel.reject.RejectPacket;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifyUserListener extends PacketChannelListener<PlayerInfoPacket> {

	private final Channel channel;
	private final IServerManager serverManager;

	public IdentifyUserListener(Channel channel, IServerManager userAcceptor) {
		super(ENetworkKey.IDENTIFY_USER, new GenericDeserializer<PlayerInfoPacket>(PlayerInfoPacket.class));
		this.channel = channel;
		this.serverManager = userAcceptor;
	}

	@Override
	protected void receivePacket(ENetworkKey key, PlayerInfoPacket playerInfo) throws IOException {
		Player player = new Player(playerInfo, channel);
		if (serverManager.acceptNewPlayer(player)) {
			channel.sendPacket(NetworkConstants.ENetworkKey.IDENTIFY_USER, new EmptyPacket());
			serverManager.sendMatchesToPlayer(player);
		} else {
			channel.sendPacket(NetworkConstants.ENetworkKey.REJECT_PACKET, new RejectPacket(NetworkConstants.ENetworkMessage.UNAUTHORIZED,
					NetworkConstants.ENetworkKey.IDENTIFY_USER));
		}
	}
}
