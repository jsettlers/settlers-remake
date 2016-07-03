package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiStatistics;

/**
 * @author codingberlin
 */
public abstract class AbstractTargetFinder implements ITargetFinder {

	protected final AiStatistics aiStatistics;
	protected final byte playerId;
	protected final int searchDistance;

	public AbstractTargetFinder(AiStatistics aiStatistics, byte playerId, int searchDistance) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.searchDistance = searchDistance;
	}

}
