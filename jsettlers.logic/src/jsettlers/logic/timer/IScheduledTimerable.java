package jsettlers.logic.timer;

import java.io.Serializable;

public interface IScheduledTimerable extends Serializable {

	/**
	 * 
	 * @return Returns the time till the next scheduling. (negative number, if no new scheduling should happen.)
	 */
	public int timerEvent();

	/**
	 * this method is called if a ITimerable object crashes during execution of timerEvent() to prevent further damage.
	 */
	public void kill();
}
