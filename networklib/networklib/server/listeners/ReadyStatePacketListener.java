package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.common.packets.ReadyStatePacket;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReadyStatePacketListener extends PacketChannelListener<ReadyStatePacket> {

	private final IServerManager serverManager;
	private final Player player;

	public ReadyStatePacketListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.READY_STATE_CHANGE, new GenericDeserializer<ReadyStatePacket>(ReadyStatePacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, ReadyStatePacket packet) throws IOException {
		serverManager.setReadyStateForPlayer(player, packet.isReady());
	}

}
