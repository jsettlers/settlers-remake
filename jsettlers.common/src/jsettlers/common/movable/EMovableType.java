/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.selectable.ESelectionType;

/**
 * Defines all types of movables with the tool they need, their selection level and if they need their players ground.
 *
 * @author Andreas Eberle
 *
 */
public enum EMovableType {
	BEARER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	SMITH(EMaterialType.HAMMER, ESelectionType.PEOPLE, true),
	LUMBERJACK(EMaterialType.AXE, ESelectionType.PEOPLE, true),
	STONECUTTER(EMaterialType.PICK, ESelectionType.PEOPLE, true),
	SAWMILLER(EMaterialType.SAW, ESelectionType.PEOPLE, true),
	FORESTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	MELTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	MINER(EMaterialType.PICK, ESelectionType.PEOPLE, true),
	FISHERMAN(EMaterialType.FISHINGROD, ESelectionType.PEOPLE, true),
	FARMER(EMaterialType.SCYTHE, ESelectionType.PEOPLE, true),
	MILLER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	BAKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	PIG_FARMER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	SLAUGHTERER(EMaterialType.AXE, ESelectionType.PEOPLE, true),
	CHARCOAL_BURNER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	WATERWORKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	WINEGROWER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),
	HEALER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true),

	BRICKLAYER(EMaterialType.HAMMER, ESelectionType.PEOPLE, true),
	DIGGER(EMaterialType.BLADE, ESelectionType.PEOPLE, true),

	THIEF(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false),
	PIONEER(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false),
	GEOLOGIST(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false),

	SWORDSMAN_L1(EMaterialType.SWORD, ESelectionType.SOLDIERS, false),
	SWORDSMAN_L2(EMaterialType.SWORD, ESelectionType.SOLDIERS, false),
	SWORDSMAN_L3(EMaterialType.SWORD, ESelectionType.SOLDIERS, false),

	PIKEMAN_L1(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false),
	PIKEMAN_L2(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false),
	PIKEMAN_L3(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false),

	BOWMAN_L1(EMaterialType.BOW, ESelectionType.SOLDIERS, false),
	BOWMAN_L2(EMaterialType.BOW, ESelectionType.SOLDIERS, false),
	BOWMAN_L3(EMaterialType.BOW, ESelectionType.SOLDIERS, false),

	DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false),
	WHITEFLAGGED_DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false);

	public static final EMovableType[] values = EMovableType.values();
	public static final int NUMBER_OF_MOVABLETYPES = values.length;

	private final EMaterialType tool;
	private final ESelectionType selectionType;
	private final boolean needsPlayersGround;

	EMovableType(EMaterialType tool, ESelectionType selectionType, boolean needsPlayersGround) {
		this.tool = tool;
		this.selectionType = selectionType;
		this.needsPlayersGround = needsPlayersGround;
	}

	/**
	 * @return the tool this settler needs to do his job.
	 */
	public final EMaterialType getTool() {
		return tool;
	}

	/**
	 * @return selection type of this movable type
	 */
	public final ESelectionType getSelectionType() {
		return selectionType;
	}

	public final boolean needsPlayersGround() {
		return needsPlayersGround;
	}

	public final boolean isMoveToAble() {
		return !needsPlayersGround;
	}

	public static boolean isBowman(EMovableType movableType) {
		return movableType.getTool() == EMaterialType.BOW;
	}
}
