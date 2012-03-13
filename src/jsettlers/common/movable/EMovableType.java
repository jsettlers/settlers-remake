package jsettlers.common.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.selectable.ESelectionType;

/**
 * Defines all types of movables with the tool they need.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EMovableType {
	BEARER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	SMITH(EMaterialType.HAMMER, ESelectionType.PEOPLE),
	LUMBERJACK(EMaterialType.AXE, ESelectionType.PEOPLE),
	STONECUTTER(EMaterialType.PICK, ESelectionType.PEOPLE),
	SAWMILLER(EMaterialType.SAW, ESelectionType.PEOPLE),
	FORESTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	MELTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	MINER(EMaterialType.PICK, ESelectionType.PEOPLE),
	FISHERMAN(EMaterialType.FISHINGROD, ESelectionType.PEOPLE),
	FARMER(EMaterialType.SCYTHE, ESelectionType.PEOPLE),
	MILLER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	BAKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	PIG_FARMER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	SLAUGHTERER(EMaterialType.AXE, ESelectionType.PEOPLE),
	CHARCOAL_BURNER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	WATERWORKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),

	BRICKLAYER(EMaterialType.HAMMER, ESelectionType.PEOPLE),
	DIGGER(EMaterialType.BLADE, ESelectionType.PEOPLE),

	THIEF(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS),
	PIONEER(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS),
	TEST_MOVABLE(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS),
	GEOLOGIST(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS),

	SWORDSMAN_L1(EMaterialType.SWORD, ESelectionType.SOLDIERS),
	SWORDSMAN_L2(EMaterialType.SWORD, ESelectionType.SOLDIERS),
	SWORDSMAN_L3(EMaterialType.SWORD, ESelectionType.SOLDIERS),

	PIKEMAN_L1(EMaterialType.SPEAR, ESelectionType.SOLDIERS),
	PIKEMAN_L2(EMaterialType.SPEAR, ESelectionType.SOLDIERS),
	PIKEMAN_L3(EMaterialType.SPEAR, ESelectionType.SOLDIERS),

	BOWMAN_L1(EMaterialType.BOW, ESelectionType.SOLDIERS),
	BOWMAN_L2(EMaterialType.BOW, ESelectionType.SOLDIERS),
	BOWMAN_L3(EMaterialType.BOW, ESelectionType.SOLDIERS),

	DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE),
	WHITEFLAGGED_DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE);

	private final EMaterialType tool;
	private final ESelectionType selectionType;

	EMovableType(EMaterialType tool, ESelectionType selectionType) {
		this.tool = tool;
		this.selectionType = selectionType;
	}

	/**
	 * @return the tool this settler needs to do his job.
	 */
	public EMaterialType getTool() {
		return tool;
	}

	/**
	 * @return selection type of this movable type
	 */
	public ESelectionType getSelectionType() {
		return selectionType;
	}
}
