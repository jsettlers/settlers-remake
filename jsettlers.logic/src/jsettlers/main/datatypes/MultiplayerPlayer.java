package jsettlers.main.datatypes;

import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.network.common.packets.PlayerInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiplayerPlayer implements IMultiplayerPlayer {

	private final String id;
	private final String name;
	private final boolean ready;

	public MultiplayerPlayer(PlayerInfoPacket playerInfoPacket) {
		this.id = playerInfoPacket.getId();
		this.name = playerInfoPacket.getName();
		this.ready = playerInfoPacket.isReady();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public String toString() {
		return "MultiplayerPlayer [id=" + id + ", name=" + name + ", ready=" + ready + "]";
	}
}
