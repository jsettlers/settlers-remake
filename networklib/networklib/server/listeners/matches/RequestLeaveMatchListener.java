package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;
import networklib.server.packets.KeyOnlyPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestLeaveMatchListener extends PacketChannelListener<KeyOnlyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public RequestLeaveMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.REQUEST_LEAVE_MATCH, KeyOnlyPacket.DEFAULT_DESERIALIZER);
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket deserialized) throws IOException {
		serverManager.leaveMatch(player);
	}

}
