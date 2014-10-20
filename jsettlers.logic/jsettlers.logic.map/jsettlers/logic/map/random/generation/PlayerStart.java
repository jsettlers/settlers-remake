package jsettlers.logic.map.random.generation;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;

/**
 * The start point of a player
 * 
 * @author michael
 */
public class PlayerStart extends ShortPoint2D implements IPlayerable {
	/**
     * 
     */
	private static final long serialVersionUID = -7638597208002064175L;

	private final byte player;

	private final byte alliance;

	public PlayerStart(int x, int y, byte player, byte alliance) {
		super(x, y);
		this.player = player;
		this.alliance = alliance;
	}

	@Override
	public byte getPlayerId() {
		return player;
	}

	public byte getAlliance() {
		return alliance;
	}

}
