package jsettlers.network.server.listeners;

import jsettlers.network.infrastructure.channel.IChannelClosedListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

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