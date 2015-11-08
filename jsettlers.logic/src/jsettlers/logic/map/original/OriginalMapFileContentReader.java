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
import jsettlers.logic.map.original.OriginalMapFileContentReader.MapResourceInfo;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapStartResources;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContentReader
{
	//--------------------------------------------------//
	public static class MapResourceInfo {
		OriginalMapFileDataStructs.EMapFilePartType PartType;
		public int offset=0;
		public int size=0;
		public int cryptKey=0;
		public boolean hasBeenDecrypted = false;
	}
	//--------------------------------------------------//

	private final List<MapResourceInfo> resources;

	public int fileChecksum = 0;
	public int widthHeight;
	public boolean isSinglePlayerMap = false;
	
	private byte[] mapContent;
	private int fileVersion = 0;
	private OriginalMapFileDataStructs.EMapStartResources startResources = EMapStartResources.HIGH_GOODS;
	public OriginalMapFileContent.MapPlayerInfo[] players;

	public OriginalMapFileContent mapData = new OriginalMapFileContent(0);
	
	
	public OriginalMapFileContentReader(InputStream originalMapFile) throws IOException {
		//- init Resource Info
		resources = new LinkedList<MapResourceInfo>();
		
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

	
	//- Read UNSIGNED Byte from Buffer
	public int readByteFrom(int offset) {
		if (mapContent == null) return 0;
		return  mapContent[offset] & 0xFF;
	}
	
	
	//- Read Big-Ending INT from Buffer
	public int readBEIntFrom(int offset) {
		if (mapContent == null) return 0;
		return  ((mapContent[offset  ] & 0xFF) << 0) |
				((mapContent[offset+1] & 0xFF) << 8) |
				((mapContent[offset+2] & 0xFF) << 16) |
				((mapContent[offset+3] & 0xFF) << 24);
	}
	
	//- Read Big-Ending 2 Byte Number from Buffer
	public int readBEWordFrom(int offset) {
		if (mapContent == null) return 0;
		return  ((mapContent[offset  ] & 0xFF) << 0) |
				((mapContent[offset+1] & 0xFF) << 8);
	}
	
	//- read the Higher 4-Bit of the buffer
	public int readHighNibbleFrom(int offset) {
		if (mapContent == null) return 0;
		return (mapContent[offset] << 4) & 0x0F;
	}
	
	//- read the Lower 4-Bit of the buffer
	public int readLowNibbleFrom(int offset) {
		if (mapContent == null) return 0;
		return (mapContent[offset]) & 0x0F;
		
	}

	//- read a C-Style String from Buffer (ends with the first \0)
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
	
	//- returns a File Resources
	private MapResourceInfo findResource(OriginalMapFileDataStructs.EMapFilePartType type)
	{
		Iterator<MapResourceInfo> iterator = resources.iterator();
		
		while(iterator.hasNext()){
			MapResourceInfo element = (MapResourceInfo) iterator.next();
			
			if (element.PartType == type) return element;
		}
		
		System.err.println("Error: findResource("+ type +") failed!");
		
		return null;
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
			if ((PartType > 0) && (PartType < OriginalMapFileDataStructs.EMapFilePartType.length) && (PartLen>=0)) {
				MapResourceInfo newRes = new MapResourceInfo();
				
				newRes.PartType = OriginalMapFileDataStructs.EMapFilePartType.getTypeByInt(PartType);
				newRes.cryptKey = PartType;
				newRes.hasBeenDecrypted = false;
				newRes.offset = MapPartPos;
				newRes.size = PartLen - 8;
				
				resources.add(newRes);
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
		MapResourceInfo FPart = findResource(OriginalMapFileDataStructs.EMapFilePartType.AREA);
		if (FPart==null) return;
		if (FPart.size < 4) return;

		//- uncrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.AREA)) return;

		//- file position
		int pos = FPart.offset;
		
		//- height and width are the same
		widthHeight = readBEIntFrom(pos);
	}
	
	

	
	
	//- Read some common information from the map-file
	public void readMapInfo() {
		MapResourceInfo FPart = findResource(OriginalMapFileDataStructs.EMapFilePartType.MAP_INFO);
		
		if ((FPart==null) || (FPart.size == 0)) {
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.MAP_INFO)) return;

		//- file position
		int pos = FPart.offset;
		
		//----------------------------------
		//- read mapType (single / multiplayer map?)
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
		//- read Player count
		int PlayerCount = readBEIntFrom(pos);
		pos += 4;
		
		players = new MapPlayerInfo[PlayerCount];
		
		for (int i=0; i<PlayerCount; i++) {
			players[i] = new MapPlayerInfo(20+i*10,20+i*10, Integer.toString(i) , OriginalMapFileDataStructs.EMapNations.ROMANS);
		}
		
		
		//----------------------------------
		//- read start resources
		int StartResourcesValue = readBEIntFrom(pos);
		pos += 4;
		
		this.startResources = EMapStartResources.FromMapValue(StartResourcesValue);
	}

	
	//- Read some common information from the map-file
	public boolean readBuildings() {
		MapResourceInfo FPart = findResource(OriginalMapFileDataStructs.EMapFilePartType.BUILDINGS);
		
		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.BUILDINGS)) return false;

		//- file position
		int pos = FPart.offset;
		
		//- Number of buildings
		int BuildinsCount = readBEIntFrom(pos);
		pos += 4;
		
		//- safety check
		if ((BuildinsCount * 12 > FPart.size) || (BuildinsCount < 0)) {
			System.err.println("wrong number of buildings in map File: "+ BuildinsCount);
		    BuildinsCount = 0;
		    return false;
		}
		
		
		//- read all Buildings
		for (int i = 0 ; i < BuildinsCount; i++) {
		 
	        int party = readByteFrom(pos++); //- Party starts with 0
	        int BType = readByteFrom(pos++);
	        int x_pos = readBEWordFrom(pos);  pos+=2;
	        int y_pos = readBEWordFrom(pos);  pos+=2;
	        
	        //- maybe a filling byte to make the record 12 Byte (= 3 INTs) long or unknown?!
	        int notUsed = readByteFrom(pos++);
	        
	        //-----------
			//- number of soldier in building is saved as 4-Bit (=Nibble):
			int countSword1 = readHighNibbleFrom(pos);
			int countSword2 = readLowNibbleFrom(pos);
			pos++;
			
			int countArcher2 = readHighNibbleFrom(pos);
			int countArcher3 = readLowNibbleFrom(pos);
			pos++;
			
			int countSword3 = readHighNibbleFrom(pos);
			int countArcher1 = readLowNibbleFrom(pos);
			pos++;
			
			int countSpear3 = readHighNibbleFrom(pos);
			int countNotUsed = readLowNibbleFrom(pos);
			pos++;
			
			int countSpear1 = readHighNibbleFrom(pos);
			int countSpear2 = readLowNibbleFrom(pos);
			pos++;

			//-------------
	        //- update data                              
			mapData.setBuilding(x_pos, y_pos, BType, party, countSword1, countSword2, countSword3, countArcher1, countArcher2, countArcher3, countSpear1, countSpear2, countSpear3);
		}
		
		return true;
	}
	
	
	//- Read the Player Info
	public void readPlayerInfo() {
		MapResourceInfo FPart = findResource(OriginalMapFileDataStructs.EMapFilePartType.PLAYER_INFO);
		
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
	public boolean readMapData() {
		
		
		mapData.fileChecksum = fileChecksum;
		
		//- get resource information for the area 
		MapResourceInfo FPart = findResource(OriginalMapFileDataStructs.EMapFilePartType.AREA);
		if (FPart==null) return false;
		
		if (FPart.size == 0) {
			System.err.println("Warning: No area information available in mapfile!");
			return false;
		}

		//- Decrypt this resource if necessary
		if (!doDecrypt(OriginalMapFileDataStructs.EMapFilePartType.AREA)) return false;

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
			mapData.setLandscapeHeight(i, readByteFrom(pos++));
			mapData.setLandscape(i, readByteFrom(pos++));
			mapData.setMapObject(i, readByteFrom(pos++));
			mapData.setPalyerClaim(i, mapContent[pos++]);
			mapData.setAccessible(i, mapContent[pos++]);
			mapData.setResources(i, mapContent[pos++]);
		}
		
		//- add palyers
		mapData.setMapPlayerInfos(this.players, startResources);

		return true;
	}


	
	//- uncrypt a resource
	private boolean doDecrypt(OriginalMapFileDataStructs.EMapFilePartType type) {
		
		MapResourceInfo FPart = findResource(type);
		if (FPart == null) return false;
		
		if (mapContent == null) {
			System.err.println("Warning: Unable to decrypt map file: no data loaded!");
			return false;
		}
		
		//- already encrypted
		if (FPart.hasBeenDecrypted) return true;
		
		//- length of data
		int length = FPart.size;
		if (length <= 0) return true;
		
		//- start of data
		int pos = FPart.offset;
		
		//- init the key
		int key = (FPart.cryptKey & 0xFF);
		
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

		FPart.hasBeenDecrypted = true;
		return true;
	}

	public EMapStartResources getStartResources() {
		return startResources;
	}
}