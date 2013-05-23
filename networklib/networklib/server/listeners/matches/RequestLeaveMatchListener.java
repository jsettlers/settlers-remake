package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.infrastructure.channel.packet.EmptyPacket;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestLeaveMatchListener extends PacketChannelListener<EmptyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public RequestLeaveMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.REQUEST_LEAVE_MATCH, EmptyPacket.DEFAULT_DESERIALIZER);
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, EmptyPacket deserialized) throws IOException {
		serverManager.leaveMatch(player);
	}

}
