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

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.loading.data.objects.DecorationMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;

/**
 * The map objects on the map, see {@link EOriginalMapObjectClass}
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EOriginalMapObjectType {

	NO_OBJECT(null, 0), // - 0

	// TODO: EOriginalMapObjectClass.DECORATION does not work!
	UNKNOWN_01(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_1 = 1,
	UNKNOWN_02(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_2 = 2,
	UNKNOWN_03(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_3 = 3,
	UNKNOWN_04(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_4 = 4,
	UNKNOWN_05(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_5 = 5,
	UNKNOWN_06(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_6 = 6,
	UNKNOWN_07(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_7 = 7,
	UNKNOWN_08(EOriginalMapObjectClass.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_8 = 8,
	UNKNOWN_09(null, 0), // - GAME_OBJECT_STONE_1 = 9,
	UNKNOWN_0A(null, 0), // - GAME_OBJECT_STONE_2 = 10,
	UNKNOWN_0B(null, 0), // - GAME_OBJECT_STONE_3 = 11,
	UNKNOWN_0C(null, 0), // - GAME_OBJECT_STONE_4 = 12,
	UNKNOWN_0D(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_1 = 13,
	UNKNOWN_0E(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_2 = 14,
	UNKNOWN_0F(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_3 = 15,
	UNKNOWN_10(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_4 = 16,
	UNKNOWN_11(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_5 = 17,
	UNKNOWN_12(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_6 = 18,
	UNKNOWN_13(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_7 = 19,
	UNKNOWN_14(null, 0), // - GAME_OBJECT_BOUNDERY_STONE_8 = 20,
	UNKNOWN_15(null, 0), // - GAME_OBJECT_SMALL_STONE_1 = 21,
	UNKNOWN_16(null, 0), // - GAME_OBJECT_SMALL_STONE_2 = 22,
	UNKNOWN_17(null, 0), // - GAME_OBJECT_SMALL_STONE_3 = 23,
	UNKNOWN_18(null, 0), // - GAME_OBJECT_SMALL_STONE_4 = 24,
	UNKNOWN_19(null, 0), // - GAME_OBJECT_SMALL_STONE_5 = 25,
	UNKNOWN_1A(null, 0), // - GAME_OBJECT_SMALL_STONE_6 = 26,
	UNKNOWN_1B(null, 0), // - GAME_OBJECT_SMALL_STONE_7 = 27,
	UNKNOWN_1C(null, 0), // - GAME_OBJECT_SMALL_STONE_8 = 28,
	UNKNOWN_1D(null, 0), // - GAME_OBJECT_WRECK_1 = 29,
	UNKNOWN_1E(null, 0), // - GAME_OBJECT_WRECK_2 = 30,
	UNKNOWN_1F(null, 0), // - GAME_OBJECT_WRECK_3 = 31,
	UNKNOWN_20(null, 0), // - GAME_OBJECT_WRECK_4 = 32,
	UNKNOWN_21(null, 0), // - GAME_OBJECT_WRECK_5 = 33,
	UNKNOWN_22(null, 0), // - GAME_OBJECT_GRAVE = 34,
	UNKNOWN_23(null, 0), // - GAME_OBJECT_PLANT_SMALL_1 = 35,
	UNKNOWN_24(null, 0), // - GAME_OBJECT_PLANT_SMALL_2 = 36,
	UNKNOWN_25(null, 0), // - GAME_OBJECT_PLANT_SMALL_3 = 37,
	UNKNOWN_26(null, 0), // - GAME_OBJECT_MUSHROOM_1 = 38,
	UNKNOWN_27(null, 0), // - GAME_OBJECT_MUSHROOM_2 = 39,
	UNKNOWN_28(null, 0), // - GAME_OBJECT_MUSHROOM_3 = 40,
	UNKNOWN_29(null, 0), // - GAME_OBJECT_TREE_STUMP_1 = 41,
	UNKNOWN_2A(null, 0), // - GAME_OBJECT_TREE_STUMP_2 = 42,
	UNKNOWN_2B(null, 0), // - GAME_OBJECT_TREE_DEAD_1 = 43,
	UNKNOWN_2C(null, 0), // - GAME_OBJECT_TREE_DEAD_2 = 44,
	UNKNOWN_2D(null, 0), // - GAME_OBJECT_CACTUS_1 = 45,
	UNKNOWN_2E(null, 0), // - GAME_OBJECT_CACTUS_2 = 46,
	UNKNOWN_2F(null, 0), // - GAME_OBJECT_CACTUS_3 = 47,
	UNKNOWN_30(null, 0), // - GAME_OBJECT_CACTUS_4 = 48,
	UNKNOWN_31(null, 0), // - GAME_OBJECT_BONES = 49,
	UNKNOWN_32(null, 0), // - GAME_OBJECT_FLOWER_1 = 50,
	UNKNOWN_33(null, 0), // - GAME_OBJECT_FLOWER_2 = 51,
	UNKNOWN_34(null, 0), // - GAME_OBJECT_FLOWER_3 = 52,
	UNKNOWN_35(null, 0), // - GAME_OBJECT_STRUB_SMALL_1 = 53,
	UNKNOWN_36(null, 0), // - GAME_OBJECT_STRUB_SMALL_2 = 54,
	UNKNOWN_37(null, 0), // - GAME_OBJECT_STRUB_SMALL_3 = 55,
	UNKNOWN_38(null, 0), // - GAME_OBJECT_STRUB_SMALL_4 = 56,
	UNKNOWN_39(null, 0), // - GAME_OBJECT_STRUB_1 = 57,
	UNKNOWN_3A(null, 0), // - GAME_OBJECT_STRUB_2 = 58,
	UNKNOWN_3B(null, 0), // - GAME_OBJECT_STRUB_3 = 59,
	UNKNOWN_3C(null, 0), // - GAME_OBJECT_STRUB_4 = 60,
	UNKNOWN_3D(null, 0), // - GAME_OBJECT_STRUB_5 = 61,
	UNKNOWN_3E(null, 0), // - GAME_OBJECT_REED_BEDS_1 = 62,
	UNKNOWN_3F(null, 0), // - GAME_OBJECT_REED_BEDS_2 = 63,
	UNKNOWN_40(null, 0), // - GAME_OBJECT_REED_BEDS_3 = 64,
	UNKNOWN_41(null, 0), // - GAME_OBJECT_REED_BEDS_4 = 65,
	UNKNOWN_42(null, 0), // - GAME_OBJECT_REED_BEDS_5 = 66,
	UNKNOWN_43(null, 0), // - GAME_OBJEC()T_REED_BEDS_6 = 67,
	TREE_BIRCH_1(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_BIRCH_1 = 68,
	TREE_BIRCH_2(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_BIRCH_2 = 69,
	TREE_ELM_1(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_ELM_1 = 70,
	TREE_ELM_2(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_ELM_2 = 71,
	TREE_OAK_1(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_OAK_1 = 72,
	TREE_UNKNOWN_1(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_1 = 73,
	TREE_UNKNOWN_2(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_2 = 74,
	TREE_UNKNOWN_3(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_3 = 75,
	TREE_UNKNOWN_4(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_4 = 76,
	TREE_UNKNOWN_5(EOriginalMapObjectClass.TREE, 0), // - //-- unknown: 77
	TREE_ARECACEAE_1(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_ARECACEAE_1 = 78,
	TREE_ARECACEAE_2(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_ARECACEAE_2 = 79,
	TREE_UNKNOWN_6(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_6 = 80,
	UNKNOWN_51(null, 0), // - //-- unknown: 81
	UNKNOWN_52(null, 0), // - //-- unknown: 82
	UNKNOWN_53(null, 0), // - //-- unknown: 83
	UNKNOWN_54(EOriginalMapObjectClass.TREE, 0), // - GAME_OBJECT_TREE_SMALL = 84,
	UNKNOWN_55(null, 0), // - //-- unknown...
	UNKNOWN_56(null, 0), // - //-- unknown...
	UNKNOWN_57(null, 0), // - //-- unknown...
	UNKNOWN_58(null, 0), // - //-- unknown...
	UNKNOWN_59(null, 0), // - //-- unknown...
	UNKNOWN_5A(null, 0), // - //-- unknown...
	UNKNOWN_5B(null, 0), // - //-- unknown...
	UNKNOWN_5C(null, 0), // - //-- unknown...
	UNKNOWN_5D(null, 0), // - //-- unknown...
	UNKNOWN_5E(null, 0), // - //-- unknown...
	UNKNOWN_5F(null, 0), // - //-- unknown...
	UNKNOWN_60(null, 0), // - //-- unknown...
	UNKNOWN_61(null, 0), // - //-- unknown...
	UNKNOWN_62(null, 0), // - //-- unknown...
	UNKNOWN_63(null, 0), // - //-- unknown...
	UNKNOWN_64(null, 0), // - //-- unknown...
	UNKNOWN_65(null, 0), // - //-- unknown...
	UNKNOWN_66(null, 0), // - //-- unknown...
	UNKNOWN_67(null, 0), // - //-- unknown...
	UNKNOWN_68(null, 0), // - //-- unknown...
	UNKNOWN_69(null, 0), // - //-- unknown...
	UNKNOWN_6A(null, 0), // - //-- unknown...
	UNKNOWN_6B(null, 0), // - //-- unknown...
	UNKNOWN_6C(null, 0), // - //-- unknown...
	UNKNOWN_6D(null, 0), // - //-- unknown...
	UNKNOWN_6E(null, 0), // - //-- unknown...
	UNKNOWN_6F(null, 0), // - GAME_OBJECT_REEF_SMALL = 111,
	UNKNOWN_70(null, 0), // - GAME_OBJECT_REEF_MEDIUM = 112,
	UNKNOWN_71(null, 0), // - GAME_OBJECT_REEF_LARGE = 113,
	UNKNOWN_72(null, 0), // - GAME_OBJECT_REEF_XLARGE = 114,
	RES_STONE_01(EOriginalMapObjectClass.STONE, 12), // - GAME_OBJECT_RES_STONE_01 = 115,
	RES_STONE_02(EOriginalMapObjectClass.STONE, 11), // - GAME_OBJECT_RES_STONE_02 = 116,
	RES_STONE_03(EOriginalMapObjectClass.STONE, 10), // - GAME_OBJECT_RES_STONE_03 = 117,
	RES_STONE_04(EOriginalMapObjectClass.STONE, 9), // - GAME_OBJECT_RES_STONE_04 = 118,
	RES_STONE_05(EOriginalMapObjectClass.STONE, 8), // - GAME_OBJECT_RES_STONE_05 = 119,
	RES_STONE_06(EOriginalMapObjectClass.STONE, 7), // - GAME_OBJECT_RES_STONE_06 = 120,
	RES_STONE_07(EOriginalMapObjectClass.STONE, 6), // - GAME_OBJECT_RES_STONE_07 = 121,
	RES_STONE_08(EOriginalMapObjectClass.STONE, 5), // - GAME_OBJECT_RES_STONE_08 = 122,
	RES_STONE_09(EOriginalMapObjectClass.STONE, 4), // - GAME_OBJECT_RES_STONE_09 = 123,
	RES_STONE_10(EOriginalMapObjectClass.STONE, 3), // - GAME_OBJECT_RES_STONE_10 = 124,
	RES_STONE_11(EOriginalMapObjectClass.STONE, 2), // - GAME_OBJECT_RES_STONE_11 = 125,
	RES_STONE_12(EOriginalMapObjectClass.STONE, 1), // - GAME_OBJECT_RES_STONE_12 = 126,
	RES_STONE_13(EOriginalMapObjectClass.STONE, 0); // - GAME_OBJECT_RES_STONE_13 = 127,;
	
	private static final EOriginalMapObjectType[] VALUES = EOriginalMapObjectType.values();

	public final EOriginalMapObjectClass type;
	public final int style;

	EOriginalMapObjectType(EOriginalMapObjectClass type, int style) {
		this.type = type;
		this.style = style;
	}

	EOriginalMapObjectType(EOriginalMapObjectClass type, EMapObjectType style) {
		this.type = type;
		this.style = style.ordinal();
	}

	public static EOriginalMapObjectType getTypeByInt(int type) {
		if (type < 0 || type >= VALUES.length) {
			return NO_OBJECT;
		} else {
			return VALUES[type];
		}
	}

	public MapDataObject getNewInstance() {
		if (type == null) {
			return null;
		}

		switch (type) {
		case DECORATION:
			if (style < 0 || style >= EMapObjectType.VALUES.length) {
				return null;
			} else {
				// - TODO: does not work?!
				return new DecorationMapDataObject(EMapObjectType.VALUES[style]);
			}

		case STONE:
			return StoneMapDataObject.getInstance(style);

		case TREE:
			return MapTreeObject.getInstance();

		default:
			return null;
		}
	}

}