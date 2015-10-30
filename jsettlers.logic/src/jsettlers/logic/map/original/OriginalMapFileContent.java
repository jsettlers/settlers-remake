package jsettlers.logic.map.original;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.*;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.original.OriginalMapFileDataStructs;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContent implements IMapData
{
	
	public int fileChecksum = 0;
	
	//- original maps are squared
	private int _WidthHeight = 0;
	private int _dataCount = 0;

	private byte [] _height = null;
	private ELandscapeType[] _Type = null;
	private MapObject [] _Object = null ;
	private byte [] _plyerClaim = null ;
	private byte [] _accessible = null ;
	private byte [] _resources = null;

	public OriginalMapFileContent(int WidthHeight)
	{
		setWidthHeight(WidthHeight);
	}
	
	
	public void setWidthHeight(int WidthHeight)
	{
		_WidthHeight = WidthHeight;
		
		_dataCount = WidthHeight * WidthHeight;
		
		_height = new byte[_dataCount];
		_Type = new ELandscapeType[_dataCount];
		_Object = new MapObject[_dataCount];
		_plyerClaim = new byte[_dataCount];
		_accessible = new byte[_dataCount];
		_resources = new byte[_dataCount];
	}
	
	public void setLandscapeHeight(int pos, byte height)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_height[pos] = height;
	}
	
	public void setLandscape(int pos, byte type)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_Type[pos] = OriginalMapFileDataStructs.LANDSCAPE_TYPE.getTypeByInt(type).value;
	}
	
	public void setMapObject(int pos, byte type)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_Object[pos] = OriginalMapFileDataStructs.OBJECT_TYPE.getTypeByInt(type).value;;
	}
	
	public void setPalyerClaim(int pos, byte player)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_plyerClaim[pos] = player;
	}
	
	public void setAccessible(int pos, byte isAccessible)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_accessible[pos] = isAccessible;
	}
	
	public void setResources(int pos, byte Resources)
	{
		if ((pos<0) || (pos>_dataCount)) return;
		
		_resources[pos] = Resources;
	}
	
	
	//------------------------------//
	//-- Interface IMapData --//
	
	@Override
	public int getWidth()
	{
		return _WidthHeight;
	}
	
	@Override
	public int getHeight()
	{
		return _WidthHeight;
	}

	@Override
	public ELandscapeType getLandscape(int x, int y)
	{
		int pos = y * _WidthHeight + x;
		
		if ((pos < 0) || (pos > _dataCount)) return ELandscapeType.WATER1;
		
		if (_Type[pos]==null) return ELandscapeType.GRASS;
		
		return _Type[pos];
	}
	
	@Override
	public MapObject getMapObject(int x, int y)
	{
		int pos = y * _WidthHeight + x;
		
		if ((pos < 0) || (pos > _dataCount)) return null;
		
		if ((x==100) && (y==100))
		{
			return new BuildingObject(EBuildingType.TOWER, (byte)0);
		}
		
		return _Object[pos];
	}
	
	@Override
	public byte getLandscapeHeight(int x, int y)
	{
		int pos = y * _WidthHeight + x;
		
		if ((pos < 0) || (pos > _dataCount)) return 0;
		
		return _height[pos];
	}

	/**
	 * Gets the amount of resources for a given position. In range 0..127
	 */
	@Override
	public byte getResourceAmount(short x, short y)
	{
		return 0;
	}

	@Override
	public EResourceType getResourceType(short x, short y)
	{
		return EResourceType.FISH;
	}
	
	/**
	 * Gets the id of the blocked partition of the given position.
	 * @return The id of the blocked partition the given position belongs to.
	 */
	@Override
	public short getBlockedPartition(short x, short y)
	{
		int pos = y * _WidthHeight + x;
		
		if ((pos < 0) || (pos > _dataCount)) return 0;
		
		//- Player1=1 ... Player2=2 ... noPlayer=0
		return _plyerClaim[pos];
	}
	
	
	@Override
	public ShortPoint2D getStartPoint(int player)
	{
		return new ShortPoint2D(100,100);
	}
	
	@Override
	public int getPlayerCount()
	{
		return 1;
	}
	



	
	
}