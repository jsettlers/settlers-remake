package jsettlers.common.movable;

import jsettlers.common.material.EMaterialType;

public enum EMovableType {
	BEARER(EMaterialType.NO_MATERIAL),
	SMITH(EMaterialType.HAMMER),
	LUMBERJACK(EMaterialType.AXE),
	STONECUTTER(EMaterialType.PICK),
	SAWMILLER(EMaterialType.SAW),
	FORESTER(EMaterialType.NO_MATERIAL),
	MELTER(EMaterialType.NO_MATERIAL),
	MINER(EMaterialType.PICK),
	FISHERMAN(EMaterialType.FISHINGROD),
	FARMER(EMaterialType.SCYTHE),
	MILLER(EMaterialType.NO_MATERIAL),
	BAKER(EMaterialType.NO_MATERIAL),
	PIG_FARMER(EMaterialType.NO_MATERIAL),
	SLAUGHTERER(EMaterialType.AXE),
	CHARCOAL_BURNER(EMaterialType.NO_MATERIAL),
	WATERWORKER(EMaterialType.NO_MATERIAL),

	BRICKLAYER(EMaterialType.HAMMER),
	DIGGER(EMaterialType.BLADE),

	THIEF(EMaterialType.NO_MATERIAL),
	PIONEER(EMaterialType.NO_MATERIAL),
	TEST_MOVABLE(EMaterialType.NO_MATERIAL),

	SWORDSMAN_L1(EMaterialType.SWORD),
	SWORDSMAN_L2(EMaterialType.SWORD),
	SWORDSMAN_L3(EMaterialType.SWORD),

	PIKEMAN_L1(EMaterialType.SPEAR),
	PIKEMAN_L2(EMaterialType.SPEAR),
	PIKEMAN_L3(EMaterialType.SPEAR),

	BOWMAN_L1(EMaterialType.BOW),
	BOWMAN_L2(EMaterialType.BOW),
	BOWMAN_L3(EMaterialType.BOW),

	DONKEY(EMaterialType.NO_MATERIAL),
	WHITEFLAGGED_DONKEY(EMaterialType.NO_MATERIAL),
	GEOLOGIST(EMaterialType.NO_MATERIAL);

	private final EMaterialType tool;

	EMovableType(EMaterialType tool) {
		this.tool = tool;
	}

	/**
	 * @return the tool this settler needs to do his job.
	 */
	public EMaterialType getTool() {
		return tool;
	}
}
