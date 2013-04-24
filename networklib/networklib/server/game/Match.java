package networklib.server.game;

import java.util.LinkedList;
import java.util.UUID;

import networklib.server.actions.packets.MapInfoPacket;
import networklib.server.actions.packets.PlayerInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Match {

	private final String id = UUID.randomUUID().toString();
	private final LinkedList<Player> players = new LinkedList<Player>();
	private final int maxPlayers;
	private final MapInfoPacket map;
	private final String name;

	private EMatchState state = EMatchState.OPENED;

	public Match(int maxPlayers, MapInfoPacket map, String name) {
		this.maxPlayers = maxPlayers;
		this.map = map;
		this.name = name;
	}

	public EMatchState getState() {
		return state;
	}

	public boolean canJoin() {
		return state == EMatchState.OPENED && players.size() < maxPlayers;
	}

	public String getId() {
		return id;
	}

	public MapInfoPacket getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public PlayerInfoPacket[] getPlayerInfos() {
		synchronized (players) {
			PlayerInfoPacket[] result = new PlayerInfoPacket[players.size()];
			int i = 0;
			for (Player curr : players) {
				result[i] = curr.getPlayerInfo();
				i++;
			}

			return result;
		}
	}

}
