package jsettlers.logic.player;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.Partition;
import jsettlers.logic.map.grid.partition.PartitionsGrid;

import java.io.Serializable;

/**
 * @author codingberlin
 */
public class CombatStrengthInformation implements ICombatStrengthInformation, Serializable {
	private static final long serialVersionUID = -2354305349487471882L;

	public static final float COMBAT_STRENGTH_WITHIN_OWN_LAND = 1;
	public static final int BORDER_50_PERCENT = 600;
	public static final int BORDER_85_PERCENT = 1160;
	private static final float[] COMBAT_STRENGTH_INCREASE = {613, 626, 639, 652, 665, 679, 693, 707, 721, 735, 750, 765, 780, 795, 810, 826,
			842, 858, 874, 890, 907, 924, 941, 958, 975, 993, 1011, 1029, 1047, 1065, 1084, 1103, 1122, 1141};
	private final byte playerId;
	private final PartitionsGrid partitionsGrid;
	private final int startAmountIndex;
	private static final float[] START_AMOUNT = {652, 600, 504, 444, 396, 348, 312, 300, 276, 252, 228, 216, 204, 192, 180, 168, 163, 156, 149, 144};

	public CombatStrengthInformation(byte playerId, PartitionsGrid partitionsGrid, byte sumOfPlayers) {
		this.playerId = playerId;
		this.partitionsGrid = partitionsGrid;
		this.startAmountIndex = sumOfPlayers - 1;
	}

	@Override public float getCombatStrengthAtPosition(ShortPoint2D position) {
		float combatStrength = getCombatStrength();
		if (combatStrength >= COMBAT_STRENGTH_WITHIN_OWN_LAND) {
			return combatStrength;
		}
		if (partitionsGrid.getPlayerIdAt(position.x, position.y) == playerId) {
			return COMBAT_STRENGTH_WITHIN_OWN_LAND;
		}
		return combatStrength;
	}

	@Override public float getCombatStrength() {
		float amountOfGold = START_AMOUNT[startAmountIndex];
		for (Partition playersPartition : partitionsGrid.getPartitionsOfPlayerId(playerId)) {
			amountOfGold += playersPartition.getMaterialCounts().getAmountOf(EMaterialType.GOLD);
		}

		if (amountOfGold <= BORDER_50_PERCENT) {
			return (amountOfGold / 12f) / 100f;
		}
		if (amountOfGold <= BORDER_85_PERCENT) {
			for (int i = COMBAT_STRENGTH_INCREASE.length; i >= 0; i--) {
				if (amountOfGold >= COMBAT_STRENGTH_INCREASE[i]) {
					return (51f + (float) i) / 100f;
				}
				return 0.5f;
			}
		}
		return ((1700f + amountOfGold - BORDER_85_PERCENT) / 20f) / 100f;
	}
}
