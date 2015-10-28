package jsettlers.logic.map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import jsettlers.logic.map.save.loader.MapLoader;

import java.io.*;
import java.util.Vector;


/**
 * @author codingberlin
 */
public class OriginalMapLoader {
	public MapLoader loadOriginalMap(File originalMapFile) {
		try {
			OriginalMapFileContent mapContent = new OriginalMapFileContent(originalMapFile);

			if (!mapContent.isChecksumValid()) {
				System.out.println("Checksum of original map was not valid!");
				return null;
			}
			
			mapContent.loadMapResources();
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	enum MapFilePartsTypes
	{
		T_EOF (0,""), // End of File and Padding
		T_MapInfo (1,"Map Info"),
		T_PlayerInfo (2,"Player Info"),
		T_TeamInfo (3,"Team Info"),
		T_Preview (4,"Preview"),
		T_Area (6,"Area"),
		T_Settlers (7,"Settlers"),
		T_Buildings (8,"Buildings"),
		T_Resources (9,"Resources"),
		T_QuestText (11,"QuestText"),
		T_QuestTip (12,"QuestTip");
		
		
		public static final int length = 13;
		
		public final int typeValue;
		private final String typeText;
		
		MapFilePartsTypes(int typeValue, String typeText)
		{
	        this.typeValue = typeValue;
	        this.typeText = typeText;
	    }
		
		public String toString()
		{
			return typeText;
		}
		
		public static MapFilePartsTypes getTypeByInt(int intType)
		{
			int val = intType & 0x0000FFFF;
			if (val <= 0) return T_EOF;
			if (val >= length) return T_EOF;
			
			return MapFilePartsTypes.values()[val];
		}
	}
	
	public class MapResourceInfo
	{
		public int offset=0;
		public int size=0;
		public int cryptKey=0;
		public boolean hasBeenDecrypted = false;
	}
	
	public class OriginalMapFileContent
	{
		byte[] mapContent;
		int _fileChecksum = 0;
		int _fileVersion = 0;
		MapResourceInfo[] _resources;
		
		
		public OriginalMapFileContent(File originalMapFile) throws IOException {
			
			//- init Resource Info
			_resources = new MapResourceInfo[MapFilePartsTypes.length];
			for(int i=0; i<_resources.length;i++) _resources[i] = new MapResourceInfo();
			
			//- read File into buffer
			mapContent = java.nio.file.Files.readAllBytes(originalMapFile.toPath());
			
			
			/*
			DataInputStream dis = new DataInputStream(new FileInputStream(originalMapFile));
					
			while (dis.available() > 0) {
				byte currentByte = dis.readUnsignedByte();
				mapContent.add(currentByte);
			}
			dis.close();
			
			
			this.mapContent = new byte[mapContent.size()];
			for (int i = 0; i < mapContent.size(); i++) {
				this.mapContent[i] = mapContent.get(i);
			}
			*/
		}

		//- Read Big-Ending INT from Buffer
		public int readBEIntFrom(int numberOfBytes, int offset) {
			int result = 0;
			for (int i = 0; i < numberOfBytes; i++)
			{
				//-                     ( signed byte 2 unsigned int )
				result = result | ((int)( mapContent[i+offset] & 0xFF) << (i << 3));
			}
			return result;
		}
		
		//- Read Big-Ending INT from Buffer
		public int readBEIntFrom(int offset)
		{
			int result;

			result  = (mapContent[offset  ] & 0xFF) << (0 << 3);
			result += (mapContent[offset+1] & 0xFF) << (1 << 3);
			result += (mapContent[offset+2] & 0xFF) << (2 << 3);
			result += (mapContent[offset+3] & 0xFF) << (3 << 3);
			
			return result;
		}
		
		boolean isChecksumValid()
		{
			//- read Checksum from File
			int fileChecksum = readBEIntFrom(0);
			
			int count = mapContent.length;
			int currentChecksum = 0;
			
			//- Map Content start at Byte 8
			for (int i = 8; i < count ; i+=4)
			{
				//- read DWord
				int currentInt = readBEIntFrom(i);
				
				//- using: Logic Right-Shift-Operator: >>>
				currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
			}
			
			_fileChecksum = currentChecksum;
			
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
			int DataLenght = mapContent.length;
			
			int MapPartType = 0;
			int MapPartLen = 0;

			//- start of map-content
			int FilePos = 8; 

			do
			{
				MapPartType = readBEIntFrom(FilePos);
				MapPartLen = readBEIntFrom(FilePos + 4);


				//- uncrypt
				MapPartLen = MapPartLen + 0xFFFFFFF8;

				int FileTypeSub = (MapPartType << 16) & 0x0000FFFF;
				MapPartType = MapPartType & 0x0000FFFF;

				int MapPartPos = FilePos + 8;

				//- debug output
				System.out.println("@ "+ FilePos +"   size: "+ MapPartLen +"  Type:"+ MapPartType +" -> "+  MapFilePartsTypes.getTypeByInt(MapPartType).toString());

						
				//- next pos in File
				FilePos = FilePos + MapPartLen + 8;

				//- save the values
				if ((MapPartType > 0) && (MapPartType < _resources.length))
				{
					_resources[MapPartType].cryptKey = MapPartType;
					_resources[MapPartType].hasBeenDecrypted = false;
					_resources[MapPartType].offset = MapPartPos;
					_resources[MapPartType].size = MapPartLen;
				}

			}
			while ((MapPartType != 0) && (FilePos < DataLenght));

			return true;
		}

	}

	public static void main(String[] args) {
		(new OriginalMapLoader()).loadOriginalMap(new File("D:\\Spiele\\Siedler3\\Map\\User\\a.map"));
	}

}
