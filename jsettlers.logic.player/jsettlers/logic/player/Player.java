package jsettlers.logic.player;

import java.io.Serializable;

/**
 * This class represents a player in the game. It can be used to access player specific statistics and methods.
 * 
 * @author Andreas Eberle
 * 
 */
public class Player implements Serializable {
	private static final long serialVersionUID = 1L;

	private final byte playerId;
	private final Team team;

	public Player(byte playerId, Team team) {
		this.playerId = playerId;
		this.team = team;
		team.registerPlayer(this);
	}

	@Override
	public String toString() {
		return "Player " + playerId + " of team " + team.getTeamId();
	}
}
