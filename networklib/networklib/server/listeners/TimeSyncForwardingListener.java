package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.TimeSyncPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TimeSyncForwardingListener extends PacketChannelListener<TimeSyncPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public TimeSyncForwardingListener(IServerManager serverManager, Player player) {
		super(ENetworkKey.TIME_SYNC, new GenericDeserializer<TimeSyncPacket>(TimeSyncPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, TimeSyncPacket packet) throws IOException {
		serverManager.distributeTimeSync(player, packet);
	}

}
