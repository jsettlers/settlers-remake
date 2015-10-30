package jsettlers.logic.map.original;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.*;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.original.OriginalMapFileDataStructs;
import jsettlers.logic.map.original.OriginalMapFileContent.MapPlayerInfo;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.MAP_START_RESOURCES;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContentReader
{
	//--------------------------------------------------//
	public static class MapResourceInfo
	{
		public int offset=0;
		public int size=0;
		public int cryptKey=0;
		public boolean hasBeenDecrypted = false;
	}
	//--------------------------------------------------//
	
	
	
	public int fileChecksum = 0;
	public String MapName = "";
	public int WidthHeight;
	
	
	public boolean isSinglePlayerMap = false;
	
	private byte[] _mapContent;
	private int _fileVersion = 0;
	private MapResourceInfo[] _resources;
	

	
	public MAP_START_RESOURCES StartResources = MAP_START_RESOURCES.SMALL;
	public OriginalMapFileContent.MapPlayerInfo[] Players;
	
	
	public OriginalMapFileContentReader(InputStream originalMapFile) throws IOException 
	{
		//- init Resource Info
		_resources = new MapResourceInfo[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.length];
		for(int i=0; i<_resources.length;i++) _resources[i] = new MapResourceInfo();
		
		//- init players
		Players = new MapPlayerInfo[1];
		Players[0] = new MapPlayerInfo(100,100,"",OriginalMapFileDataStructs.MAP_NATIONS.Nation_Romans );

		
		
		//- read File into buffer
		_mapContent = getBytesFromInputStream(originalMapFile);
	}



	public static byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
	    try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
	    {
	        byte[] buffer = new byte[0xFFFF];
	
	        for (int len; (len = is.read(buffer)) != -1;)
	            os.write(buffer, 0, len);
	
	        os.flush();
	
	        return os.toByteArray();
	    }
	    catch (Exception e)
	    {
	    	return new byte[0];
	    }
	}


	//- Read Big-Ending INT from Buffer
	public int readBEIntFrom(int numberOfBytes, int offset)
	{
		int result = 0;
		for (int i = 0; i < numberOfBytes; i++)
		{
			//-                     ( signed byte 2 unsigned int )
			result = result | ((int)( _mapContent[i+offset] & 0xFF) << (i << 3));
		}
		return result;
	}
	
	
	//- Read Big-Ending INT from Buffer
	public int readBEIntFrom(int offset)
	{
		if (_mapContent == null) return 0;
		return  ((_mapContent[offset  ] & 0xFF) << 0) |
				((_mapContent[offset+1] & 0xFF) << 8) |
				((_mapContent[offset+2] & 0xFF) << 16) |
				((_mapContent[offset+3] & 0xFF) << 24);
	}
	
	
	public String readCStrFrom(int offset, int length)
	{
		if (_mapContent == null) return "";
		if (_mapContent.length <= offset + length) return "";
		
		String outStr = "";
		int pos = offset;
		
		for (int i=length; i>0; i--)
		{
			byte b = _mapContent[pos];
			pos++;
			
			if (b==0) break;
			
			outStr += new String(new byte[] {b}); 
		}
		return outStr;
	}
	
	
	boolean isChecksumValid()
	{
		//- read Checksum from File
		int fileChecksum = readBEIntFrom(0);
		
		//- make count a Multiple of four
		int count = _mapContent.length & 0xFFFFFFFC;
		int currentChecksum = 0;
		
		//- Map Content start at Byte 8
		for (int i = 8; i < count ; i+=4)
		{
			//- read DWord
			int currentInt = readBEIntFrom(i);
			
			//- using: Logic Right-Shift-Operator: >>>
			currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
		}

		//- return TRUE if the checksum is OK!
		return (currentChecksum == fileChecksum);
	}
	
	
	//- Reads in the Map-File-Structure
	boolean loadMapResources()
	{
		//- Version of File:  0x0A : Original Siedler Map ; 0x0B : Amazon Map
		_fileVersion = readBEIntFrom(4);
		
		//- check if the Version is compatible?
		if ((_fileVersion != 0x0A) && (_fileVersion != 0x0B)) return false;
		
		//- Data lenght
		int DataLenght = _mapContent.length;

		//- start of map-content
		int FilePos = 8; 
		int PartTypeTemp = 0;
		
		do
		{
			PartTypeTemp = readBEIntFrom(FilePos);
			int PartLen = readBEIntFrom(FilePos + 4);

			//- don't know what the [FileTypeSub] is for -> it should by zero
			int PartTypeSub = (PartTypeTemp << 16) & 0x0000FFFF;
			int PartType = (PartTypeTemp & 0x0000FFFF);

			//- position/start of data
			int MapPartPos = FilePos + 8;

			//- debug output
			System.out.println("@ "+ FilePos +"   size: "+ PartLen +"  Type:"+ PartType +"  Sub:"+ PartTypeSub +"  -> "+  OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.getTypeByInt(PartType).toString() );

					
			//- next position in File
			FilePos = FilePos + PartLen;

			//- save the values
			if ((PartType > 0) && (PartType < _resources.length) && (PartLen>=0))
			{
				_resources[PartType].cryptKey = PartType;
				_resources[PartType].hasBeenDecrypted = false;
				_resources[PartType].offset = MapPartPos;
				_resources[PartType].size = PartLen - 8;
			}

		}
		while ((PartTypeTemp != 0) && ((FilePos+8) <= DataLenght));
	
		
		return true;
	}

	
	
	public void readBasicMapInformation()
	{
		//- Reset
		fileChecksum = 0;
		WidthHeight=0;
		
		//- safety checks
		if (_mapContent==null) return;
		if (_mapContent.length < 100) return;
		
		//- checksum is the first DWord in File
		fileChecksum = readBEIntFrom(0);
		
		//- read Map Information
		MapInfo();
		PlayerInfo();
		
		
		//- reset
		WidthHeight=0;
	
		//- get resource information for the area 
		MapResourceInfo FPart = _resources[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area.value];
		if (FPart==null) return;
		if (FPart.size < 4) return;

		//- uncrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area)) return ;	

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		WidthHeight = readBEIntFrom(pos);
	}
	
	
	
	public void MapInfo()
	{
		MapResourceInfo FPart = _resources[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.MapInfo.value];
		
		if ((FPart==null) || (FPart.size == 0))
		{
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.MapInfo)) return;	

		//- file position
		int pos = FPart.offset;
		
		//----------------------------------
		int MapType = readBEIntFrom(pos);
		pos += 4;
			  
		if (MapType == 1) 
		{
			isSinglePlayerMap = true;
		}
		else if (MapType == 0)
		{
			isSinglePlayerMap = false;
		}
		else
		{
			System.err.println("wrong value for 'isSinglePlayerMap' "+ Integer.toString(MapType) +" in mapfile!");
		}
				 

		//----------------------------------  
		int PlayerCount = readBEIntFrom(pos);
		pos += 4;
		
		Players = new MapPlayerInfo[PlayerCount];
		
		for (int i=0; i<PlayerCount; i++)
		{
			Players[i] = new MapPlayerInfo(20+i*10,20+i*10, Integer.toString(i) ,OriginalMapFileDataStructs.MAP_NATIONS.Nation_Romans );
		}
		
		
		//----------------------------------
		int StartResourcesValue = readBEIntFrom(pos);
		pos += 4;
		
		this.StartResources = MAP_START_RESOURCES.FromMapValue(StartResourcesValue);
	}
	
	
	
	//- Read the Player Info
	public void PlayerInfo()
	{
		MapResourceInfo FPart = _resources[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.PlayerInfo.value];
		
		if ((FPart==null) || (FPart.size == 0))
		{
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.PlayerInfo)) return;	

		//- file position
		int pos = FPart.offset;
		
		for (int i = 0; i < Players.length; i++)
		{
			Players[i].Nation = OriginalMapFileDataStructs.MAP_NATIONS.FromMapValue(readBEIntFrom(pos));
			pos += 4;
			
			Players[i].startX = readBEIntFrom(pos);
			pos += 4;
			
			Players[i].startY = readBEIntFrom(pos);
			pos += 4;
			
			Players[i].PName = readCStrFrom(pos, 33);
			pos += 33;
			
			System.out.println("Player "+ Integer.toString(i) +" : "+ Players[i].PName +" @ ("+ Players[i].startX +" , "+ Players[i].startY +")");
		}
	}
	
	
	
	//- Reads in the Map Data and returns a IMapData
	public OriginalMapFileContent readMapData()
	{
		OriginalMapFileContent mapData = new OriginalMapFileContent(0);
		
		mapData.fileChecksum = fileChecksum;
		
		//- get resource information for the area 
		MapResourceInfo FPart = _resources[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area.value];
		if (FPart==null) return mapData;
		
		if (FPart.size == 0)
		{
			System.err.println("Warning: No area information available in mapfile!");
			return mapData;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area)) return mapData;	


		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		int WidthHeight = readBEIntFrom(pos);
		pos+=4;
		
		//- init size of MapData
		mapData.setWidthHeight(WidthHeight);

		//- points to read
		int dataCount = WidthHeight * WidthHeight;
		
		for (int i=0; i< dataCount; i++)
		{
		//for (int y = 0; y < WidthHeight; y++)
		//{
		//	for (int x = 0; x < WidthHeight; x++)
		//	{
		//		int i = x + (WidthHeight * (WidthHeight - y - 1));
				
				mapData.setLandscapeHeight(i, _mapContent[pos++]);
				mapData.setLandscape(i, _mapContent[pos++]);
				mapData.setMapObject(i, _mapContent[pos++]);
	
				mapData.setPalyerClaim(i, _mapContent[pos++]);
	
				mapData.setAccessible(i, _mapContent[pos++]);
				mapData.setResources(i, _mapContent[pos++]);
		//	}
		//}
		}
		
		//- add palyers
		mapData.Players = this.Players;
		
		return mapData;
	}
	
	
	
	//- uncrypt a resource
	private boolean doDecrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE type)
	{
		int PartId = type.value;
		if ((PartId <= 0) || (PartId >= OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.length)) return false;
		
		if (_mapContent==null)
		{
			System.err.println("Warning: Unable to decrypt map file: no data loaded!");
			return false;
		}
		
		//- already encrypted
		if (_resources[PartId].hasBeenDecrypted) return true;
		
		//- length of data
		int length = _resources[PartId].size;
		if (length <= 0) return true;
		
		//- start of data
		int pos = _resources[PartId].offset;
		
		//- init the key
		int key = (_resources[PartId].cryptKey & 0xFF);
		
		
		for (int i = length; i > 0; i--)
		{
			//- read one byte
			int byt = (_mapContent[pos] ^ key) & 0xFF;

			//- write Byte
			_mapContent[pos] =(byte)byt;
			
			//- next position/byte
			pos++;
			if (pos >= _mapContent.length)
			{
				System.err.println("Error: Unable to decrypt map file: unexpected eof!");
				return false;
			}
				
			//- calculate next Key
			key = ((key << 1) & 0xFF) ^ byt;
		}

		_resources[PartId].hasBeenDecrypted = true;
		
		
		return true;
	}
	
}