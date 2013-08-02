package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.ChatMessagePacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.match.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ChatMessageForwardingListener extends PacketChannelListener<ChatMessagePacket> {

	private final IServerManager serverManager;
	private final Player player;

	public ChatMessageForwardingListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.ENetworkKey.CHAT_MESSAGE, new GenericDeserializer<ChatMessagePacket>(ChatMessagePacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, ChatMessagePacket packet) throws IOException {
		serverManager.forwardChatMessage(player, packet);
	}
}