package jsettlers.logic.management;

import jsettlers.common.position.ISPosition2D;

/**
 * interface for jobs.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IJob {
	/**
	 * @return first position to be visited to fulfill this job
	 */
	public ISPosition2D getFirstPos();

}
