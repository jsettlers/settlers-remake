package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.ReadyStatePacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.match.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReadyStatePacketListener extends PacketChannelListener<ReadyStatePacket> {

	private final IServerManager serverManager;
	private final Player player;

	public ReadyStatePacketListener(IServerManager serverManager, Player player) {
		super(ENetworkKey.CHANGE_READY_STATE, new GenericDeserializer<ReadyStatePacket>(ReadyStatePacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, ReadyStatePacket packet) throws IOException {
		serverManager.setReadyStateForPlayer(player, packet.isReady());
	}

}
