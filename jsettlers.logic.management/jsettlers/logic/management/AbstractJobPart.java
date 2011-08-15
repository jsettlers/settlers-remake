package jsettlers.logic.management;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;

/**
 * abstract class representing a request or an offer for something.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class AbstractJobPart implements ILocatable, IPlayerable {
	private boolean cancelled = false;
	private boolean fulfilled;
	private final byte player;

	public AbstractJobPart(byte player) {
		this.player = player;
	}

	@Override
	public byte getPlayer() {
		return player;
	}

	/**
	 * cancel this request or offer
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * set the fulfilled flag
	 */
	public void setFulfilled() {
		fulfilled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isFulfilled() {
		return fulfilled;
	}

}
