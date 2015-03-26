package jsettlers.network.server.listeners;

import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.TimeSyncPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

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
