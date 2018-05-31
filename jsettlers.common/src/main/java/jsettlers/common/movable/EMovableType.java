/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
	BEARER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	SMITH(EMaterialType.HAMMER, ESelectionType.PEOPLE, true, false),
	LUMBERJACK(EMaterialType.AXE, ESelectionType.PEOPLE, true, false),
	STONECUTTER(EMaterialType.PICK, ESelectionType.PEOPLE, true, false),
	SAWMILLER(EMaterialType.SAW, ESelectionType.PEOPLE, true, false),
	FORESTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	MELTER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	MINER(EMaterialType.PICK, ESelectionType.PEOPLE, true, false),
	FISHERMAN(EMaterialType.FISHINGROD, ESelectionType.PEOPLE, true, false),
	FARMER(EMaterialType.SCYTHE, ESelectionType.PEOPLE, true, false),
	MILLER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	BAKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	PIG_FARMER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	DONKEY_FARMER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	SLAUGHTERER(EMaterialType.AXE, ESelectionType.PEOPLE, true, false),
	CHARCOAL_BURNER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	WATERWORKER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	WINEGROWER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	HEALER(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, true, false),
	DOCKWORKER(EMaterialType.HAMMER, ESelectionType.PEOPLE, true, false),

	BRICKLAYER(EMaterialType.HAMMER, ESelectionType.PEOPLE, true, false),
	DIGGER(EMaterialType.BLADE, ESelectionType.PEOPLE, true, false),

	THIEF(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false, true),
	PIONEER(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false, true),
	GEOLOGIST(EMaterialType.NO_MATERIAL, ESelectionType.SPECIALISTS, false, true),

	MAGE(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false, true, 0.7, 100f, 0f),

	SWORDSMAN_L1(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, true, 0.45, 100f, 10f),
	SWORDSMAN_L2(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, true, 0.45, 120f, 14f),
	SWORDSMAN_L3(EMaterialType.SWORD, ESelectionType.SOLDIERS, false, true, 0.45, 150f, 20f),

	PIKEMAN_L1(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, true, 0.5, 200f, 4f),
	PIKEMAN_L2(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, true, 0.5, 240f, 5f),
	PIKEMAN_L3(EMaterialType.SPEAR, ESelectionType.SOLDIERS, false, true, 0.5, 300f, 6f),

	BOWMAN_L1(EMaterialType.BOW, ESelectionType.SOLDIERS, false, true, 0.6, 100f, 5f),
	BOWMAN_L2(EMaterialType.BOW, ESelectionType.SOLDIERS, false, true, 0.6, 120f, 6f),
	BOWMAN_L3(EMaterialType.BOW, ESelectionType.SOLDIERS, false, true, 0.6, 150f, 7f),

	DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false, true),
	WHITEFLAGGED_DONKEY(EMaterialType.NO_MATERIAL, ESelectionType.PEOPLE, false, true),

	FERRY(EMaterialType.NO_MATERIAL, ESelectionType.SHIPS, false, true, 0.6, 400f, 0f),
	CARGO_BOAT(EMaterialType.NO_MATERIAL, ESelectionType.SHIPS, false, false);

	/**
	 * All step durations will be multiplied with this speedup factor.
	 */
	private static final float STEP_DURATION_SPEEDUP_FACTOR = 0.75f;

	public static final double DEFAULT_STEP_DURATION_SECONDS = 0.6;
	public static final float  DEFAULT_HEALTH                = 100f;
	public static final float  DEFAULT_STRENGTH              = 0f;

	public static final EMovableType[] VALUES                 = EMovableType.values();
	public static final int            NUMBER_OF_MOVABLETYPES = VALUES.length;

	public static final Set<EMovableType> SWORDSMEN = EnumSet.of(SWORDSMAN_L1, SWORDSMAN_L2, SWORDSMAN_L3);
	public static final Set<EMovableType> PIKEMEN   = EnumSet.of(PIKEMAN_L1, PIKEMAN_L2, PIKEMAN_L3);
	public static final Set<EMovableType> BOWMEN    = EnumSet.of(BOWMAN_L1, BOWMAN_L2, BOWMAN_L3);

	public static final Set<EMovableType> SOLDIERS = EnumSet.of(
		SWORDSMAN_L1, SWORDSMAN_L2, SWORDSMAN_L3,
		PIKEMAN_L1, PIKEMAN_L2, PIKEMAN_L3,
		BOWMAN_L1, BOWMAN_L2, BOWMAN_L3
	);

	public static final Set<EMovableType> INFANTRY = EnumSet.of(
		SWORDSMAN_L1, SWORDSMAN_L2, SWORDSMAN_L3,
		PIKEMAN_L1, PIKEMAN_L2, PIKEMAN_L3
	);

	public static final Set<EMovableType> SHIPS = EnumSet.of(FERRY, CARGO_BOAT);

	private final EMaterialType  tool;
	private final ESelectionType selectionType;
	private final boolean        needsPlayersGround;
	private final boolean        isPlayerControllable;
	private final short          stepDurationMs;
	private final float          health;
	private final float          strength;

	EMovableType(EMaterialType tool, ESelectionType selectionType, boolean needsPlayersGround, boolean isPlayerControllable) {
		this(tool, selectionType, needsPlayersGround, isPlayerControllable, DEFAULT_STEP_DURATION_SECONDS, DEFAULT_HEALTH, DEFAULT_STRENGTH);
	}

	EMovableType(EMaterialType tool, ESelectionType selectionType, boolean needsPlayersGround, boolean isPlayerControllable, double stepDurationSec, float health, float strength) {
		this.tool = tool;
		this.selectionType = selectionType;
		this.needsPlayersGround = needsPlayersGround;
		this.isPlayerControllable = isPlayerControllable;
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

	public final boolean isPlayerControllable() {
		return isPlayerControllable;
	}

	public short getStepDurationMs() {
		return stepDurationMs;
	}

	public boolean isBowman() {
		return BOWMEN.contains(this);
	}

	public boolean isSoldier() {
		return SOLDIERS.contains(this);
	}

	public boolean isSwordsman() {
		return SWORDSMEN.contains(this);
	}

	public boolean isPikeman() {
		return PIKEMEN.contains(this);
	}

	public boolean isInfantry() {
		return INFANTRY.contains(this);
	}

	public boolean isShip() {
		return SHIPS.contains(this);
	}

	public ESoldierType getSoldierType() {
		if (isBowman()) {
			return ESoldierType.BOWMAN;
		} else if (isPikeman()) {
			return ESoldierType.PIKEMAN;
		} else if (isSwordsman()) {
			return ESoldierType.SWORDSMAN;
		} else {
			return null;
		}
	}

	public ESoldierClass getSoldierClass() {
		if (isBowman()) {
			return ESoldierClass.BOWMAN;
		} else if (isInfantry()) {
			return ESoldierClass.INFANTRY;
		} else {
			return null;
		}
	}
}
