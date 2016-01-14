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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapFileVersion;
import jsettlers.logic.map.original.OriginalMapFileDataStructs.EMapStartResources;
import jsettlers.logic.map.save.MapFileHeader;

/**
 * @author Thomas Zeugner
 */
public class OriginalMapFileContentReader {
	// --------------------------------------------------//
	public static class MapResourceInfo {
		OriginalMapFileDataStructs.EMapFilePartType partType;
		public int offset = 0;
		public int size = 0;
		public int cryptKey = 0;
		public boolean hasBeenDecrypted = false;
	}
	// --------------------------------------------------//

	private final List<MapResourceInfo> resources;

	public int fileChecksum = 0;
	public int widthHeight;
	public boolean isSinglePlayerMap = false;
	private boolean hasBuildings = false;

	private byte[] mapContent;
	private OriginalMapFileDataStructs.EMapStartResources startResources = EMapStartResources.HIGH_GOODS;

	private String mapQuestTip = null;
	private String mapQuestText = null;

	public OriginalMapFileContent mapData = new OriginalMapFileContent(0);

	/**
	 * Charset of read strings
	 */
	private static final Charset TEXT_CHARSET = Charset.forName("ISO-8859-1");

	public OriginalMapFileContentReader(InputStream originalMapFile) throws IOException {
		// - init Resource Info
		resources = new LinkedList<MapResourceInfo>();

		// - init players
		mapData.setPlayerCount(1);

		// - read File into buffer
		mapContent = getBytesFromInputStream(originalMapFile);
	}

	// - reads the whole stream and returns it as BYTE-Array
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {

		// - read file to buffer
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;) {
				os.write(buffer, 0, len);
			}
			os.flush();

			return os.toByteArray();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	// - Read UNSIGNED Byte from Buffer
	public int readByteFrom(int offset) {
		if (mapContent == null)
			return 0;
		return mapContent[offset] & 0xFF;
	}

	// - Read Big-Ending INT from Buffer
	public int readBEIntFrom(int offset) {
		if (mapContent == null)
			return 0;
		return ((mapContent[offset] & 0xFF)) |
				((mapContent[offset + 1] & 0xFF) << 8) |
				((mapContent[offset + 2] & 0xFF) << 16) |
				((mapContent[offset + 3] & 0xFF) << 24);
	}

	// - Read Big-Ending 2 Byte Number from Buffer
	public int readBEWordFrom(int offset) {
		if (mapContent == null)
			return 0;
		return ((mapContent[offset] & 0xFF)) |
				((mapContent[offset + 1] & 0xFF) << 8);
	}

	// - read the Higher 4-Bit of the buffer
	public int readHighNibbleFrom(int offset) {
		if (mapContent == null)
			return 0;
		return (mapContent[offset] >> 4) & 0x0F;
	}

	// - read the Lower 4-Bit of the buffer
	public int readLowNibbleFrom(int offset) {
		if (mapContent == null)
			return 0;
		return (mapContent[offset]) & 0x0F;

	}

	// - read a C-Style String from Buffer (ends with the first \0)
	public String readCStrFrom(int offset, int length) {
		if (mapContent == null)
			return "";
		if (mapContent.length <= offset + length)
			return "";

		byte[] buffer = new byte[length];

		int i = 0;
		for (; i < length; i++) {
			buffer[i] = mapContent[offset + i];
			if (buffer[i] == 0) {
				break;
			}
		}
		if (i == 0) {
			return "";
		}

		return new String(buffer, 0, i - 1, TEXT_CHARSET);
	}

	// - returns a File Resources
	private MapResourceInfo findResource(OriginalMapFileDataStructs.EMapFilePartType type) {
		for (MapResourceInfo element : resources) {
			if (element.partType == type)
				return element;
		}

		return null;
	}

	// - calculates the checksum of the file and compares it
	boolean isChecksumValid() {
		// - read Checksum from File
		int fileChecksum = readBEIntFrom(0);

		mapData.fileChecksum = fileChecksum;

		// - make "count" a Multiple of four
		int count = mapContent.length & 0xFFFFFFFC;
		int currentChecksum = 0;

		// - Map Content start at Byte 8
		for (int i = 8; i < count; i += 4) {
			// - read DWord
			int currentInt = readBEIntFrom(i);

			// - using: Logic Right-Shift-Operator: >>>
			currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
		}

		// - return TRUE if the checksum is OK!
		return (currentChecksum == fileChecksum);
	}

	// - Reads in the Map-File-Structure
	boolean loadMapResources() {
		// - Version of File: 0x0A : Original Siedler Map ; 0x0B : Amazon Map
		int fileVersion = readBEIntFrom(4);

		// - check if the Version is compatible?
		if ((fileVersion != EMapFileVersion.DEFAULT.value) && (fileVersion != EMapFileVersion.AMAZONS.value))
			return false;

		// - Data lenght
		int dataLength = mapContent.length;

		// - start of map-content
		int filePos = 8;
		int partTypeTemp;

		do {
			partTypeTemp = readBEIntFrom(filePos);
			int partLen = readBEIntFrom(filePos + 4);

			// - don't know what the [FileTypeSub] is for -> it should by zero
			int partType = (partTypeTemp & 0x0000FFFF);

			// - position/start of data
			int mapPartPos = filePos + 8;

			// - debug output
			// System.out.println("@ "+ filePos +" size: "+ PartLen +" Type:"+ partType +" Sub:"+ PartTypeSub +" -> "+
			// OriginalMapFileDataStructs.EMapFilePartType .getTypeByInt(partType).toString() );

			// - next position in File
			filePos = filePos + partLen;

			// - save the values
			if ((partType > 0) && (partType < OriginalMapFileDataStructs.EMapFilePartType.length) && (partLen >= 0)) {
				MapResourceInfo newRes = new MapResourceInfo();

				newRes.partType = OriginalMapFileDataStructs.EMapFilePartType.getTypeByInt(partType);
				newRes.cryptKey = partType;
				newRes.hasBeenDecrypted = false;
				newRes.offset = mapPartPos;
				newRes.size = partLen - 8;

				resources.add(newRes);
			}

		} while ((partTypeTemp != 0) && ((filePos + 8) <= dataLength));

		return true;
	}

	// - freeing the internal buffers
	public void freeBuffer() {
		// System.out.println("Freeing Buffer.");
		mapContent = null;
		mapData.freeBuffer();
	}

	public void reOpen(InputStream originalMapFile) {
		// - read File into buffer
		try {
			mapContent = getBytesFromInputStream(originalMapFile);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		// - reset Crypt Info

		for (MapResourceInfo element : resources) {
			element.hasBeenDecrypted = false;
		}
	}

	public void readBasicMapInformation() {
		// - Reset
		fileChecksum = 0;
		widthHeight = 0;
		hasBuildings = false;

		// - safety checks
		if (mapContent == null)
			return;
		if (mapContent.length < 100)
			return;

		// - checksum is the first DWord in File
		fileChecksum = readBEIntFrom(0);

		// - read Map Information
		readMapInfo();
		readPlayerInfo();

		readMapQuestText();
		readMapQuestTip();

		// - reset
		widthHeight = 0;

		// - get resource information for the area
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.AREA);

		if (filePart == null)
			return;
		if (filePart.size < 4)
			return;

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return;

		// - file position
		int pos = filePart.offset;

		// - height and width are the same
		widthHeight = readBEIntFrom(pos);
	}

	public String readMapQuestText() {
		if (mapQuestText != null)
			return mapQuestText;

		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.QUEST_TEXT);

		if ((filePart == null) || (filePart.size == 0))
			return "";

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return "";

		// - read Text
		mapQuestText = readCStrFrom(filePart.offset, filePart.size);

		// System.out.println("Quest: "+ mapQuestText);

		return mapQuestText;
	}

	public short[] getPreviewImage(int width, int height)
	{
		short[] outImg = new short[width * height];
		
		// - get resource information for the area
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.PREVIEW);

		if (filePart == null)
			return outImg;
		if (filePart.size < 4)
			return outImg;
		
		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return outImg;
		
		// - file position
		int pos = filePart.offset;

		// - height and width are the same
		int wh = readBEWordFrom(pos);
		pos+=2;
		int unknown = readBEWordFrom(pos);
		pos+=2;
		
		float scale_width = wh / width;
		float scale_height = wh / height;
		
		int out_index = 0;
		int offset = pos;
		
		for (int y = 0; y < height; y++) {
			int src_row =  (int)(Math.floor(scale_height * y)) * wh;
			
			for (int x = 0; x < width; x++){
				
				int in_index = offset + ((src_row + (int)Math.floor(x * scale_width)) * 2);
				
				int colorValue = ((mapContent[in_index] & 0xFF)) | ((mapContent[in_index+1] & 0xFF) << 8);
				
				//- the Settlers Remake uses Short-Colors like argb_1555 (alpha, r, g, b) 
				outImg[out_index] = (short)(1 | colorValue << 1);
				out_index++;
			}
		}

		
		return outImg;
	}
	
	
	public String readMapQuestTip() {

		if (mapQuestTip != null)
			return mapQuestTip;

		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.QUEST_TIP);

		if ((filePart == null) || (filePart.size == 0))
			return "";

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return "";

		// - read Text
		mapQuestTip = readCStrFrom(filePart.offset, filePart.size);

		// System.out.println("Tip: "+ mapQuestTip);

		return mapQuestTip;
	}

	// - Read some common information from the map-file
	public void readMapInfo() {
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.MAP_INFO);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return;

		// - file position
		int pos = filePart.offset;

		// ----------------------------------
		// - read mapType (single / multiplayer map?)
		int mapType = readBEIntFrom(pos);
		pos += 4;

		if (mapType == 1) {
			isSinglePlayerMap = true;
		} else if (mapType == 0) {
			isSinglePlayerMap = false;
		} else {
			System.err.println("wrong value for 'isSinglePlayerMap' " + Integer.toString(mapType) + " in mapfile!");
		}

		// ----------------------------------
		// - read Player count
		int playerCount = readBEIntFrom(pos);
		pos += 4;

		mapData.setPlayerCount(playerCount);

		// ----------------------------------
		// - read start resources
		int startResourcesValue = readBEIntFrom(pos);
		this.startResources = EMapStartResources.fromMapValue(startResourcesValue);
	}

	// - read buildings from the map-file
	public boolean readBuildings() {
		hasBuildings = false;

		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.BUILDINGS);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No Buildings available in mapfile!");
			return false;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return false;

		// - file position
		int pos = filePart.offset;

		// - Number of buildings
		int buildingsCount = readBEIntFrom(pos);
		pos += 4;

		// - safety check
		if ((buildingsCount * 12 > filePart.size) || (buildingsCount < 0)) {
			System.err.println("wrong number of buildings in map File: " + buildingsCount);
			return false;
		}

		hasBuildings = true;

		// - read all Buildings
		for (int i = 0; i < buildingsCount; i++) {

			int party = readByteFrom(pos++); // - Party starts with 0
			int buildingType = readByteFrom(pos++);
			int posX = readBEWordFrom(pos);
			pos += 2;
			int posY = readBEWordFrom(pos);
			pos += 2;

			pos++; // not used - maybe a filling byte to make the record 12 Byte (= 3 INTs) long or unknown?!

			// -----------
			// - number of soldier in building is saved as 4-Bit (=Nibble):
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
			// low nibble is a not used count
			pos++;

			int countSpear1 = readHighNibbleFrom(pos);
			int countSpear2 = readLowNibbleFrom(pos);
			pos++;

			// -------------
			// - update data
			mapData.setBuilding(posX, posY, buildingType, party, countSword1, countSword2, countSword3, countArcher1, countArcher2, countArcher3,
					countSpear1, countSpear2, countSpear3);
		}

		return true;
	}

	// - Read stacks from the map-file
	public boolean readStacks() {
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.STACKS);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No Stacks available in mapfile!");
			return false;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return false;

		// - file position
		int pos = filePart.offset;

		// - Number of buildings
		int stackCount = readBEIntFrom(pos);
		pos += 4;

		// - safety check
		if ((stackCount * 8 > filePart.size) || (stackCount < 0)) {
			System.err.println("wrong number of stacks in map File: " + stackCount);
			return false;
		}

		// - read all Stacks
		for (int i = 0; i < stackCount; i++) {

			int posX = readBEWordFrom(pos);
			pos += 2;
			int posY = readBEWordFrom(pos);
			pos += 2;

			int stackType = readByteFrom(pos++);
			int count = readByteFrom(pos++);

			pos += 2; // not used - maybe: padding to size of 8 (2 INTs)

			// -------------
			// - update data
			mapData.setStack(posX, posY, stackType, count);
		}

		return true;
	}

	// - Read settlers from the map-file
	public boolean readSettlers() {
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.SETTLERS);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No Settlers available in mapfile!");
			return false;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return false;

		// - file position
		int pos = filePart.offset;

		// - Number of buildings
		int settlerCount = readBEIntFrom(pos);
		pos += 4;

		// - safety check
		if ((settlerCount * 6 > filePart.size) || (settlerCount < 0)) {
			System.err.println("wrong number of settlers in map File: " + settlerCount);
			return false;
		}

		// - read all Stacks
		for (int i = 0; i < settlerCount; i++) {

			int party = readByteFrom(pos++);
			int settlerType = readByteFrom(pos++);

			int posX = readBEWordFrom(pos);
			pos += 2;
			int posY = readBEWordFrom(pos);
			pos += 2;

			// -------------
			// - update data
			mapData.setSettler(posX, posY, settlerType, party);
		}

		return true;
	}

	// - Read the Player Info
	public void readPlayerInfo() {
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.PLAYER_INFO);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No Player information available in mapfile!");
			return;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return;

		// - file position
		int pos = filePart.offset;

		for (int i = 0; i < mapData.getPlayerCount(); i++) {

			int nation = readBEIntFrom(pos);
			pos += 4;

			int startX = readBEIntFrom(pos);
			pos += 4;

			int startY = readBEIntFrom(pos);
			pos += 4;

			String playerName = readCStrFrom(pos, 33);
			pos += 33;

			mapData.setPlayer(i, startX, startY, nation, playerName);

		}
	}

	// - Reads in the Map Data / Landscape and MapObjects like trees
	public boolean readMapData() {
		// - get resource information for the area
		MapResourceInfo filePart = findResource(OriginalMapFileDataStructs.EMapFilePartType.AREA);

		if ((filePart == null) || (filePart.size == 0)) {
			System.err.println("Warning: No area information available in mapfile!");
			return false;
		}

		// - Decrypt this resource if necessary
		if (!doDecrypt(filePart))
			return false;

		// - file position
		int pos = filePart.offset;

		// - height and width are the same
		int widthHeight = readBEIntFrom(pos);
		pos += 4;

		// - init size of MapData
		mapData.setWidthHeight(widthHeight);

		// - points to read
		int dataCount = widthHeight * widthHeight;

		for (int i = 0; i < dataCount; i++) {
			mapData.setLandscapeHeight(i, readByteFrom(pos++));
			mapData.setLandscape(i, readByteFrom(pos++));
			mapData.setMapObject(i, readByteFrom(pos++));
			readByteFrom(pos++); // - which Player is the owner of this position
			mapData.setAccessible(i, mapContent[pos++]);

			mapData.setResources(i, readHighNibbleFrom(pos), readLowNibbleFrom(pos));
			pos++;
		}

		return true;
	}

	public void addStartTowerMaterialsAndSettlers() {
		// - only if there are no buildings
		if (hasBuildings)
			return;

		int playerCount = mapData.getPlayerCount();

		for (byte playerId = 0; playerId < playerCount; playerId++) {
			ShortPoint2D startPoint = mapData.getStartPoint(playerId);

			// - add the start Tower for this player
			mapData.setMapObject(startPoint.x, startPoint.y, new BuildingObject(EBuildingType.TOWER, playerId));

			// - list of all objects that have to be added for this player
			List<MapObject> mapObjects = EMapStartResources.generateStackObjects(startResources);
			mapObjects.addAll(EMapStartResources.generateMovableObjects(startResources, playerId));

			// - blocking area of the tower
			List<RelativePoint> towerTiles = Arrays.asList(EBuildingType.TOWER.getProtectedTiles());

			RelativePoint relativeMapObjectPoint = new RelativePoint(-3, 3);

			for (MapObject currentMapObject : mapObjects) {
				do {
					// - get next point
					relativeMapObjectPoint = nextPointOnSpiral(relativeMapObjectPoint);

					// - don't put things under the tower
					if (towerTiles.contains(relativeMapObjectPoint))
						continue;

					// - get absolute position
					int x = relativeMapObjectPoint.calculateX(startPoint.x);
					int y = relativeMapObjectPoint.calculateY(startPoint.y);

					// - is this place free?
					if (mapData.getMapObject(x, y) == null) {
						// - add Object
						mapData.setMapObject(x, y, currentMapObject);
						// - break DO: next object...
						break;
					}
				} while (true);
			}
		}
	}

	private RelativePoint nextPointOnSpiral(RelativePoint previousPoint) {
		short previousX = previousPoint.getDx();
		short previousY = previousPoint.getDy();

		short basis = (short) Math.max(Math.abs(previousX), Math.abs(previousY));

		if (previousX == basis && previousY > -basis)
			return new RelativePoint(previousX, previousY - 1);
		if (previousX == -basis && previousY <= basis)
			return new RelativePoint(previousX, previousY + 1);
		if (previousX < basis && previousY == basis)
			return new RelativePoint(previousX + 1, previousY);
		if (previousX > -basis && previousY == -basis)
			return new RelativePoint(previousX - 1, previousY);

		return null;
	}

	// - Decrypt a resource
	private boolean doDecrypt(MapResourceInfo filePart) {

		if (filePart == null)
			return false;

		if (mapContent == null) {
			System.err.println("OriginalMapFile-Warning: Unable to decrypt map file: no data loaded!");
			return false;
		}

		// - already encrypted
		if (filePart.hasBeenDecrypted)
			return true;

		// - length of data
		int length = filePart.size;
		if (length <= 0)
			return true;

		// - start of data
		int pos = filePart.offset;

		// - init the key
		int key = (filePart.cryptKey & 0xFF);

		for (int i = length; i > 0; i--) {
			// - read one byte
			int byt = (mapContent[pos] ^ key) & 0xFF;

			// - write Byte
			mapContent[pos] = (byte) byt;

			// - next position/byte
			pos++;
			if (pos >= mapContent.length) {
				System.err.println("Error: Unable to decrypt map file: unexpected eof!");
				return false;
			}

			// - calculate next Key
			key = ((key << 1) & 0xFF) ^ byt;
		}

		filePart.hasBeenDecrypted = true;
		return true;
	}

}