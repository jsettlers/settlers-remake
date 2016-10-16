package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;

/**
 * @author codingberlin
 */
public class SameBlockedPartitionLikePlayerFilter implements AiPositions.AiPositionFilter {

	private final AiStatistics aiStatistics;
	private final byte playerId;

	public SameBlockedPartitionLikePlayerFilter(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
	}

	@Override
	public boolean contains(int x, int y) {
		return aiStatistics.hasPlayersBlockedPartition(playerId, x, y);
	}
}
