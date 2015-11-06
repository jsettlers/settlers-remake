package jsettlers.logic.map.original;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;

import java.util.List;
import java.util.Vector;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContent implements IMapData
{
	//--------------------------------------------------//
	public static class MapPlayerInfo {
		public static int startX;
		public static int startY;
		public static String playerName;
		public static OriginalMapFileDataStructs.EMapNations nation;
		
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
	
	
	public int fileChecksum = 0;
	
	//- original maps are squared
	private int widthHeight = 0;
	private int dataCount = 0;

	private byte [] height = null;
	private ELandscapeType[] landscapeType = null;
	private MapObject [] object = null ;
	private byte [] plyerClaim = null ;
	private byte [] accessible = null ;
	private byte [] resources = null;

	public MapPlayerInfo[] mapPlayerInfos;
	
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
		resources = new byte[dataCount];
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
		if (!mapObjects.contains(originalType)) {
			mapObjects.add(originalType);
			System.out.print("#" + originalType + "(" + (pos % widthHeight) + "|" + (pos / widthHeight) + ")");
			if (originalType == OriginalMapFileDataStructs.EObjectType.NOT_A_TYPE) {
				System.out.println(" (not a type: " + type + ")");
			}
			if (originalType.value != null) {
				System.out.println(" (" + originalType.value + ")");
			} else System.out.println();
		}
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
	
	public void setResources(int pos, byte Resources) {
		if ((pos<0) || (pos> dataCount)) return;
		
		resources[pos] = Resources;
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
		
		//- for debugging:
		if (mapPlayerInfos.length>0) if ((x== mapPlayerInfos[0].startX) && (y== mapPlayerInfos[0].startY)) return new BuildingObject(EBuildingType.TOWER, (byte)0);
		if (mapPlayerInfos.length>1) if ((x== mapPlayerInfos[1].startX) && (y== mapPlayerInfos[1].startY)) return new BuildingObject(EBuildingType.TOWER, (byte)0);
		if (mapPlayerInfos.length>2) if ((x== mapPlayerInfos[2].startX) && (y== mapPlayerInfos[2].startY)) return new BuildingObject(EBuildingType.TOWER, (byte)0);
		if (mapPlayerInfos.length>3) if ((x== mapPlayerInfos[3].startX) && (y== mapPlayerInfos[3].startY)) return new BuildingObject(EBuildingType.TOWER, (byte)0);
		//--------------
		
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
			return new ShortPoint2D(100,100);
		}
		return new ShortPoint2D(mapPlayerInfos[player].startX, mapPlayerInfos[player].startY);
	}
	
	@Override
	public int getPlayerCount() {
		return mapPlayerInfos.length;
	}
	



	
	
}