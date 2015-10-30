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
	
	public int MinPlayers = 1; // TODO
	public int MaxPlayers = 1; // TODO
	
	private byte[] _mapContent;
	private int _fileVersion = 0;
	private MapResourceInfo[] _resources;
	
	
	public OriginalMapFileContentReader(InputStream originalMapFile) throws IOException 
	{
		//- init Resource Info
		_resources = new MapResourceInfo[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.length];
		for(int i=0; i<_resources.length;i++) _resources[i] = new MapResourceInfo();
		
		
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
		return  ((_mapContent[offset  ] & 0xFF) << 0) |
				((_mapContent[offset+1] & 0xFF) << 8) |
				((_mapContent[offset+2] & 0xFF) << 16) |
				((_mapContent[offset+3] & 0xFF) << 24);
	}
	
	
	boolean isChecksumValid()
	{
		//- read Checksum from File
		int fileChecksum = readBEIntFrom(0);
		
		int count = _mapContent.length;
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
		
		//- reset
		WidthHeight=0;
	
		//- get resource information for the area 
		MapResourceInfo FPart = _resources[OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area.value];
		if (FPart==null) return;
		if (FPart.size < 4) return;

		//- uncrypt this resource if necessary
		if (!doUncrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area)) return ;	

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		WidthHeight = readBEIntFrom(pos);
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
			// TODO Error : ("No area-information available in mapfile!");
			return mapData;
		}

		//- uncrypt this resource if necessary
		if (!doUncrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.Area))
		{
			// TODO Error : error while uncrypting file
			return mapData;	
		}

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		int WidthHeight = readBEIntFrom(pos);
		pos+=4;
		
		//- init size of MapData
		mapData.setWidthHeight(WidthHeight);

		//- points to read
		int dataCount = WidthHeight * WidthHeight;
		
		
		for (int i = 0; i < dataCount; i++)
		{
			mapData.setLandscapeHeight(i, _mapContent[pos++]);
			mapData.setLandscape(i, _mapContent[pos++]);
			mapData.setMapObject(i, _mapContent[pos++]);

			mapData.setPalyerClaim(i, _mapContent[pos++]);

			mapData.setAccessible(i, _mapContent[pos++]);
			mapData.setResources(i, _mapContent[pos++]);
		}

		return mapData;
	}
	
	
	
	//- uncrypt a resource
	private boolean doUncrypt(OriginalMapFileDataStructs.MAP_FILE_PART_TYPE type)
	{
		int PartId = type.value;
		if ((PartId <= 0) || (PartId >= OriginalMapFileDataStructs.MAP_FILE_PART_TYPE.length)) return false;
		
		if (_mapContent==null) return false;
		
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
			if (pos >= _mapContent.length) return false;
				
			//- calculate next Key
			key = ((key << 1) & 0xFF) ^ byt;
		}

		_resources[PartId].hasBeenDecrypted = true;
		
		
		return true;
	}
	
}