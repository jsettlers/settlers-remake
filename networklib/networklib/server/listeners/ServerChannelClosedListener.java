package networklib.server.listeners;

import networklib.infrastructure.channel.IChannelClosedListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerChannelClosedListener implements IChannelClosedListener {
	private final IServerManager serverManager;
	private final Player player;

	public ServerChannelClosedListener(IServerManager serverManager, Player player) {
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	public void channelClosed() {
		this.serverManager.channelClosed(player);
	}
}