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

import java.util.EnumSet;
import java.util.Set;

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

	MAGE(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false, 0.7, 100f, 0f),

	SWORDSMAN_L1(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, 0.45, 100f, 10f),
	SWORDSMAN_L2(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, 0.45, 120f, 14f),
	SWORDSMAN_L3(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, 0.45, 150f, 20f),

	PIKEMAN_L1(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, 0.5, 200f, 4f),
	PIKEMAN_L2(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, 0.5, 240f, 5f),
	PIKEMAN_L3(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, 0.5, 300f, 6f),

	BOWMAN_L1(EMaterialType.BOW, ESelectionType.SOLDIERS, false, 0.6, 100f, 5f),
	BOWMAN_L2(EMaterialType.BOW, ESelectionType.SOLDIERS, false, 0.6, 120f, 6f),
	BOWMAN_L3(EMaterialType.BOW, ESelectionType.SOLDIERS, false, 0.6, 150f, 7f),

	DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false),
	WHITEFLAGGED_DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false);

	/**
	 * All step durations will be multiplied with this speedup factor.
	 */
	private static final float STEP_DURATION_SPEEDUP_FACTOR = 0.75f;

	public static final double DEFAULT_STEP_DURATION_SECONDS = 0.6;
	public static final float DEFAULT_HEALTH = 100f;
	public static final float DEFAULT_STRENGTH = 0f;

	public static final EMovableType[] values = EMovableType.values();
	public static final int NUMBER_OF_MOVABLETYPES = values.length;

	private final EMaterialType tool;
	private final ESelectionType selectionType;
	private final boolean needsPlayersGround;
	private final short stepDurationMs;
	private final float health;
	private final float strength;

	private static final Set<EMovableType> soldiers = EnumSet.of(
			SWORDSMAN_L1, SWORDSMAN_L2, SWORDSMAN_L3,
			BOWMAN_L1, BOWMAN_L2, BOWMAN_L3,
			PIKEMAN_L1, PIKEMAN_L2, PIKEMAN_L3);

	private static final Set<EMovableType> pikemen = EnumSet.of(
			PIKEMAN_L1, PIKEMAN_L2, PIKEMAN_L3);

	private static final Set<EMovableType> bowmen = EnumSet.of(
			BOWMAN_L1, BOWMAN_L2, BOWMAN_L3);

	EMovableType(EMaterialType tool, ESelectionType selectionType, boolean needsPlayersGround) {
		this(tool, selectionType, needsPlayersGround, DEFAULT_STEP_DURATION_SECONDS, DEFAULT_HEALTH, DEFAULT_STRENGTH);
	}

	EMovableType(EMaterialType tool, ESelectionType selectionType, boolean needsPlayersGround, double stepDurationSec, float health, float strength) {
		this.tool = tool;
		this.selectionType = selectionType;
		this.needsPlayersGround = needsPlayersGround;
		this.stepDurationMs = (short) (stepDurationSec * 1000 * STEP_DURATION_SPEEDUP_FACTOR);
		this.health = health;
		this.strength = strength;
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

	public float getStrength() {
		return strength;
	}

	public float getHealth() {
		return health;
	}

	public final boolean isUserControlled() {
		return !needsPlayersGround;
	}

	public short getStepDurationMs() {
		return stepDurationMs;
	}

	public static boolean isBowman(EMovableType movableType) {
		return bowmen.contains(movableType);
	}

	public static boolean isSoldier(EMovableType movableType) {
		return soldiers.contains(movableType);
	}

	public static boolean isPikeman(EMovableType movableType) {
		return pikemen.contains(movableType);
	}
}
