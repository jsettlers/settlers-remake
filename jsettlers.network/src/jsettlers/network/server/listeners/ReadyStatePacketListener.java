package jsettlers.network.server.listeners;

import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.BooleanMessagePacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReadyStatePacketListener extends PacketChannelListener<BooleanMessagePacket> {

	private final IServerManager serverManager;
	private final Player player;

	public ReadyStatePacketListener(IServerManager serverManager, Player player) {
		super(ENetworkKey.CHANGE_READY_STATE, new GenericDeserializer<BooleanMessagePacket>(BooleanMessagePacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, BooleanMessagePacket packet) throws IOException {
		serverManager.setReadyStateForPlayer(player, packet.getValue());
	}

}
