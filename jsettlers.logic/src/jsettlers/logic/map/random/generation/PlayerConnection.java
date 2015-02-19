package jsettlers.logic.map.random.generation;

public class PlayerConnection {

	private final PlayerStart player1;
	private final PlayerStart player2;

	public PlayerConnection(PlayerStart player1, PlayerStart player2) {
		this.player1 = player1;
		this.player2 = player2;

	}

	public boolean isSameAlliance() {
		return player1.getAlliance() == player2.getAlliance();
	}

	public PlayerStart getPlayer1() {
		return player1;
	}

	public PlayerStart getPlayer2() {
		return player2;
	}
}
