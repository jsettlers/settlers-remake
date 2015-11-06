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

import java.io.*;

import jsettlers.logic.map.original.OriginalMapFileContent.MapPlayerInfo;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapStartResources;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContentReader
{
	//--------------------------------------------------//
	public static class MapResourceInfo {
		public int offset=0;
		public int size=0;
		public int cryptKey=0;
		public boolean hasBeenDecrypted = false;
	}
	//--------------------------------------------------//

	private final MapResourceInfo[] resources;

	public int fileChecksum = 0;
	public int widthHeight;
	public boolean isSinglePlayerMap = false;
	
	private byte[] mapContent;
	private int fileVersion = 0;
	private OriginalMapFileDataStructs.EMapStartResources startResources = EMapStartResources.HIGH_GOODS;
	public OriginalMapFileContent.MapPlayerInfo[] players;

	public OriginalMapFileContentReader(InputStream originalMapFile) throws IOException {
		//- init Resource Info
		resources = new MapResourceInfo[OriginalMapFileDataStructs.EMapFilePartType.length];
		for(int i=0; i< resources.length;i++) resources[i] = new MapResourceInfo();
		
		//- init players
		players = new MapPlayerInfo[1];
		players[0] = new MapPlayerInfo(100,100,"", OriginalMapFileDataStructs.EMapNations.ROMANS);

		//- read File into buffer
		mapContent = getBytesFromInputStream(originalMapFile);
	}



	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
	    try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
	        byte[] buffer = new byte[0xFFFF];
	
	        for (int len; (len = is.read(buffer)) != -1;) {
				os.write(buffer, 0, len);
			}
	        os.flush();
	
	        return os.toByteArray();
	    }
	    catch (Exception e) {
	    	return new byte[0];
	    }
	}

	//- Read Big-Ending INT from Buffer
	public int readBEIntFrom(int numberOfBytes, int offset) {
		int result = 0;
		for (int i = 0; i < numberOfBytes; i++) {
			//-                     ( signed byte 2 unsigned int )
			result = result | ((int)( mapContent[i+offset] & 0xFF) << (i << 3));
		}
		return result;
	}
	
	
	//- Read Big-Ending INT from Buffer
	public int readBEIntFrom(int offset) {
		if (mapContent == null) return 0;
		return  ((mapContent[offset  ] & 0xFF) << 0) |
				((mapContent[offset+1] & 0xFF) << 8) |
				((mapContent[offset+2] & 0xFF) << 16) |
				((mapContent[offset+3] & 0xFF) << 24);
	}
	
	
	public String readCStrFrom(int offset, int length) {
		if (mapContent == null) return "";
		if (mapContent.length <= offset + length) return "";
		
		String outStr = "";
		int pos = offset;
		
		for (int i=length; i>0; i--) {
			byte b = mapContent[pos];
			pos++;
			
			if (b==0) break;
			
			outStr += new String(new byte[] {b}); 
		}
		return outStr;
	}
	
	
	boolean isChecksumValid() {
		//- read Checksum from File
		int fileChecksum = readBEIntFrom(0);
		
		//- make count a Multiple of four
		int count = mapContent.length & 0xFFFFFFFC;
		int currentChecksum = 0;
		
		//- Map Content start at Byte 8
		for (int i = 8; i < count ; i+=4) {
			//- read DWord
			int currentInt = readBEIntFrom(i);
			
			//- using: Logic Right-Shift-Operator: >>>
			currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
		}

		//- return TRUE if the checksum is OK!
		return (currentChecksum == fileChecksum);
	}
	
	
	//- Reads in the Map-File-Structure
	boolean loadMapResources() {
		//- Version of File:  0x0A : Original Siedler Map ; 0x0B : Amazon Map
		fileVersion = readBEIntFrom(4);
		
		//- check if the Version is compatible?
		if ((fileVersion != 0x0A) && (fileVersion != 0x0B)) return false;
		
		//- Data lenght
		int DataLenght = mapContent.length;

		//- start of map-content
		int FilePos = 8; 
		int PartTypeTemp = 0;
		
		do {
			PartTypeTemp = readBEIntFrom(FilePos);
			int PartLen = readBEIntFrom(FilePos + 4);

			//- don't know what the [FileTypeSub] is for -> it should by zero
			int PartTypeSub = (PartTypeTemp << 16) & 0x0000FFFF;
			int PartType = (PartTypeTemp & 0x0000FFFF);

			//- position/start of data
			int MapPartPos = FilePos + 8;

			//- debug output
			System.out.println("@ "+ FilePos +"   size: "+ PartLen +"  Type:"+ PartType +"  Sub:"+ PartTypeSub +"  -> "+  OriginalMapFileDataStructs.EMapFilePartType
					.getTypeByInt(PartType).toString() );

			//- next position in File
			FilePos = FilePos + PartLen;

			//- save the values
			if ((PartType > 0) && (PartType < resources.length) && (PartLen>=0)) {
				resources[PartType].cryptKey = PartType;
				resources[PartType].hasBeenDecrypted = false;
				resources[PartType].offset = MapPartPos;
				resources[PartType].size = PartLen - 8;
			}

		}
		while ((PartTypeTemp != 0) && ((FilePos+8) <= DataLenght));

		return true;
	}

	public void readBasicMapInformation() {
		//- Reset
		fileChecksum = 0;
		widthHeight =0;
		
		//- safety checks
		if (mapContent ==null) return;
		if (mapContent.length < 100) return;
		
		//- checksum is the first DWord in File
		fileChecksum = readBEIntFrom(0);
		
		//- read Map Information
		readMapInfo();
		readPlayerInfo();

		//- reset
		widthHeight = 0;
	
		//- get resource information for the area 
		MapResourceInfo FPart = resources[OriginalMapFileDataStructs.EMapFilePartType.AREA.value];
		if (FPart==null) return;
		if (FPart.size < 4) return;

		//- uncrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.AREA)) return;

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		widthHeight = readBEIntFrom(pos);
	}
	
	
	
	public void readMapInfo() {
		MapResourceInfo FPart = resources[OriginalMapFileDataStructs.EMapFilePartType.MAP_INFO.value];
		
		if ((FPart==null) || (FPart.size == 0)) {
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.MAP_INFO)) return;

		//- file position
		int pos = FPart.offset;
		
		//----------------------------------
		int MapType = readBEIntFrom(pos);
		pos += 4;
			  
		if (MapType == 1) {
			isSinglePlayerMap = true;
		} else if (MapType == 0) {
			isSinglePlayerMap = false;
		} else {
			System.err.println("wrong value for 'isSinglePlayerMap' "+ Integer.toString(MapType) +" in mapfile!");
		}

		//----------------------------------  
		int PlayerCount = readBEIntFrom(pos);
		pos += 4;
		
		players = new MapPlayerInfo[PlayerCount];
		
		for (int i=0; i<PlayerCount; i++) {
			players[i] = new MapPlayerInfo(20+i*10,20+i*10, Integer.toString(i) , OriginalMapFileDataStructs.EMapNations.ROMANS);
		}
		
		
		//----------------------------------
		int StartResourcesValue = readBEIntFrom(pos);
		pos += 4;
		
		this.startResources = EMapStartResources.FromMapValue(StartResourcesValue);
	}

	//- Read the Player Info
	public void readPlayerInfo() {
		MapResourceInfo FPart = resources[OriginalMapFileDataStructs.EMapFilePartType.PLAYER_INFO.value];
		
		if ((FPart==null) || (FPart.size == 0)) {
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.PLAYER_INFO)) return;

		//- file position
		int pos = FPart.offset;
		
		for (int i = 0; i < players.length; i++) {
			players[i].nation = OriginalMapFileDataStructs.EMapNations.FromMapValue(readBEIntFrom(pos));
			pos += 4;
			
			players[i].startX = readBEIntFrom(pos);
			pos += 4;
			
			players[i].startY = readBEIntFrom(pos);
			pos += 4;
			
			players[i].playerName = readCStrFrom(pos, 33);
			pos += 33;
			
			System.out.println("Player "+ Integer.toString(i) +" : "+ players[i].playerName +" @ ("+ players[i].startX +" , "+ players[i].startY +")");
		}
	}
	
	
	
	//- Reads in the Map Data and returns a IMapData
	public OriginalMapFileContent readMapData() {
		OriginalMapFileContent mapData = new OriginalMapFileContent(0);
		
		mapData.fileChecksum = fileChecksum;
		
		//- get resource information for the area 
		MapResourceInfo FPart = resources[OriginalMapFileDataStructs.EMapFilePartType.AREA.value];
		if (FPart==null) return mapData;
		
		if (FPart.size == 0) {
			System.err.println("Warning: No area information available in mapfile!");
			return mapData;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.AREA)) return mapData;

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		int WidthHeight = readBEIntFrom(pos);
		pos+=4;
		
		//- init size of MapData
		mapData.setWidthHeight(WidthHeight);

		//- points to read
		int dataCount = WidthHeight * WidthHeight;
		
		for (int i=0; i< dataCount; i++) {
		//for (int y = 0; y < widthHeight; y++)
		//{
		//	for (int x = 0; x < widthHeight; x++)
		//	{
		//		int i = x + (widthHeight * (widthHeight - y - 1));
				
				mapData.setLandscapeHeight(i, mapContent[pos++]);
				mapData.setLandscape(i, transformToUnsignedByte(mapContent[pos++]));
				mapData.setMapObject(i, transformToUnsignedByte(mapContent[pos++]));
				mapData.setPalyerClaim(i, mapContent[pos++]);
				mapData.setAccessible(i, mapContent[pos++]);
				mapData.setResources(i, mapContent[pos++]);
		//	}
		//}
		}
		
		//- add palyers
		mapData.setMapPlayerInfos(this.players, startResources);

		return mapData;
	}

	short transformToUnsignedByte(byte signedByte) {
		if (signedByte < 0) {
			return (short) (signedByte & 0xFF);
		} else {
			return signedByte;
		}
	}
	
	//- uncrypt a resource
	private boolean doDecrypt(OriginalMapFileDataStructs.EMapFilePartType type) {
		int PartId = type.value;
		if ((PartId <= 0) || (PartId >= OriginalMapFileDataStructs.EMapFilePartType.length)) return false;
		
		if (mapContent ==null) {
			System.err.println("Warning: Unable to decrypt map file: no data loaded!");
			return false;
		}
		
		//- already encrypted
		if (resources[PartId].hasBeenDecrypted) return true;
		
		//- length of data
		int length = resources[PartId].size;
		if (length <= 0) return true;
		
		//- start of data
		int pos = resources[PartId].offset;
		
		//- init the key
		int key = (resources[PartId].cryptKey & 0xFF);
		
		for (int i = length; i > 0; i--) {
			//- read one byte
			int byt = (mapContent[pos] ^ key) & 0xFF;

			//- write Byte
			mapContent[pos] =(byte)byt;
			
			//- next position/byte
			pos++;
			if (pos >= mapContent.length) {
				System.err.println("Error: Unable to decrypt map file: unexpected eof!");
				return false;
			}
				
			//- calculate next Key
			key = ((key << 1) & 0xFF) ^ byt;
		}

		resources[PartId].hasBeenDecrypted = true;
		return true;
	}

	public EMapStartResources getStartResources() {
		return startResources;
	}
}