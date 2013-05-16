package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.common.packets.TimeSyncPacket;
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
		super(NetworkConstants.Keys.TIME_SYNC, new GenericDeserializer<TimeSyncPacket>(TimeSyncPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, TimeSyncPacket packet) throws IOException {
		serverManager.distributeTimeSync(player, packet);
	}

}
