package jsettlers.network.server.listeners;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.ChatMessagePacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

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