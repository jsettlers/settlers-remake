package networklib.server.actions.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;
import networklib.client.exceptions.InvalidStateException;
import networklib.server.actions.packets.KeyOnlyPacket;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestLeaveMatchListener extends PacketChannelListener<KeyOnlyPacket> {

	private final Player player;

	public RequestLeaveMatchListener(Player player) {
		super(NetworkConstants.Keys.REQUEST_LEAVE_MATCH, KeyOnlyPacket.DEFAULT_DESERIALIZER);
		this.player = player;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket deserialized) throws IOException {
		try {
			player.leaveMatch();
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

}
