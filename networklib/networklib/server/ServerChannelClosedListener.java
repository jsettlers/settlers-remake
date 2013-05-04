package networklib.server;

import networklib.channel.IChannelClosedListener;
import networklib.server.game.Player;

class ServerChannelClosedListener implements IChannelClosedListener {
	/**
	 * 
	 */
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