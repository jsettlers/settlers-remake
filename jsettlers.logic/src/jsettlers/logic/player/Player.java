package jsettlers.logic.player;

import java.io.Serializable;

import jsettlers.graphics.map.IMessenger;
import jsettlers.graphics.messages.Message;

/**
 * This class represents a player in the game. It can be used to access player specific statistics and methods.
 * 
 * @author Andreas Eberle
 * 
 */
public class Player implements Serializable, IMessenger {
	private static final long serialVersionUID = 1L;

	public final byte playerId;
	private final Team team;

	private transient IMessenger messenger;

	public Player(byte playerId, Team team) {
		this.playerId = playerId;
		this.team = team;
		team.registerPlayer(this);
	}

	@Override
	public String toString() {
		return "Player " + playerId + " of team " + team.getTeamId();
	}

	public void setMessenger(IMessenger messenger) {
		this.messenger = messenger;
	}

	@Override
	public void showMessage(Message message) {
		if (messenger != null) {
			messenger.showMessage(message);
		}
	}
}
