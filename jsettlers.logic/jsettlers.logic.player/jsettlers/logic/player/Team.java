package jsettlers.logic.player;

import java.io.Serializable;
import java.util.LinkedList;

public class Team implements Serializable {
	private static final long serialVersionUID = 8051219906193296800L;

	private final byte teamId;
	private final LinkedList<Player> members = new LinkedList<Player>();

	public Team(byte teamId) {
		this.teamId = teamId;

	}

	public void registerPlayer(Player player) {
		members.add(player);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Team " + getTeamId() + "\n");
		for (Player curr : members) {
			builder.append(curr.toString());
		}

		return builder.toString();
	}

	public byte getTeamId() {
		return teamId;
	}
}
