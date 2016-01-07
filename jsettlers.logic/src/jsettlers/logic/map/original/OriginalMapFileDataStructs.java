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
package jsettlers.logic.map.original;

import java.util.List;
import java.util.Vector;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

/**
 * @author Thomas Zeugner
 * @author codingberlin
 */
public class OriginalMapFileDataStructs {

	public enum EMapFilePartType {
		EOF(0, ""), // End of File and Padding
		MAP_INFO(1, "Map Info"),
		PLAYER_INFO(2, "Player Info"),
		TEAM_INFO(3, "Team Info"),
		PREVIEW(4, "Preview"),
		UNKNOWN_5(5, "UNKNOWN_5"),
		AREA(6, "Area"),
		SETTLERS(7, "Settlers"),
		BUILDINGS(8, "Buildings"),
		STACKS(9, "Stacks"),
		UNKNOWN_10(10, "UNKNOWN_10"), // - maybe the winning conditions
		QUEST_TEXT(11, "QuestText"),
		QUEST_TIP(12, "QuestTip");

		// - length of [MapFilePartsTypes]
		public static final int length = 13;

		public final int value;
		private final String typeText;

		EMapFilePartType(int typeValue, String typeText) {
			this.value = typeValue;
			this.typeText = typeText;
		}

		@Override
		public String toString() {
			return typeText;
		}

		public static EMapFilePartType getTypeByInt(int intType) {
			int val = intType & 0x0000FFFF;
			if (val <= 0)
				return EOF;
			if (val >= length)
				return EOF;

			return EMapFilePartType.values()[val];
		}

	}

	// --------------------------------------------------//
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

		public static EMapNations fromMapValue(int mapValue) {
			for (int i = 0; i < EMapNations.values().length; i++) {
				if (EMapNations.values()[i].value == mapValue)
					return EMapNations.values()[i];
			}

			System.err.println("wrong value for 'EMapNations' " + Integer.toString(mapValue) + "!");

			return EMapNations.ROMANS;
		}

	}

	// --------------------------------------------------//
	public enum EMapStartResources {
		LOW_GOODS(1),
		MEDIUM_GOODS(2),
		HIGH_GOODS(3);

		public final int value;

		EMapStartResources(int value) {
			this.value = value;
		}

		public static EMapStartResources fromMapValue(int mapValue) {
			for (int i = 0; i < EMapStartResources.values().length; i++) {
				if (EMapStartResources.values()[i].value == mapValue)
					return EMapStartResources.values()[i];
			}

			System.err.println("wrong value for 'EMapStartResources' " + Integer.toString(mapValue) + "!");

			return EMapStartResources.HIGH_GOODS;
		}

		public static List<MapObject> generateStackObjects(EMapStartResources mapStartResources) {
			List<MapObject> goods = new Vector<MapObject>();
			switch (mapStartResources) {
			case LOW_GOODS:
				goods.add(new StackObject(EMaterialType.PLANK, 6));
				goods.add(new StackObject(EMaterialType.PLANK, 6));
				goods.add(new StackObject(EMaterialType.STONE, 6));
				goods.add(new StackObject(EMaterialType.STONE, 6));
				goods.add(new StackObject(EMaterialType.BLADE, 5));
				goods.add(new StackObject(EMaterialType.HAMMER, 6));
				goods.add(new StackObject(EMaterialType.AXE, 3));
				goods.add(new StackObject(EMaterialType.PICK, 2));
				goods.add(new StackObject(EMaterialType.SAW, 1));
				break;
			case MEDIUM_GOODS:
				goods.add(new StackObject(EMaterialType.PLANK, 7));
				goods.add(new StackObject(EMaterialType.PLANK, 6));
				goods.add(new StackObject(EMaterialType.PLANK, 6));
				goods.add(new StackObject(EMaterialType.PLANK, 6));
				goods.add(new StackObject(EMaterialType.STONE, 7));
				goods.add(new StackObject(EMaterialType.STONE, 6));
				goods.add(new StackObject(EMaterialType.STONE, 6));
				goods.add(new StackObject(EMaterialType.STONE, 6));
				goods.add(new StackObject(EMaterialType.COAL, 8));
				goods.add(new StackObject(EMaterialType.COAL, 7));
				goods.add(new StackObject(EMaterialType.IRONORE, 5));
				goods.add(new StackObject(EMaterialType.FISH, 3));
				goods.add(new StackObject(EMaterialType.BREAD, 7));
				goods.add(new StackObject(EMaterialType.MEAT, 5));
				goods.add(new StackObject(EMaterialType.BLADE, 5));
				goods.add(new StackObject(EMaterialType.BLADE, 5));
				goods.add(new StackObject(EMaterialType.HAMMER, 8));
				goods.add(new StackObject(EMaterialType.HAMMER, 7));
				goods.add(new StackObject(EMaterialType.AXE, 6));
				goods.add(new StackObject(EMaterialType.PICK, 4));
				goods.add(new StackObject(EMaterialType.SAW, 2));
				goods.add(new StackObject(EMaterialType.SCYTHE, 1));
				goods.add(new StackObject(EMaterialType.FISHINGROD, 1));
				break;
			default:
				goods.add(new StackObject(EMaterialType.PLANK, 8));
				goods.add(new StackObject(EMaterialType.PLANK, 8));
				goods.add(new StackObject(EMaterialType.PLANK, 7));
				goods.add(new StackObject(EMaterialType.PLANK, 7));
				goods.add(new StackObject(EMaterialType.STONE, 8));
				goods.add(new StackObject(EMaterialType.STONE, 8));
				goods.add(new StackObject(EMaterialType.STONE, 8));
				goods.add(new StackObject(EMaterialType.STONE, 7));
				goods.add(new StackObject(EMaterialType.COAL, 7));
				goods.add(new StackObject(EMaterialType.COAL, 7));
				goods.add(new StackObject(EMaterialType.COAL, 6));
				goods.add(new StackObject(EMaterialType.COAL, 6));
				goods.add(new StackObject(EMaterialType.IRONORE, 6));
				goods.add(new StackObject(EMaterialType.IRONORE, 6));
				goods.add(new StackObject(EMaterialType.FISH, 7));
				goods.add(new StackObject(EMaterialType.BREAD, 8));
				goods.add(new StackObject(EMaterialType.BREAD, 7));
				goods.add(new StackObject(EMaterialType.MEAT, 8));
				goods.add(new StackObject(EMaterialType.BLADE, 8));
				goods.add(new StackObject(EMaterialType.BLADE, 7));
				goods.add(new StackObject(EMaterialType.HAMMER, 7));
				goods.add(new StackObject(EMaterialType.HAMMER, 6));
				goods.add(new StackObject(EMaterialType.HAMMER, 6));
				goods.add(new StackObject(EMaterialType.HAMMER, 6));
				goods.add(new StackObject(EMaterialType.AXE, 8));
				goods.add(new StackObject(EMaterialType.PICK, 5));
				goods.add(new StackObject(EMaterialType.SAW, 3));
				goods.add(new StackObject(EMaterialType.SCYTHE, 3));
				goods.add(new StackObject(EMaterialType.FISHINGROD, 2));
				break;
			}
			return goods;
		}

		public static List<MapObject> generateMovableObjects(EMapStartResources mapStartResources, byte playerId) {
			List<MapObject> movables = new Vector<MapObject>();
			switch (mapStartResources) {
			case LOW_GOODS:
				for (byte i = 0; i < 2; i++)
					movables.add(new MovableObject(EMovableType.MINER, playerId));
				movables.add(new MovableObject(EMovableType.SMITH, playerId));
				movables.add(new MovableObject(EMovableType.BOWMAN_L1, playerId));
				movables.add(new MovableObject(EMovableType.PIKEMAN_L1, playerId));
				for (byte i = 0; i < 6; i++)
					movables.add(new MovableObject(EMovableType.SWORDSMAN_L1, playerId));
				for (byte i = 0; i < 16; i++)
					movables.add(new MovableObject(EMovableType.BEARER, playerId));
				break;
			case MEDIUM_GOODS:
				for (byte i = 0; i < 4; i++)
					movables.add(new MovableObject(EMovableType.MINER, playerId));
				for (byte i = 0; i < 2; i++)
					movables.add(new MovableObject(EMovableType.SMITH, playerId));
				for (byte i = 0; i < 2; i++)
					movables.add(new MovableObject(EMovableType.BOWMAN_L1, playerId));
				for (byte i = 0; i < 2; i++)
					movables.add(new MovableObject(EMovableType.PIKEMAN_L1, playerId));
				for (byte i = 0; i < 10; i++)
					movables.add(new MovableObject(EMovableType.SWORDSMAN_L1, playerId));
				for (byte i = 0; i < 32; i++)
					movables.add(new MovableObject(EMovableType.BEARER, playerId));
				for (byte i = 0; i < 4; i++)
					movables.add(new MovableObject(EMovableType.DONKEY, playerId));
				break;
			default:
				for (byte i = 0; i < 6; i++)
					movables.add(new MovableObject(EMovableType.MINER, playerId));
				for (byte i = 0; i < 3; i++)
					movables.add(new MovableObject(EMovableType.SMITH, playerId));
				for (byte i = 0; i < 4; i++)
					movables.add(new MovableObject(EMovableType.BOWMAN_L1, playerId));
				for (byte i = 0; i < 4; i++)
					movables.add(new MovableObject(EMovableType.PIKEMAN_L1, playerId));
				for (byte i = 0; i < 12; i++)
					movables.add(new MovableObject(EMovableType.SWORDSMAN_L1, playerId));
				for (byte i = 0; i < 50; i++)
					movables.add(new MovableObject(EMovableType.BEARER, playerId));
				for (byte i = 0; i < 6; i++)
					movables.add(new MovableObject(EMovableType.DONKEY, playerId));
				break;
			}
			return movables;
		}
	}

	// --------------------------------------------------//
	public enum EMapFileVersion {
		NO_S3_FILE(0x00),
		DEFAULT(0x0A),
		AMAZONS(0x0B);

		public final int value;

		EMapFileVersion(int value) {
			this.value = value;
		}
	}

	// --------------------------------------------------//
	public enum EMapResources {

		FISH(EResourceType.FISH),
		COAL(EResourceType.COAL),
		IRONORE(EResourceType.IRONORE),
		GOLDORE(EResourceType.GOLDORE),
		GEMS(EResourceType.GEMSTONE),
		SULFUR(EResourceType.BRIMSTONE),
		NOT_A_RESOURCE_TYPE(EResourceType.NOTHING);

		public final EResourceType value;

		// - length of THIS enum (without NOT_A_TYPE)
		public static final int length = EMapResources.values().length - 1;

		EMapResources(EResourceType value) {
			this.value = value;
		}

		public static EMapResources getTypeByInt(int type) {
			if (type < 0)
				return NOT_A_RESOURCE_TYPE;
			if (type >= EMapResources.length)
				return NOT_A_RESOURCE_TYPE;

			return EMapResources.values()[type];
		}
	}

	// --------------------------------------------------//
	public enum EOriginalLandscapeType {
		WATER1(ELandscapeType.WATER1), // - value = 0x00
		WATER2(ELandscapeType.WATER2), // - value = 0x01
		WATER3(ELandscapeType.WATER3),
		WATER4(ELandscapeType.WATER4),
		WATER5(ELandscapeType.WATER5),
		WATER6(ELandscapeType.WATER6),
		WATER7(ELandscapeType.WATER7),
		WATER8(ELandscapeType.WATER8),
		UNKNOWN_08(null),
		UNKNOWN_09(null),
		UNKNOWN_0A(null),
		UNKNOWN_0B(null),
		UNKNOWN_0C(null),
		UNKNOWN_0D(null),
		UNKNOWN_0E(null),
		UNKNOWN_0F(null),
		GRASS(ELandscapeType.GRASS),
		MOUNTAINBORDEROUTER(ELandscapeType.MOUNTAINBORDEROUTER),
		UNKNOWN_12(null),
		UNKNOWN_13(null),
		DESERTBORDEROUTER(ELandscapeType.DESERTBORDEROUTER),
		MOORBORDER(ELandscapeType.MOORBORDER),
		UNKNOWN_16(null),
		MUDBORDER(ELandscapeType.MUDBORDER),
		UNKNOWN_18(null),
		UNKNOWN_19(null),
		UNKNOWN_1A(null),
		UNKNOWN_1B(null),
		UNKNOWN_1C(null),
		UNKNOWN_1D(null),
		UNKNOWN_1E(null),
		UNKNOWN_1F(null),
		MOUNTAIN(ELandscapeType.MOUNTAIN),
		MOUNTAINBORDER(ELandscapeType.MOUNTAINBORDER),
		UNKNOWN_22(null),
		SNOWBORDER(ELandscapeType.SNOWBORDER),
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
		SAND(ELandscapeType.SAND),
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
		DESERT(ELandscapeType.DESERT),
		DESERTBORDER(ELandscapeType.DESERTBORDER),
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
		MOORINNER(ELandscapeType.MOORINNER),
		MOOR(ELandscapeType.MOOR),
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
		RIVER1(ELandscapeType.RIVER1),
		RIVER2(ELandscapeType.RIVER2),
		RIVER3(ELandscapeType.RIVER3),
		RIVER4(ELandscapeType.RIVER4),
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
		SNOWINNER(ELandscapeType.SNOWINNER),
		SNOW(ELandscapeType.SNOW),
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
		MUDINNER(ELandscapeType.MUDINNER),
		MUD(ELandscapeType.MUD),
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

		NOT_A_TYPE(null); // - has to be the last item

		// - length of THIS enum (without END_OF_LIST)
		public static final int length = EOriginalLandscapeType.values().length - 1;
		public final ELandscapeType value;

		EOriginalLandscapeType(ELandscapeType value) {
			this.value = value;
		}

		public static EOriginalLandscapeType getTypeByInt(int type) {
			if (type < 0)
				return NOT_A_TYPE;
			if (type >= EOriginalLandscapeType.length)
				return NOT_A_TYPE;

			return EOriginalLandscapeType.values()[type];
		}
	}

	public enum EMapBuildingType {

		NOT_A_BUILDING(null), // - 0 is not defined

		STOCK(EBuildingType.STOCK),
		LUMBERJACK(EBuildingType.LUMBERJACK),
		STONECUTTER(EBuildingType.STONECUTTER),
		SAWMILL(EBuildingType.SAWMILL),
		FORESTER(EBuildingType.FORESTER),
		LOOKOUT_TOWER(EBuildingType.LOOKOUT_TOWER),
		COALMINE(EBuildingType.COALMINE),
		GOLDMINE(EBuildingType.GOLDMINE),
		IRONMINE(EBuildingType.IRONMINE),
		GOLDMELT(EBuildingType.GOLDMELT),
		Eisenschmelze(EBuildingType.IRONMELT),
		TOOLSMITH(EBuildingType.TOOLSMITH),
		WEAPONSMITH(EBuildingType.WEAPONSMITH),
		WINEGROWER(EBuildingType.WINEGROWER),
		TOWER(EBuildingType.TOWER),
		TOWER_BIG(EBuildingType.BIG_TOWER),
		Muehle(EBuildingType.MILL),
		CASTLE(EBuildingType.CASTLE),
		BARRACK(EBuildingType.BARRACK),
		BAKER(EBuildingType.BAKER),
		Metzger(EBuildingType.SLAUGHTERHOUSE),
		Destille(null),
		PIG_FARM(EBuildingType.PIG_FARM),
		FARM(EBuildingType.FARM),
		FISHER(EBuildingType.FISHER),
		LIVINGHOUSE_SMALL(EBuildingType.SMALL_LIVINGHOUSE),
		LIVINGHOUSE_MEDIUM(EBuildingType.MEDIUM_LIVINGHOUSE),
		LIVINGHOUSE_BIG(EBuildingType.BIG_LIVINGHOUSE),
		Schwefelmine(null),
		WATERWORKS(EBuildingType.WATERWORKS),
		Katapultwerk(null),
		DOCKYARD(EBuildingType.DOCKYARD),
		HARBOR(EBuildingType.HARBOR),
		Marktplatz(null),
		HOSPITAL(EBuildingType.HOSPITAL),
		Reisfarm(null),
		Edelsteinmine(null),
		Brauerei(null),
		CHARCOAL_BURNER(EBuildingType.CHARCOAL_BURNER),
		Pulvermacherei(null),
		Pyramide(null),
		Sphinx(null),
		BIG_TEMPLE(EBuildingType.BIG_TEMPLE), // TODO : does not work?!
		TEMPLE(EBuildingType.TEMPLE),
		grosse_Pagode(null),
		kleine_Pagode(null),
		Ballistenwerkstatt(null),
		Kanonenwerkstatt(null),
		DONKEY_FARM(EBuildingType.DONKEY_FARM),
		grosser_Gong(null),
		Impkerei(null),
		Metwinzerei(null),
		Labortorium(null),
		kleiner_Tempel(null),
		grosser_Tempel(null),
		SchwefelmineAmazonen(null),

		END_OF_LIST(null); // - has to be the last item

		// - length of THIS enum (without END_OF_LIST)
		public static final int length = EMapBuildingType.values().length - 1;
		public final EBuildingType value;

		EMapBuildingType(EBuildingType value) {
			this.value = value;
		}

		public static EMapBuildingType getTypeByInt(int type) {
			if (type < 0)
				return NOT_A_BUILDING;
			if (type >= EMapBuildingType.length)
				return NOT_A_BUILDING;

			return EMapBuildingType.values()[type];
		}

	}

	public enum EMapStackType {

		NOT_A_STACK(null), // - 0 is not defined

		PLANK(EMaterialType.PLANK),
		STONE(EMaterialType.STONE),
		Baumstamm(EMaterialType.TRUNK),
		BREAD(EMaterialType.BREAD),
		COAL(EMaterialType.COAL),
		GOLDORE(EMaterialType.GOLDORE),
		IRONORE(EMaterialType.IRONORE),
		FISH(EMaterialType.FISH),
		CROP(EMaterialType.CROP),
		GOLD(EMaterialType.GOLD),
		IRON(EMaterialType.IRON),
		SPADE(EMaterialType.BLADE),
		HAMMER(EMaterialType.HAMMER),
		AXE(EMaterialType.AXE),
		PICK(EMaterialType.PICK),
		SAW(EMaterialType.SAW),
		FISHINGROD(EMaterialType.FISHINGROD),
		SWORD(EMaterialType.SWORD),
		BOW(EMaterialType.BOW),
		SPEAR(EMaterialType.SPEAR),
		WINE(EMaterialType.WINE),
		FLOUR(EMaterialType.FLOUR),
		PIG(EMaterialType.PIG),
		MEAT(EMaterialType.MEAT),
		SULFUR(EMaterialType.SULFUR),
		WATER(EMaterialType.WATER),
		RICE(EMaterialType.RICE),
		GEMS(EMaterialType.GEMS),
		KEG(EMaterialType.KEG),
		Schiesspulver(null),
		unknown(null),
		SCYTHE(EMaterialType.SCYTHE),
		Reisschnaps(null),
		Met(null),
		HONEY(EMaterialType.HONEY),

		END_OF_LIST(null); // - has to be the last item

		// - length of THIS enum (without END_OF_LIST)
		public static final int length = EMapStackType.values().length - 1;
		public final EMaterialType value;

		EMapStackType(EMaterialType value) {
			this.value = value;
		}

		public static EMapStackType getTypeByInt(int type) {
			if (type < 0)
				return NOT_A_STACK;
			if (type >= EMapBuildingType.length)
				return NOT_A_STACK;

			return EMapStackType.values()[type];
		}

	}

	public enum EMapSettlersType {

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

		// - length of THIS enum (without END_OF_LIST)
		public static final int length = EMapSettlersType.values().length - 1;
		public final EMovableType value;

		EMapSettlersType(EMovableType value) {
			this.value = value;
		}

		public static EMapSettlersType getTypeByInt(int type) {
			if (type < 0)
				return NOT_A_SETTLER;
			if (type >= EMapSettlersType.length)
				return NOT_A_SETTLER;

			return EMapSettlersType.values()[type];
		}

	}

	// --------------------------------------------------//
	public enum EMapObjectTypeType {
		DECORATION,
		STONE,
		TREE
	}

	// --------------------------------------------------//
	public enum EObjectType {

		NO_OBJECT(null, 0), // - 0

		// TODO: EMapObjectTypeType.DECORATION does not work!
		UNKNOWN_01(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_1 = 1,
		UNKNOWN_02(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_2 = 2,
		UNKNOWN_03(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_3 = 3,
		UNKNOWN_04(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_4 = 4,
		UNKNOWN_05(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_5 = 5,
		UNKNOWN_06(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_6 = 6,
		UNKNOWN_07(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_7 = 7,
		UNKNOWN_08(EMapObjectTypeType.DECORATION, EMapObjectType.STONE), // - GAME_OBJECT_BIG_STONE_8 = 8,
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
		TREE_BIRCH_1(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_BIRCH_1 = 68,
		TREE_BIRCH_2(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_BIRCH_2 = 69,
		TREE_ELM_1(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_ELM_1 = 70,
		TREE_ELM_2(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_ELM_2 = 71,
		TREE_OAK_1(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_OAK_1 = 72,
		TREE_UNKNOWN_1(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_1 = 73,
		TREE_UNKNOWN_2(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_2 = 74,
		TREE_UNKNOWN_3(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_3 = 75,
		TREE_UNKNOWN_4(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_4 = 76,
		TREE_UNKNOWN_5(EMapObjectTypeType.TREE, 0), // - //-- unknown: 77
		TREE_ARECACEAE_1(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_ARECACEAE_1 = 78,
		TREE_ARECACEAE_2(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_ARECACEAE_2 = 79,
		TREE_UNKNOWN_6(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_UNKNOWN_6 = 80,
		UNKNOWN_51(null, 0), // - //-- unknown: 81
		UNKNOWN_52(null, 0), // - //-- unknown: 82
		UNKNOWN_53(null, 0), // - //-- unknown: 83
		UNKNOWN_54(EMapObjectTypeType.TREE, 0), // - GAME_OBJECT_TREE_SMALL = 84,
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
		RES_STONE_01(EMapObjectTypeType.STONE, 12), // - GAME_OBJECT_RES_STONE_01 = 115,
		RES_STONE_02(EMapObjectTypeType.STONE, 11), // - GAME_OBJECT_RES_STONE_02 = 116,
		RES_STONE_03(EMapObjectTypeType.STONE, 10), // - GAME_OBJECT_RES_STONE_03 = 117,
		RES_STONE_04(EMapObjectTypeType.STONE, 9), // - GAME_OBJECT_RES_STONE_04 = 118,
		RES_STONE_05(EMapObjectTypeType.STONE, 8), // - GAME_OBJECT_RES_STONE_05 = 119,
		RES_STONE_06(EMapObjectTypeType.STONE, 7), // - GAME_OBJECT_RES_STONE_06 = 120,
		RES_STONE_07(EMapObjectTypeType.STONE, 6), // - GAME_OBJECT_RES_STONE_07 = 121,
		RES_STONE_08(EMapObjectTypeType.STONE, 5), // - GAME_OBJECT_RES_STONE_08 = 122,
		RES_STONE_09(EMapObjectTypeType.STONE, 4), // - GAME_OBJECT_RES_STONE_09 = 123,
		RES_STONE_10(EMapObjectTypeType.STONE, 3), // - GAME_OBJECT_RES_STONE_10 = 124,
		RES_STONE_11(EMapObjectTypeType.STONE, 2), // - GAME_OBJECT_RES_STONE_11 = 125,
		RES_STONE_12(EMapObjectTypeType.STONE, 1), // - GAME_OBJECT_RES_STONE_12 = 126,
		RES_STONE_13(EMapObjectTypeType.STONE, 0), // - GAME_OBJECT_RES_STONE_13 = 127,

		END_OF_LIST(null, 0); // - has to be the last item

		public final EMapObjectTypeType type;
		public final int style;

		public static final int length = EObjectType.values().length - 1;

		EObjectType(EMapObjectTypeType type, int style) {
			this.type = type;
			this.style = style;
		}

		EObjectType(EMapObjectTypeType type, EMapObjectType style) {
			this.type = type;
			this.style = style.ordinal();
		}

		public static EObjectType getTypeByInt(int type) {
			if (type < 0)
				return NO_OBJECT;
			if (type >= EObjectType.length)
				return NO_OBJECT;

			return EObjectType.values()[type];
		}

		public MapObject getNewInstance() {
			if (type == null)
				return null;

			switch (type) {
			case DECORATION:
				if (style < 0)
					return null;
				if (style >= EMapObjectType.values().length)
					return null;

				// - TODO: does not work?!
				return new MapDecorationObject(EMapObjectType.values()[style]);

			case STONE:
				return MapStoneObject.getInstance(style);

			case TREE:
				return MapTreeObject.getInstance();

			default:
				return null;
			}
		}

	}
}