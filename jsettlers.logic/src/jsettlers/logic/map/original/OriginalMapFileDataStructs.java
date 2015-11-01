package jsettlers.logic.map.original;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.mapobject.EMapObjectType;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileDataStructs 
{
	
	public enum EMapFilePartType {
		EOF (0,""), // End of File and Padding
		MAP_INFO(1,"Map Info"),
		PLAYER_INFO(2,"Player Info"),
		TEAM_INFO(3,"Team Info"),
		PREVIEW(4, "Preview"),
		UNKNOWN_5 (5,"UNKNOWN_5"),
		AREA(6,"Area"),
		SETTLERS(7,"Settlers"),
		BUILDINGS(8,"Buildings"),
		RESOURCES(9,"Resources"),
		UNKNOWN_10 (10,"UNKNOWN_10"),
		QUEST_TEXT(11,"QuestText"),
		QUEST_TIP(12,"QuestTip");
		
		//- length of [MapFilePartsTypes]
		public static final int length = 13;

		public final int value;
		private final String typeText;
	
		EMapFilePartType(int typeValue, String typeText) {
			this.value = typeValue;
			this.typeText = typeText;
		}
		
		public String toString() {
			return typeText;
		}
		
		public static EMapFilePartType getTypeByInt(int intType) {
			int val = intType & 0x0000FFFF;
			if (val <= 0) return EOF;
			if (val >= length) return EOF;
			
			return EMapFilePartType.values()[val];
		}
		
	}

	//--------------------------------------------------//
	public enum EMapNations {
		ROMANS(0),
		EGYPTIANS(1),
		ASIANS(2),
		AMAZONS(3),
		FREE_CHOICE(255),
		NOT_DEFINED(256);
	
		public final int value;

		EMapNations(int value) {
			this.value = value;
		}
		
		public static EMapNations FromMapValue(int mapValue) {
			for (int i=0; i < EMapNations.values().length; i++) {
				if (EMapNations.values()[i].value == mapValue) return EMapNations.values()[i];
			}
			
			System.err.println("wrong value for 'EMapNations' "+ Integer.toString(mapValue) +"!");
		
			return EMapNations.ROMANS;
		}
	}
	
	//--------------------------------------------------//
	public enum EMapStartResources {
		SMALL(1),
		MEDIUM(2),
		MUCH(3);
	
		public final int value;
		
		EMapStartResources(int value) {
			this.value = value;
		}
		
		public static EMapStartResources FromMapValue(int mapValue)
		{
			for (int i=0; i< EMapStartResources.values().length; i++)
			{
				if (EMapStartResources.values()[i].value == mapValue) return EMapStartResources.values()[i];
			}
			
			System.err.println("wrong value for 'EMapStartResources' "+ Integer.toString(mapValue) +"!");
			
			return EMapStartResources.SMALL;
		}
	}

	//--------------------------------------------------//
	public enum EMapFileVersion {
		NO_S3_FILE (0x00),
		DEFAULT (0x0A),
		AMAZONS (0x0B);
		
		public final int value;
		
		EMapFileVersion(int value) {
			this.value = value;
		}
	};

	//--------------------------------------------------//
	public enum EOriginalLandscapeType {
		UNKNOWN_00(ELandscapeType.WATER1),
		UNKNOWN_01(ELandscapeType.WATER2),
		UNKNOWN_02(ELandscapeType.WATER3),
		UNKNOWN_03(ELandscapeType.WATER4),
		UNKNOWN_04(ELandscapeType.WATER5),
		UNKNOWN_05(ELandscapeType.WATER6),
		UNKNOWN_06(ELandscapeType.WATER7),
		UNKNOWN_07(ELandscapeType.WATER8),
		UNKNOWN_08(null),
		UNKNOWN_09(null),
		UNKNOWN_0A(null),
		UNKNOWN_0B(null),
		UNKNOWN_0C(null),
		UNKNOWN_0D(null),
		UNKNOWN_0E(null),
		UNKNOWN_0F(null),
		UNKNOWN_10(ELandscapeType.GRASS),
		UNKNOWN_11(ELandscapeType.MOUNTAINBORDEROUTER),
		UNKNOWN_12(null),
		UNKNOWN_13(null),
		UNKNOWN_14(ELandscapeType.DESERTBORDEROUTER),
		UNKNOWN_15(ELandscapeType.MOORBORDER),
		UNKNOWN_16(null),
		UNKNOWN_17(null),
		UNKNOWN_18(null),
		UNKNOWN_19(null),
		UNKNOWN_1A(null),
		UNKNOWN_1B(null),
		UNKNOWN_1C(null),
		UNKNOWN_1D(null),
		UNKNOWN_1E(null),
		UNKNOWN_1F(null),
		UNKNOWN_20(ELandscapeType.MOUNTAIN),
		UNKNOWN_21(ELandscapeType.MOUNTAINBORDER),
		UNKNOWN_22(null),
		UNKNOWN_23(null),
		UNKNOWN_24(null),
		UNKNOWN_25(null),
		UNKNOWN_26(null),
		UNKNOWN_27(null),
		UNKNOWN_28(null),
		UNKNOWN_29(null),
		UNKNOWN_2A(null),
		UNKNOWN_2B(null),
		UNKNOWN_2C(null),
		UNKNOWN_2D(null),
		UNKNOWN_2E(null),
		UNKNOWN_2F(null),
		UNKNOWN_30(ELandscapeType.SAND),
		UNKNOWN_31(null),
		UNKNOWN_32(null),
		UNKNOWN_33(null),
		UNKNOWN_34(null),
		UNKNOWN_35(null),
		UNKNOWN_36(null),
		UNKNOWN_37(null),
		UNKNOWN_38(null),
		UNKNOWN_39(null),
		UNKNOWN_3A(null),
		UNKNOWN_3B(null),
		UNKNOWN_3C(null),
		UNKNOWN_3D(null),
		UNKNOWN_3E(null),
		UNKNOWN_3F(null),
		UNKNOWN_40(ELandscapeType.DESERT),
		UNKNOWN_41(ELandscapeType.DESERTBORDER),
		UNKNOWN_42(null),
		UNKNOWN_43(null),
		UNKNOWN_44(null),
		UNKNOWN_45(null),
		UNKNOWN_46(null),
		UNKNOWN_47(null),
		UNKNOWN_48(null),
		UNKNOWN_49(null),
		UNKNOWN_4A(null),
		UNKNOWN_4B(null),
		UNKNOWN_4C(null),
		UNKNOWN_4D(null),
		UNKNOWN_4E(null),
		UNKNOWN_4F(null),
		UNKNOWN_50(ELandscapeType.MOORINNER), //- swamp ??
		UNKNOWN_51(ELandscapeType.MOOR),
		UNKNOWN_52(null),
		UNKNOWN_53(null),
		UNKNOWN_54(null),
		UNKNOWN_55(null),
		UNKNOWN_56(null),
		UNKNOWN_57(null),
		UNKNOWN_58(null),
		UNKNOWN_59(null),
		UNKNOWN_5A(null),
		UNKNOWN_5B(null),
		UNKNOWN_5C(null),
		UNKNOWN_5D(null),
		UNKNOWN_5E(null),
		UNKNOWN_5F(null),
		UNKNOWN_60(ELandscapeType.RIVER1),
		UNKNOWN_61(ELandscapeType.RIVER2),
		UNKNOWN_62(ELandscapeType.RIVER3),
		UNKNOWN_63(ELandscapeType.RIVER4),
		UNKNOWN_64(null),
		UNKNOWN_65(null),
		UNKNOWN_66(null),
		UNKNOWN_67(null),
		UNKNOWN_68(null),
		UNKNOWN_69(null),
		UNKNOWN_6A(null),
		UNKNOWN_6B(null),
		UNKNOWN_6C(null),
		UNKNOWN_6D(null),
		UNKNOWN_6E(null),
		UNKNOWN_6F(null),
		UNKNOWN_70(null),
		UNKNOWN_71(null),
		UNKNOWN_72(null),
		UNKNOWN_73(null),
		UNKNOWN_74(null),
		UNKNOWN_75(null),
		UNKNOWN_76(null),
		UNKNOWN_77(null),
		UNKNOWN_78(null),
		UNKNOWN_79(null),
		UNKNOWN_7A(null),
		UNKNOWN_7B(null),
		UNKNOWN_7C(null),
		UNKNOWN_7D(null),
		UNKNOWN_7E(null),
		UNKNOWN_7F(null),
		UNKNOWN_80(ELandscapeType.SNOW),
		UNKNOWN_81(null),
		UNKNOWN_82(null),
		UNKNOWN_83(null),
		UNKNOWN_84(null),
		UNKNOWN_85(null),
		UNKNOWN_86(null),
		UNKNOWN_87(null),
		UNKNOWN_88(null),
		UNKNOWN_89(null),
		UNKNOWN_8A(null),
		UNKNOWN_8B(null),
		UNKNOWN_8C(null),
		UNKNOWN_8D(null),
		UNKNOWN_8E(null),
		UNKNOWN_8F(null),
		UNKNOWN_90(ELandscapeType.MOOR), //- mud ??
		UNKNOWN_91(null),
		UNKNOWN_92(null),
		UNKNOWN_93(null),
		UNKNOWN_94(null),
		UNKNOWN_95(null),
		UNKNOWN_96(null),
		UNKNOWN_97(null),
		UNKNOWN_98(null),
		UNKNOWN_99(null),
		UNKNOWN_9A(null),
		UNKNOWN_9B(null),
		UNKNOWN_9C(null),
		UNKNOWN_9D(null),
		UNKNOWN_9E(null),
		UNKNOWN_9F(null),
		UNKNOWN_A0(null),
		UNKNOWN_A1(null),
		UNKNOWN_A2(null),
		UNKNOWN_A3(null),
		UNKNOWN_A4(null),
		UNKNOWN_A5(null),
		UNKNOWN_A6(null),
		UNKNOWN_A7(null),
		UNKNOWN_A8(null),
		UNKNOWN_A9(null),
		UNKNOWN_AA(null),
		UNKNOWN_AB(null),
		UNKNOWN_AC(null),
		UNKNOWN_AD(null),
		UNKNOWN_AE(null),
		UNKNOWN_AF(null),
		UNKNOWN_B0(null),
		UNKNOWN_B1(null),
		UNKNOWN_B2(null),
		UNKNOWN_B3(null),
		UNKNOWN_B4(null),
		UNKNOWN_B5(null),
		UNKNOWN_B6(null),
		UNKNOWN_B7(null),
		UNKNOWN_B8(null),
		UNKNOWN_B9(null),
		UNKNOWN_BA(null),
		UNKNOWN_BB(null),
		UNKNOWN_BC(null),
		UNKNOWN_BD(null),
		UNKNOWN_BE(null),
		UNKNOWN_BF(null),
		UNKNOWN_C0(null),
		UNKNOWN_C1(null),
		UNKNOWN_C2(null),
		UNKNOWN_C3(null),
		UNKNOWN_C4(null),
		UNKNOWN_C5(null),
		UNKNOWN_C6(null),
		UNKNOWN_C7(null),
		UNKNOWN_C8(null),
		UNKNOWN_C9(null),
		UNKNOWN_CA(null),
		UNKNOWN_CB(null),
		UNKNOWN_CC(null),
		UNKNOWN_CD(null),
		UNKNOWN_CE(null),
		UNKNOWN_CF(null),
		UNKNOWN_D0(null),
		UNKNOWN_D1(null),
		UNKNOWN_D2(null),
		UNKNOWN_D3(null),
		UNKNOWN_D4(null),
		UNKNOWN_D5(null),
		UNKNOWN_D6(null),
		UNKNOWN_D7(null),
		UNKNOWN_D8(null),
		UNKNOWN_D9(null),
		UNKNOWN_DA(null),
		UNKNOWN_DB(null),
		UNKNOWN_DC(null),
		UNKNOWN_DD(null),
		UNKNOWN_DE(null),
		UNKNOWN_DF(null),
		UNKNOWN_E0(null),
		UNKNOWN_E1(null),
		UNKNOWN_E2(null),
		UNKNOWN_E3(null),
		UNKNOWN_E4(null),
		UNKNOWN_E5(null),
		UNKNOWN_E6(null),
		UNKNOWN_E7(null),
		UNKNOWN_E8(null),
		UNKNOWN_E9(null),
		UNKNOWN_EA(null),
		UNKNOWN_EB(null),
		UNKNOWN_EC(null),
		UNKNOWN_ED(null),
		UNKNOWN_EE(null),
		UNKNOWN_EF(null),
		UNKNOWN_F0(null),
		UNKNOWN_F1(null),
		UNKNOWN_F2(null),
		UNKNOWN_F3(null),
		UNKNOWN_F4(null),
		UNKNOWN_F5(null),
		UNKNOWN_F6(null),
		UNKNOWN_F7(null),
		UNKNOWN_F8(null),
		UNKNOWN_F9(null),
		UNKNOWN_FA(null),
		UNKNOWN_FB(null),
		UNKNOWN_FC(null),
		UNKNOWN_FD(null),
		UNKNOWN_FE(null),
		
		NOT_A_TYPE(null); //- has to be the last item
		
		//- length of [EOriginalLandscapeType]
		public final ELandscapeType value;
		
		EOriginalLandscapeType(ELandscapeType value) {
			this.value = value;
		}

		public static EOriginalLandscapeType getTypeByInt(byte intType) {
			if (intType < 0) return NOT_A_TYPE;
			if (intType >= EOriginalLandscapeType.values().length) return NOT_A_TYPE;
			
			return EOriginalLandscapeType.values()[intType];
		}
	}

	
	//--------------------------------------------------//
	public enum EObjectType {
		NO_OBJECT(null),  //- 0
		UNKNOWN_01(null),  //- GAME_OBJECT_BIG_STONE_1 = 1,
		UNKNOWN_02(null),  //- GAME_OBJECT_BIG_STONE_2 = 2,
		UNKNOWN_03(null),  //- GAME_OBJECT_BIG_STONE_3 = 3,
		UNKNOWN_04(null),  //- GAME_OBJECT_BIG_STONE_4 = 4,
		UNKNOWN_05(null),  //- GAME_OBJECT_BIG_STONE_5 = 5,
		UNKNOWN_06(null),  //- GAME_OBJECT_BIG_STONE_6 = 6,
		UNKNOWN_07(null),  //- GAME_OBJECT_BIG_STONE_7 = 7,
		UNKNOWN_08(null),  //- GAME_OBJECT_BIG_STONE_8 = 8,
		UNKNOWN_09(null),  //- GAME_OBJECT_STONE_1 = 9,
		UNKNOWN_0A(null),  //- GAME_OBJECT_STONE_2 = 10,
		UNKNOWN_0B(null),  //- GAME_OBJECT_STONE_3 = 11,
		UNKNOWN_0C(null),  //- GAME_OBJECT_STONE_4 = 12,
		UNKNOWN_0D(null),  //- GAME_OBJECT_BOUNDERY_STONE_1 = 13,
		UNKNOWN_0E(null),  //- GAME_OBJECT_BOUNDERY_STONE_2 = 14,
		UNKNOWN_0F(null),  //- GAME_OBJECT_BOUNDERY_STONE_3 = 15,
		UNKNOWN_10(null),  //- GAME_OBJECT_BOUNDERY_STONE_4 = 16,
		UNKNOWN_11(null),  //- GAME_OBJECT_BOUNDERY_STONE_5 = 17,
		UNKNOWN_12(null),  //- GAME_OBJECT_BOUNDERY_STONE_6 = 18,
		UNKNOWN_13(null),  //- GAME_OBJECT_BOUNDERY_STONE_7 = 19,
		UNKNOWN_14(null),  //- GAME_OBJECT_BOUNDERY_STONE_8 = 20,
		UNKNOWN_15(null),  //- GAME_OBJECT_SMALL_STONE_1 = 21,
		UNKNOWN_16(null),  //- GAME_OBJECT_SMALL_STONE_2 = 22,
		UNKNOWN_17(null),  //- GAME_OBJECT_SMALL_STONE_3 = 23,
		UNKNOWN_18(null),  //- GAME_OBJECT_SMALL_STONE_4 = 24,
		UNKNOWN_19(null),  //- GAME_OBJECT_SMALL_STONE_5 = 25,
		UNKNOWN_1A(null),  //- GAME_OBJECT_SMALL_STONE_6 = 26,
		UNKNOWN_1B(null),  //- GAME_OBJECT_SMALL_STONE_7 = 27,
		UNKNOWN_1C(null),  //- GAME_OBJECT_SMALL_STONE_8 = 28,
		UNKNOWN_1D(null),  //- GAME_OBJECT_WRECK_1 = 29,
		UNKNOWN_1E(null),  //- GAME_OBJECT_WRECK_2 = 30,
		UNKNOWN_1F(null),  //- GAME_OBJECT_WRECK_3 = 31,
		UNKNOWN_20(null),  //- GAME_OBJECT_WRECK_4 = 32,
		UNKNOWN_21(null),  //- GAME_OBJECT_WRECK_5 = 33,
		UNKNOWN_22(null),  //- GAME_OBJECT_GRAVE = 34,
		UNKNOWN_23(null),  //- GAME_OBJECT_PLANT_SMALL_1 = 35,
		UNKNOWN_24(null),  //- GAME_OBJECT_PLANT_SMALL_2 = 36,
		UNKNOWN_25(null),  //- GAME_OBJECT_PLANT_SMALL_3 = 37,
		UNKNOWN_26(null),  //- GAME_OBJECT_MUSHROOM_1 = 38,
		UNKNOWN_27(null),  //- GAME_OBJECT_MUSHROOM_2 = 39,
		UNKNOWN_28(null),  //- GAME_OBJECT_MUSHROOM_3 = 40,
		UNKNOWN_29(null),  //- GAME_OBJECT_TREE_STUMP_1 = 41,
		UNKNOWN_2A(null),  //- GAME_OBJECT_TREE_STUMP_2 = 42,
		UNKNOWN_2B(null),  //- GAME_OBJECT_TREE_DEAD_1 = 43,
		UNKNOWN_2C(null),  //- GAME_OBJECT_TREE_DEAD_2 = 44,
		UNKNOWN_2D(null),  //- GAME_OBJECT_CACTUS_1 = 45,
		UNKNOWN_2E(null),  //- GAME_OBJECT_CACTUS_2 = 46,
		UNKNOWN_2F(null),  //- GAME_OBJECT_CACTUS_3 = 47,
		UNKNOWN_30(null),  //- GAME_OBJECT_CACTUS_4 = 48,
		UNKNOWN_31(null),  //- GAME_OBJECT_BONES = 49,
		UNKNOWN_32(null),  //- GAME_OBJECT_FLOWER_1 = 50,
		UNKNOWN_33(null),  //- GAME_OBJECT_FLOWER_2 = 51,
		UNKNOWN_34(null),  //- GAME_OBJECT_FLOWER_3 = 52,
		UNKNOWN_35(null),  //- GAME_OBJECT_STRUB_SMALL_1 = 53,
		UNKNOWN_36(null),  //- GAME_OBJECT_STRUB_SMALL_2 = 54,
		UNKNOWN_37(null),  //- GAME_OBJECT_STRUB_SMALL_3 = 55,
		UNKNOWN_38(null),  //- GAME_OBJECT_STRUB_SMALL_4 = 56,
		UNKNOWN_39(null),  //- GAME_OBJECT_STRUB_1 = 57,
		UNKNOWN_3A(null),  //- GAME_OBJECT_STRUB_2 = 58,
		UNKNOWN_3B(null),  //- GAME_OBJECT_STRUB_3 = 59,
		UNKNOWN_3C(null),  //- GAME_OBJECT_STRUB_4 = 60,
		UNKNOWN_3D(null),  //- GAME_OBJECT_STRUB_5 = 61,
		UNKNOWN_3E(null),  //- GAME_OBJECT_REED_BEDS_1 = 62,
		UNKNOWN_3F(null),  //- GAME_OBJECT_REED_BEDS_2 = 63,
		UNKNOWN_40(null),  //- GAME_OBJECT_REED_BEDS_3 = 64,
		UNKNOWN_41(null),  //- GAME_OBJECT_REED_BEDS_4 = 65,
		UNKNOWN_42(null),  //- GAME_OBJECT_REED_BEDS_5 = 66,
		UNKNOWN_43(null),  //- GAME_OBJECT_REED_BEDS_6 = 67,
		UNKNOWN_44(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_BIRCH_1 = 68,
		UNKNOWN_45(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_BIRCH_2 = 69,
		UNKNOWN_46(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_ELM_1 = 70,
		UNKNOWN_47(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_ELM_2 = 71,
		UNKNOWN_48(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_OAK_1 = 72,
		UNKNOWN_49(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_UNKNOWN_1 = 73,
		UNKNOWN_4A(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_UNKNOWN_2 = 74,
		UNKNOWN_4B(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_UNKNOWN_3 = 75,
		UNKNOWN_4C(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_UNKNOWN_4 = 76,
		UNKNOWN_4D(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- //-- unknown: 77
		UNKNOWN_4E(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_ARECACEAE_1 = 78,
		UNKNOWN_4F(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_ARECACEAE_2 = 79,
		UNKNOWN_50(new MapDecorationObject(EMapObjectType.TREE_ADULT)),  //- GAME_OBJECT_TREE_UNKNOWN_5 = 80,
		UNKNOWN_51(null),  //- //-- unknown: 81
		UNKNOWN_52(null),  //- //-- unknown: 82
		UNKNOWN_53(null),  //- //-- unknown: 83
		UNKNOWN_54(null),  //- GAME_OBJECT_TREE_SMALL = 84,
		UNKNOWN_55(null),  //- //-- unknown...
		UNKNOWN_56(null),  //- //-- unknown...
		UNKNOWN_57(null),  //- //-- unknown...
		UNKNOWN_58(null),  //- //-- unknown...
		UNKNOWN_59(null),  //- //-- unknown...
		UNKNOWN_5A(null),  //- //-- unknown...
		UNKNOWN_5B(null),  //- //-- unknown...
		UNKNOWN_5C(null),  //- //-- unknown...
		UNKNOWN_5D(null),  //- //-- unknown...
		UNKNOWN_5E(null),  //- //-- unknown...
		UNKNOWN_5F(null),  //- //-- unknown...
		UNKNOWN_60(null),  //- //-- unknown...
		UNKNOWN_61(null),  //- //-- unknown...
		UNKNOWN_62(null),  //- //-- unknown...
		UNKNOWN_63(null),  //- //-- unknown...
		UNKNOWN_64(null),  //- //-- unknown...
		UNKNOWN_65(null),  //- //-- unknown...
		UNKNOWN_66(null),  //- //-- unknown...
		UNKNOWN_67(null),  //- //-- unknown...
		UNKNOWN_68(null),  //- //-- unknown...
		UNKNOWN_69(null),  //- //-- unknown...
		UNKNOWN_6A(null),  //- //-- unknown...
		UNKNOWN_6B(null),  //- //-- unknown...
		UNKNOWN_6C(null),  //- //-- unknown...
		UNKNOWN_6D(null),  //- //-- unknown...
		UNKNOWN_6E(null),  //- //-- unknown...
		UNKNOWN_6F(null),  //- GAME_OBJECT_REEF_SMALL = 111,
		UNKNOWN_70(null),  //- GAME_OBJECT_REEF_MEDIUM = 112,
		UNKNOWN_71(null),  //- GAME_OBJECT_REEF_LARGE = 113,
		UNKNOWN_72(null),  //- GAME_OBJECT_REEF_XLARGE = 114,
		UNKNOWN_73(null),  //- GAME_OBJECT_RES_STONE_01 = 115,
		UNKNOWN_74(null),  //- GAME_OBJECT_RES_STONE_02 = 116,
		UNKNOWN_75(null),  //- GAME_OBJECT_RES_STONE_03 = 117,
		UNKNOWN_76(null),  //- GAME_OBJECT_RES_STONE_04 = 118,
		UNKNOWN_77(null),  //- GAME_OBJECT_RES_STONE_05 = 119,
		UNKNOWN_78(null),  //- GAME_OBJECT_RES_STONE_06 = 120,
		UNKNOWN_79(null),  //- GAME_OBJECT_RES_STONE_07 = 121,
		UNKNOWN_7A(null),  //- GAME_OBJECT_RES_STONE_08 = 122,
		UNKNOWN_7B(null),  //- GAME_OBJECT_RES_STONE_09 = 123,
		UNKNOWN_7C(null),  //- GAME_OBJECT_RES_STONE_10 = 124,
		UNKNOWN_7D(null),  //- GAME_OBJECT_RES_STONE_11 = 125,
		UNKNOWN_7E(null),  //- GAME_OBJECT_RES_STONE_12 = 126,
		UNKNOWN_7F(null),  //- GAME_OBJECT_RES_STONE_13 = 127,

		NOT_A_TYPE(null); //- has to be the last item
		
		//- length of [EObjectType]
		public static final int length = 13;
		public final MapObject value;
		
		EObjectType(MapObject value) {
			this.value = value;
		}
		
		public static EObjectType getTypeByInt(byte intType) {
			if (intType <= 0) return NOT_A_TYPE;
			if (intType >= length) return NOT_A_TYPE;
			
			return EObjectType.values()[intType];
		}
	}

}