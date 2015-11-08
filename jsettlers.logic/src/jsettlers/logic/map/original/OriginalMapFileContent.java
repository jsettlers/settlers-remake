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
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

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
	private boolean startTowerMaterialsAndSettlersWereSet = false;
	private EMapStartResources mapStartResources;
	public int fileChecksum = 0;
	
	//- original maps are squared
	private int widthHeight = 0;
	private int dataCount = 0;

	private byte[] height;
	private ELandscapeType[] landscapeType;
	private MapObject[] object;
	private byte[] plyerClaim;
	private byte[] accessible;
	private EResourceType[] resourceTypes;
	private byte[] resourceAmounts;

	private MapPlayerInfo[] mapPlayerInfos;
	
	public OriginalMapFileContent(int widthHeight) {
		setWidthHeight(widthHeight);
	}
	
	public void setWidthHeight(int widthHeight) {
		this.widthHeight = widthHeight;
		
		dataCount = widthHeight * widthHeight;
		
		height = new byte[dataCount];
		landscapeType = new ELandscapeType[dataCount];
		object = new MapObject[dataCount];
		plyerClaim = new byte[dataCount];
		accessible = new byte[dataCount];
		resourceTypes = new EResourceType[dataCount];
		resourceAmounts = new byte[dataCount];
	}
	
	public void setLandscapeHeight(int pos, byte height) {
		if ((pos<0) || (pos> dataCount)) return;
		
		this.height[pos] = height;
	}

	private List<OriginalMapFileDataStructs.EOriginalLandscapeType> types = new Vector<OriginalMapFileDataStructs.EOriginalLandscapeType>();

	public void setLandscape(int pos, short type) {
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

	public void setMapObject(int pos, short type) {
		if ((pos<0) || (pos> dataCount)) return;

		OriginalMapFileDataStructs.EObjectType originalType = OriginalMapFileDataStructs.EObjectType.getTypeByInt(type);
		//TODO: remove me when Original Maps are finished ---- begin
		/*if (!mapObjects.contains(originalType)) {
			mapObjects.add(originalType);
			System.out.print("#" + originalType + "(" + (pos % widthHeight) + "|" + (pos / widthHeight) + ")");
			if (originalType == OriginalMapFileDataStructs.EObjectType.NOT_A_TYPE) {
				System.out.println(" (not a type: " + type + ")");
			}
			if (originalType.value != null) {
				System.out.println(" (" + originalType.value + ")");
			} else System.out.println();
		}*/
		//TODO: remove me when Original Maps are finished ---- end

		object[pos] = OriginalMapFileDataStructs.EObjectType.getTypeByInt(type).value;
	}
	
	public void setPalyerClaim(int pos, byte player) {
		if ((pos<0) || (pos> dataCount)) return;
		
		plyerClaim[pos] = player;
	}
	
	public void setAccessible(int pos, byte isAccessible) {
		if ((pos<0) || (pos> dataCount)) return;
		
		accessible[pos] = isAccessible;
	}
	
	public void setResources(int pos, byte resource) {
		if ((pos<0) || (pos> dataCount)) return;
		
		resourceTypes[pos] = OriginalMapFileDataStructs.getResourceTypeFrom(resource);
		resourceAmounts[pos] = OriginalMapFileDataStructs.getResourceAmountFrom(resource);
	}

	public void setMapPlayerInfos(MapPlayerInfo[] mapPlayerInfos, EMapStartResources startResources) {
		this.mapPlayerInfos = mapPlayerInfos;
		this.mapStartResources = startResources;
		addStartTowerMaterialsAndSettlers();
	}

	private void addStartTowerMaterialsAndSettlers() {
		if (!startTowerMaterialsAndSettlersWereSet) {
			for (byte playerId = 0; playerId < mapPlayerInfos.length; playerId++) {
				int towerPosition = mapPlayerInfos[playerId].startY * widthHeight + mapPlayerInfos[playerId].startX;
				object[towerPosition] =  new BuildingObject(EBuildingType.TOWER, playerId);
				List<MapObject> mapObjects = EMapStartResources.generateStackObjects(mapStartResources);
				mapObjects.addAll(EMapStartResources.generateMovableObjects(mapStartResources, playerId));

				List<RelativePoint> towerTiles = Arrays.asList(EBuildingType.TOWER.getProtectedTiles());

				//RelativePoint relativeMapObjectPoint = new RelativePoint(-3, 4);
				RelativePoint relativeMapObjectPoint = new RelativePoint(-1, 2);
				for (MapObject currentMapObject : mapObjects) {
					do {
						int mapObjectPosition = relativeMapObjectPoint.calculateY(mapPlayerInfos[playerId].startY)
								* widthHeight + relativeMapObjectPoint.calculateX(mapPlayerInfos[playerId].startX);
						if (object[mapObjectPosition] == null && !towerTiles.contains(relativeMapObjectPoint)) {
							object[mapObjectPosition] = currentMapObject;
							relativeMapObjectPoint = nextPointOnSpiral(relativeMapObjectPoint);
							break;
						}
						relativeMapObjectPoint = nextPointOnSpiral(relativeMapObjectPoint);
					} while (true);
				}
			}
			startTowerMaterialsAndSettlersWereSet = true;
		}
	}

	private RelativePoint nextPointOnSpiral(RelativePoint previousPoint) {
		short previousX = previousPoint.getDx();
		short previousY = previousPoint.getDy();
		short basis = (short) Math.max(Math.abs(previousX), Math.abs(previousY));
		if (previousX == basis && previousY > basis * -1) return new RelativePoint(previousX, previousY-1);
		if (previousX == basis *-1 && previousY <= basis) return new RelativePoint(previousX, previousY+1);
		if (previousX < basis && previousY == basis) return new RelativePoint(previousX+1, previousY);
		if (previousX > basis *-1 && previousY == basis *-1) return new RelativePoint(previousX-1, previousY);
		return null;
	}

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
		
		return object[pos];
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
		int pos = y * widthHeight + x;

		if ((pos < 0) || (pos > dataCount)) return 0;

		return resourceAmounts[pos];
	}

	@Override
	public EResourceType getResourceType(short x, short y) {
		int pos = y * widthHeight + x;

		if ((pos < 0) || (pos > dataCount)) return null;

		return resourceTypes[pos];
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
			return new ShortPoint2D(100,100);
		}
		return new ShortPoint2D(mapPlayerInfos[player].startX, mapPlayerInfos[player].startY);
	}
	
	@Override
	public int getPlayerCount() {
		return mapPlayerInfos.length;
	}

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

		public MapPlayerInfo(int X, int Y, String playerName, OriginalMapFileDataStructs.EMapNations nation) {
			this.startX = X;
			this.startY = Y;
			this.playerName = playerName;
			this.nation = nation;
		}
	}

	//--------------------------------------------------//

}