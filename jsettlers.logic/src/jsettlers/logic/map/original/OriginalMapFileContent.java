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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.original.OriginalMapFileContent.MapPlayerInfo;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapBuildingType;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapSettlersType;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapStackType;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapStartResources;

/**
 * @author Thomas Zeugner
 * @author codingberlin
 */
public class OriginalMapFileContent implements IMapData
{


	//--------------------------------------------------//
	public static class MapPlayerInfo {
		public int startX;
		public int startY;
		public String playerName;
		public OriginalMapFileDataStructs.EMapNations nation;
		
		public MapPlayerInfo(int X, int Y, String playerName, int nationInt) {
			this.startX = X;
			this.startY = Y;
			this.playerName = playerName;
			this.nation = OriginalMapFileDataStructs.EMapNations.FromMapValue(nationInt);
		}
		
		public MapPlayerInfo(int X, int Y) {
			this.startX = X;
			this.startY = Y;
			this.playerName = "";
			this.nation = OriginalMapFileDataStructs.EMapNations.ROMANS;
		}
	}
	
	//--------------------------------------------------//
	
	
	public int fileChecksum = 0;
	
	//- original maps are squared
	private int widthHeight = 0;
	private int dataCount = 0;

	private byte [] height = null;
	private ELandscapeType[] landscapeType = null;
	private MapObject [] mapObject = null ;
	private byte [] plyerClaim = null ;
	private byte [] accessible = null ;
	private byte [] resources = null;

	private MapPlayerInfo[] mapPlayerInfos;
	
	public OriginalMapFileContent(int widthHeight) {
		setWidthHeight(widthHeight);
	}
	
	public void setWidthHeight(int widthHeight) {
		this.widthHeight = widthHeight;
		
		dataCount = widthHeight * widthHeight;
		
		height = new byte[dataCount];
		landscapeType = new ELandscapeType[dataCount];
		mapObject = new MapObject[dataCount];
		plyerClaim = new byte[dataCount];
		accessible = new byte[dataCount];
		resources = new byte[dataCount];
	}
	
	public void setLandscapeHeight(int pos, int height) {
		if ((pos<0) || (pos> dataCount)) return;
		
		//- apply scaling of remake to original...
		height = height / 2;
		
		// TODO: original maps can be higher then 127!
		if (height>127) height=127;
		if (height<0) height=0;
		
		this.height[pos] = (byte)height;
	}

	private List<OriginalMapFileDataStructs.EOriginalLandscapeType> types = new Vector<OriginalMapFileDataStructs.EOriginalLandscapeType>();

	public void setLandscape(int pos, int type) {
		if ((pos<0) || (pos> dataCount)) return;

		OriginalMapFileDataStructs.EOriginalLandscapeType originalType = OriginalMapFileDataStructs.EOriginalLandscapeType.getTypeByInt(type);

		//TODO: remove me when Original Maps are finished ---- begin
		/*if (!types.contains(originalType)) {
			types.add(originalType);
			System.out.print("#" + originalType + "(" + (pos % widthHeight) + "|" + (pos / widthHeight) + ")");
			if (originalType == OriginalMapFileDataStructs.EOriginalLandscapeType.NOT_A_TYPE) {
				System.out.println(" (not a type: " + type + ")");
			}
			if (originalType.value != null) {
				System.out.println(" (" + originalType.value + ")");
			} else System.out.println();
		}*/
		//TODO: remove me when Original Maps are finished ---- end
		landscapeType[pos] = originalType.value;
	}
	
	
	private List<OriginalMapFileDataStructs.EObjectType> mapObjects = new Vector<OriginalMapFileDataStructs.EObjectType>();

	public void setMapObject(int pos, int type) {
		if ((pos<0) || (pos> dataCount)) return;

		OriginalMapFileDataStructs.EObjectType originalType = OriginalMapFileDataStructs.EObjectType.getTypeByInt(type);
		//TODO: remove me when Original Maps are finished ---- begin
		if (!mapObjects.contains(originalType)) {
			mapObjects.add(originalType);
			System.out.print("#" + originalType + "(" + (pos % widthHeight) + "|" + (pos / widthHeight) + ")");
			if (originalType == OriginalMapFileDataStructs.EObjectType.NO_OBJECT) {
				System.out.println(" (not a type: " + type + ")");
			}
			if (originalType.value != null) {
				System.out.println(" (" + originalType.value + ")");
			} else System.out.println();
		}
		//TODO: remove me when Original Maps are finished ---- end

		mapObject[pos] = OriginalMapFileDataStructs.EObjectType.getTypeByInt(type).value;
	}
	
	public void setPlayerCount(int count)
	{
		mapPlayerInfos = new MapPlayerInfo[count];
		
		for (int i=0; i < count; i++) {
			mapPlayerInfos[i] = new MapPlayerInfo(20+i*10,20+i*10);
		}
	}
	
	
	public void setPlayer(int index, int x, int y, int NationType, String PlayerName)
	{
		System.out.println("Player "+ Integer.toString(index) +" : "+ PlayerName +" @ ("+ x +" , "+ y +")");
		
		if ((index < 0) || (index >= mapPlayerInfos.length)) return;
		
		mapPlayerInfos[index].nation = OriginalMapFileDataStructs.EMapNations.FromMapValue(NationType);
		mapPlayerInfos[index].startX = x;
		mapPlayerInfos[index].startY = y;
		mapPlayerInfos[index].playerName = PlayerName;
	}
	
	public void setPlayer(int index, int x, int y, OriginalMapFileDataStructs.EMapNations NationType, String PlayerName)
	{
		if ((index < 0) || (index >= mapPlayerInfos.length)) return;
		
		mapPlayerInfos[index].nation = NationType;
		mapPlayerInfos[index].startX = x;
		mapPlayerInfos[index].startY = y;
		mapPlayerInfos[index].playerName = PlayerName;
	}
	
	
	public void setMapObject(int x, int y, MapObject newMapObject)	{
		
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return;
		

		mapObject[pos] = newMapObject;
	}
	
	public void setBuilding(int x, int y, int BType, int party, int countSword1, int countSword2, int countSword3, int countArcher1, int countArcher2, int countArcher3, int countSpear1, int countSpear2, int countSpear3) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return;
		
		EMapBuildingType BuildingType = EMapBuildingType.getTypeByInt(BType);
		
		if (BuildingType == EMapBuildingType.NOT_A_BUILDING) return;
		if (BuildingType.value != null) {
			mapObject[pos] = new BuildingObject(BuildingType.value, (byte)party);
		}
	}
	
	public void setSettler(int x, int y, int SType, int party) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return;
		
		EMapSettlersType SettlerType = EMapSettlersType.getTypeByInt(SType);
		
		if (SettlerType == EMapSettlersType.NOT_A_SETTLER) return;
		if (SettlerType.value != null) {
			mapObject[pos] = new MovableObject(SettlerType.value, (byte)party);
		}
	}
	
	
	
	public void setStack(int x, int y, int SType, int count) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return;
		
		EMapStackType StackType = EMapStackType.getTypeByInt(SType);
		
		if (StackType == EMapStackType.NOT_A_STACK) return;
		if (StackType.value != null) {
			mapObject[pos] = new StackObject(StackType.value, count);
		}
	}

	
	public void setPalyerClaim(int pos, byte player) {
		if ((pos < 0) || (pos > dataCount)) return;
		
		plyerClaim[pos] = player;
	}
	
	public void setAccessible(int pos, byte isAccessible) {
		if ((pos < 0) || (pos > dataCount)) return;
		
		accessible[pos] = isAccessible;
	}
	
	public void setResources(int pos, byte Resources) {
		if ((pos < 0) || (pos > dataCount)) return;
		
		resources[pos] = Resources;
	}

	//public void setMapPlayers(MapPlayerInfo[] mapPlayerInfos) {
	//	this.mapPlayerInfos = mapPlayerInfos;
	//}



	
	//------------------------//
	//-- Interface IMapData --//
	//------------------------//
	
	@Override
	public int getWidth() {
		return widthHeight;
	}
	
	@Override
	public int getHeight() {
		return widthHeight;
	}

	@Override
	public ELandscapeType getLandscape(int x, int y) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return ELandscapeType.WATER1;
		
		if (landscapeType[pos]==null) return ELandscapeType.GRASS;
		
		return landscapeType[pos];
	}
	
	@Override
	public MapObject getMapObject(int x, int y) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return null;
		
		return mapObject[pos];
	}

	@Override
	public byte getLandscapeHeight(int x, int y) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return 0;
		
		return height[pos];
	}

	/**
	 * Gets the amount of resources for a given position. In range 0..127
	 */
	@Override
	public byte getResourceAmount(short x, short y) {
		return 0;
	}

	@Override
	public EResourceType getResourceType(short x, short y) {
		return EResourceType.FISH;
	}
	
	/**
	 * Gets the id of the blocked partition of the given position.
	 * @return The id of the blocked partition the given position belongs to.
	 */
	@Override
	public short getBlockedPartition(short x, short y) {
		int pos = y * widthHeight + x;
		
		if ((pos < 0) || (pos > dataCount)) return 0;
		
		//- Player1=1 ... Player2=2 ... noPlayer=0
		return plyerClaim[pos];
	}
	
	
	@Override
	public ShortPoint2D getStartPoint(int player) {
		if ((player < 0) || (player >= mapPlayerInfos.length))
		{
			System.out.print("Error: not a player for getStartPoint("+ player +")");
			return new ShortPoint2D(100,100);
		}
		return new ShortPoint2D(mapPlayerInfos[player].startX, mapPlayerInfos[player].startY);
	}
	
	@Override
	public int getPlayerCount() {
		return mapPlayerInfos.length;
	}
}