package networklib.server.listeners;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.common.packets.ChatMessagePacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ChatMessageForwardingListener extends PacketChannelListener<ChatMessagePacket> {

	private final IServerManager serverManager;
	private final Player player;

	public ChatMessageForwardingListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.CHAT_MESSAGE, new GenericDeserializer<ChatMessagePacket>(ChatMessagePacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, ChatMessagePacket packet) throws IOException {
		serverManager.forwardChatMessage(player, packet);
	}
}