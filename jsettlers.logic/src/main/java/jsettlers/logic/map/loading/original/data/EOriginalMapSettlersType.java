/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.loading.original.data;

import jsettlers.common.movable.EMovableType;

/**
 * The settlers on the map.
 * 
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EOriginalMapSettlersType {

	BEARER(EMovableType.BEARER),
	DIGGER(EMovableType.DIGGER),
	BRICKLAYER(EMovableType.BRICKLAYER),
	LUMBERJACK(EMovableType.LUMBERJACK),
	STONECUTTER(EMovableType.STONECUTTER),
	SWORDSMAN_L1(EMovableType.SWORDSMAN_L1),
	SAWMILLER(EMovableType.SAWMILLER),
	FORESTER(EMovableType.FORESTER),
	BOWMAN_L1(EMovableType.BOWMAN_L1),
	MELTER(EMovableType.MELTER),
	MINER(EMovableType.MINER),
	SMITH(EMovableType.SMITH),
	MILLER(EMovableType.MILLER),
	BAKER(EMovableType.BAKER),
	BUTCHER(EMovableType.SLAUGHTERER),
	PIKEMAN_L1(EMovableType.PIKEMAN_L1),
	FARMER(EMovableType.FARMER),
	FISHERMAN(EMovableType.FISHERMAN),
	WATERWORKER(EMovableType.WATERWORKER),
	Werftarbeiter(null),
	UNKNOWN_20(null),
	HEALER(EMovableType.HEALER),
	GEOLOGIST(EMovableType.GEOLOGIST),
	THIEF(EMovableType.THIEF),
	CHARCOAL_BURNER(EMovableType.CHARCOAL_BURNER),
	Schnapsbrenner(null),
	Brauer(null),
	Pulvermacher(null),
	PIG_FARMER(EMovableType.PIG_FARMER),
	WINEGROWER(EMovableType.WINEGROWER),
	BOWMAN_L2(EMovableType.BOWMAN_L2),
	PIKEMAN_L2(EMovableType.PIKEMAN_L2),
	SWORDSMAN_L2(EMovableType.SWORDSMAN_L2),
	BOWMAN_L3(EMovableType.BOWMAN_L3),
	PIKEMAN_L3(EMovableType.PIKEMAN_L3),
	SWORDSMAN_L3(EMovableType.SWORDSMAN_L3),
	MAGE(EMovableType.MAGE),
	UNKNOWN_37(null),
	Reisbauer(null),
	DONKEY(EMovableType.DONKEY),
	PIONEER(EMovableType.PIONEER),
	Katapult(null),
	UNKNOWN_42(null),
	Handelsschiff(null),
	Faehre(null),
	Imker(null),
	Metwinzer(null),
	Alchemist(null),

	NOT_A_SETTLER(null);

	public static final EOriginalMapSettlersType[] VALUES = EOriginalMapSettlersType.values();
	public final EMovableType value;

	EOriginalMapSettlersType(EMovableType value) {
		this.value = value;
	}

	public static EOriginalMapSettlersType getTypeByInt(int type) {
		if (type < 0 || type >= VALUES.length) {
			return NOT_A_SETTLER;
		} else {
			return VALUES[type];
		}
	}
}