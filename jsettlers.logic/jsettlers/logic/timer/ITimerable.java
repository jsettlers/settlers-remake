package jsettlers.logic.timer;

import synchronic.timer.INetworkTimerable;

public interface ITimerable extends INetworkTimerable {
	@Override
	public void timerEvent();

	/**
	 * this method is called if a ITimerable object crashes during execution of timerEvent() to prevent further damage.
	 */
	public void kill();
}
