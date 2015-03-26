package jsettlers.network.server.listeners;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.PlayerInfoPacket;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

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
