/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.loading.original;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import jsettlers.common.Color;
import java8.util.Optional;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.original.data.EOriginalMapFilePartType;
import jsettlers.logic.map.loading.original.data.EOriginalMapFileVersion;
import jsettlers.logic.player.PlayerSetting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Thomas Zeugner
 */
class OriginalMapFileContentReader {
	private class MapResourceInfo {
		final EOriginalMapFilePartType partType;
		public final int offset;
		public final int size;
		final int cryptKey;
		private boolean hasBeenDecrypted = false;

		MapResourceInfo(EOriginalMapFilePartType partType, int offset, int size, int cryptKey) {
			this.partType = partType;
			this.offset = offset;
			this.size = size;
			this.cryptKey = cryptKey;
		}

		// - Decrypt a file resource
		private boolean doDecrypt() throws MapLoadException {
			if (mapContent == null) {
				throw new MapLoadException("OriginalMapFile-Warning: Unable to decrypt map file: no data loaded!");
			}

			// - already decrypted
			if (hasBeenDecrypted || size <= 0) {
				return true;
			}

			// - start of data
			int pos = offset;

			// - check if the file has enough data
			if ((pos + size) >= mapContent.length) {
				throw new MapLoadException("Error: Unable to decrypt map file: out of data!");
			}

			// - init the key
			int key = (cryptKey & 0xFF);

			for (int i = size; i > 0; i--) {

				// - read one byte and uncrypt it
				int byt = (mapContent[pos] ^ key);

				// - calculate next Key
				key = (key << 1) ^ byt;

				// - write Byte
				mapContent[pos] = (byte) byt;
				pos++;
			}

			hasBeenDecrypted = true;
			return true;
		}

		void resetDecryptedFlag() {
			hasBeenDecrypted = false;
		}
	}

	private final EnumMap<EOriginalMapFilePartType, MapResourceInfo> resources = new EnumMap<>(EOriginalMapFilePartType.class);

	private int fileChecksum = 0;
	int widthHeight;
	private boolean isSinglePlayerMap = false;
	private boolean hasBuildings = false;

	private byte[] mapContent;
	@SuppressWarnings("unused")
	private EMapStartResources startResources = EMapStartResources.HIGH_GOODS;

	private String mapQuestTip = null;
	private String mapQuestText = null;

	private short[] previewImage = null;
	private short previewWidth = 0;
	private short previewHeight = 0;

	OriginalMapFileContent mapData = new OriginalMapFileContent(0);

	/**
	 * Charset of read strings
	 */
	private static final Charset TEXT_CHARSET = Charset.forName("ISO-8859-1");

	OriginalMapFileContentReader(InputStream originalMapFile) throws IOException {
		// - init players
		mapData.setPlayerCount(1);

		// - read File into buffer
		mapContent = getBytesFromInputStream(originalMapFile);
	}

	// - reads the whole stream and returns it as BYTE-Array
	private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		// - read file to buffer
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1; ) {
				os.write(buffer, 0, len);
			}
			os.flush();

			return os.toByteArray();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	// - Read UNSIGNED Byte from Buffer
	private int readByteFrom(int offset) {
		if (mapContent == null)
			return 0;
		return mapContent[offset] & 0xFF;
	}

	// - Read Big-Ending INT from Buffer
	private int readBEIntFrom(int offset) {
		if (mapContent == null) {
			return 0;
		} else {
			return (mapContent[offset] & 0xFF) |
					((mapContent[offset + 1] & 0xFF) << 8) |
					((mapContent[offset + 2] & 0xFF) << 16) |
					((mapContent[offset + 3] & 0xFF) << 24);
		}
	}

	// - Read Big-Ending 2 Byte Number from Buffer
	private int readBEWordFrom(int offset) {
		if (mapContent == null) {
			return 0;
		} else {
			return (mapContent[offset] & 0xFF) |
					((mapContent[offset + 1] & 0xFF) << 8);
		}
	}

	// - read the Higher 4-Bit of the buffer
	private int readHighNibbleFrom(int offset) {
		if (mapContent == null) {
			return 0;
		} else {
			return (mapContent[offset] >> 4) & 0x0F;
		}
	}

	// - read the Lower 4-Bit of the buffer
	private int readLowNibbleFrom(int offset) {
		if (mapContent == null) {
			return 0;
		} else {
			return (mapContent[offset]) & 0x0F;
		}

	}

	// - read a C-Style String from Buffer (ends with the first \0)
	private String readCStrFrom(int offset, int length) {
		if (mapContent == null || mapContent.length <= offset + length) {
			return "";
		}

		// - find \0 char in buffer
		int i = 0;
		for (; i < length; i++) {
			if (mapContent[offset + i] == 0) {
				break;
			}
		}

		if (i == 0) {
			return "";
		}

		// - substring + encoding
		return new String(mapContent, offset, i - 1, TEXT_CHARSET);
	}

	// - returns a File Resources
	private MapResourceInfo findResource(EOriginalMapFilePartType type) {
		return resources.get(type);
	}

	// - calculates the checksum of the file and compares it
	boolean isChecksumValid() {
		// - read Checksum from File
		int fileChecksum = readBEIntFrom(0);

		mapData.fileChecksum = fileChecksum;

		// - make "count" a Multiple of four
		int count = mapContent.length & 0xFFFFFFFC;
		int currentChecksum = 0;

		// - Map Content starts at Byte 8
		for (int i = 8; i < count; i += 4) {

			// - read DWord
			int currentInt = (mapContent[i] & 0xFF) |
					((mapContent[i + 1] & 0xFF) << 8) |
					((mapContent[i + 2] & 0xFF) << 16) |
					((mapContent[i + 3] & 0xFF) << 24);

			// - using: Logic Right-Shift-Operator: >>>
			currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
		}

		// - return TRUE if the checksum is OK!
		return (currentChecksum == fileChecksum);
	}

	// - Reads in the Map-File-Structure
	void loadMapResources() throws MapLoadException {
		// - Version of File: 0x0A : Original Settlers Map ; 0x0B : Amazon Map
		int fileVersion = readBEIntFrom(4);

		// - check if the Version is compatible?
		if ((fileVersion != EOriginalMapFileVersion.DEFAULT.value) && (fileVersion != EOriginalMapFileVersion.AMAZONS.value)) {
			throw new MapLoadException("The version " + fileVersion + " is unknown");
		}

		// - Data length
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
			// OriginalMapFileDataStructs.EOriginalMapFilePartType .getTypeByInt(partType).toString() );

			// - next position in File
			filePos = filePos + partLen;

			// - save the values
			EOriginalMapFilePartType type = EOriginalMapFilePartType.getTypeByInt(partType);
			if (type != EOriginalMapFilePartType.EOF && (partLen >= 0)) {
				MapResourceInfo newRes = new MapResourceInfo(type, mapPartPos, partLen - 8, partType);

				resources.put(type, newRes);
			}

		} while ((partTypeTemp != 0) && ((filePos + 8) <= dataLength));
	}

	// - freeing the internal File-Buffer
	void freeBuffer() {
		mapContent = null;
		mapData.freeBuffer();
	}

	// - to process a map File this class loads the whole file to memory. To save memory this File-Buffer is
	// - closed after using/when done processing. If more data are requested from the File, the File-Biffer
	// - is loaded again with this reOpen() function.
	void reOpen(InputStream originalMapFile) throws IOException {
		// - read File into buffer
		mapContent = getBytesFromInputStream(originalMapFile);

		// - reset Crypt Info
		for (MapResourceInfo element : resources.values()) {
			element.resetDecryptedFlag();
		}
	}

	void readBasicMapInformation() throws MapLoadException {
		this.readBasicMapInformation(0, 0);
	}

	void readBasicMapInformation(int previewWidth, int previewHeight) throws MapLoadException {
		// - Reset
		fileChecksum = 0;
		widthHeight = 0;
		hasBuildings = false;

		// - safety checks
		if (mapContent == null || mapContent.length < 100) {
			return;
		}

		// - checksum is the first DWord in File
		fileChecksum = readBEIntFrom(0);

		// - read Map Information
		readMapInfo();
		readPlayerInfo();

		readMapQuestText();
		readMapQuestTip();

		// - create preview Image for cache
		if ((previewWidth > 0) && (previewHeight > 0)) {
			this.previewImage = getPreviewImage(previewWidth, previewHeight);
			this.previewWidth = (short) previewWidth;
			this.previewHeight = (short) previewHeight;
		}

		// - get resource information for the area to get map height and width
		MapResourceInfo filePart = findResource(EOriginalMapFilePartType.AREA);

		if (filePart == null || filePart.size < 4) {
			return;
		}

		// TODO: original map: the whole AREA-Block is decrypted but we only need the first 4 byte. Problem... maybe later we need the rest but only
		// if this map is selected for playing AND there was no freeBuffer() and reOpen() call in between.
		// - Decrypt this resource if necessary
		filePart.doDecrypt();

		// - file position of this part
		int pos = filePart.offset;

		// - read height and width (they are the same)
		widthHeight = readBEIntFrom(pos);
	}

	short[] getPreviewImage() {
		// - return cached Image
		return previewImage;
	}

	private short[] getPreviewImage(int width, int height) throws MapLoadException {

		// - return cached Image if available
		if ((previewWidth == width) && (previewHeight == height) && (previewImage != null)) {
			return previewImage;
		}

		// - create new Image
		short[] outImg = new short[width * height];

		// - get resource information for the area
		MapResourceInfo filePart = findResource(EOriginalMapFilePartType.PREVIEW);

		if (filePart == null || filePart.size < 4) {
			return outImg;
		}

		// - Decrypt this resource if necessary
		filePart.doDecrypt();

		// - file position
		int pos = filePart.offset;

		// - height and width are the same
		int widthAndHeight = readBEWordFrom(pos);
		pos += 2;
		@SuppressWarnings("unused")
		int unknown = readBEWordFrom(pos);
		pos += 2;

		int scaleX = widthAndHeight / width;
		int scaleY = widthAndHeight / height;

		int outIndex = 0;
		int offset = pos;

		for (int y = 0; y < height; y++) {
			int srcRow = offset + (scaleY * y) * widthAndHeight * 2;

			for (int x = 0; x < width; x++) {

				int inIndex = srcRow + (x * scaleX) * 2;

				int colorValue = (mapContent[inIndex] & 0xFF) | ((mapContent[inIndex + 1] & 0xFF) << 8);

				// - the Settlers Remake uses rgba4444 colors
				outImg[outIndex] = (short) Color.convert565to4444(colorValue);
				outIndex++;
			}
		}

		return outImg;
	}

	private MapResourceInfo findAndDecryptFilePartSafe(EOriginalMapFilePartType partType) throws MapLoadException {
		Optional<MapResourceInfo> filePart = findAndDecryptFilePart(partType);
		if (filePart.isPresent()) {
			return filePart.get();
		} else {
			throw new MapLoadException("No " + partType + " information available in mapfile!");
		}
	}

	private Optional<MapResourceInfo> findAndDecryptFilePart(EOriginalMapFilePartType partType) throws MapLoadException {
		MapResourceInfo filePart = findResource(partType);

		if ((filePart == null) || (filePart.size == 0)) {
			return Optional.empty();
		}

		// Decrypt this resource if necessary
		filePart.doDecrypt();

		// Call consumer
		return Optional.of(filePart);
	}

	String readMapQuestText() throws MapLoadException {
		if (mapQuestText != null) {
			return mapQuestText;
		}

		return findAndDecryptFilePart(EOriginalMapFilePartType.QUEST_TEXT)
				.map(filePart -> {
					mapQuestText = readCStrFrom(filePart.offset, filePart.size);
					return mapQuestText;
				})
				.orElse("");
	}

	private String readMapQuestTip() throws MapLoadException {
		if (mapQuestTip != null) {
			return mapQuestTip;
		}

		return findAndDecryptFilePart(EOriginalMapFilePartType.QUEST_TIP).map(filePart -> {
			mapQuestTip = readCStrFrom(filePart.offset, filePart.size);
			return mapQuestTip;
		}).orElse("");
	}

	// - Read some common information from the map-file
	private void readMapInfo() throws MapLoadException {
		MapResourceInfo filePartOptional = findAndDecryptFilePartSafe(EOriginalMapFilePartType.MAP_INFO);

		// - file position
		int pos = filePartOptional.offset;

		// ----------------------------------
		// - read mapType (single / multiplayer map?)
		int mapType = readBEIntFrom(pos);
		pos += 4;

		if (mapType == 1) {
			isSinglePlayerMap = true;
		} else if (mapType == 0) {
			isSinglePlayerMap = false;
		} else {
			throw new MapLoadException("wrong value for 'isSinglePlayerMap' " + Integer.toString(mapType) + " in mapfile!");
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
	void readBuildings() throws MapLoadException {
		hasBuildings = false;

		Optional<MapResourceInfo> filePartOptional = findAndDecryptFilePart(EOriginalMapFilePartType.BUILDINGS);

		if (filePartOptional.isPresent()) {
			MapResourceInfo filePart = filePartOptional.get();
			// - file position
			int pos = filePart.offset;

			// - Number of buildings
			int buildingsCount = readBEIntFrom(pos);
			pos += 4;

			// - safety check
			if ((buildingsCount * 12 > filePart.size) || (buildingsCount < 0)) {
				throw new MapLoadException("wrong number of buildings in map File: " + buildingsCount);
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
				mapData.setBuilding(posX, posY, buildingType, party, countSword1, countSword2, countSword3, countArcher1, countArcher2, countArcher3, countSpear1, countSpear2, countSpear3);
			}
		}
	}

	// - Read stacks from the map-file
	void readStacks() throws MapLoadException {
		Optional<MapResourceInfo> filePartOptional = findAndDecryptFilePart(EOriginalMapFilePartType.STACKS);

		if (filePartOptional.isPresent()) {
			MapResourceInfo filePart = filePartOptional.get();
			// - file position
			int pos = filePart.offset;

			// - Number of buildings
			int stackCount = readBEIntFrom(pos);
			pos += 4;

			// - safety check
			if ((stackCount * 8 > filePart.size) || (stackCount < 0)) {
				throw new MapLoadException("wrong number of stacks in map File: " + stackCount);
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
		}
	}

	// - Read settlers from the map-file
	void readSettlers() throws MapLoadException {
		Optional<MapResourceInfo> filePartOptional = findAndDecryptFilePart(EOriginalMapFilePartType.SETTLERS);

		if (filePartOptional.isPresent()) {
			MapResourceInfo filePart = filePartOptional.get();
			// - file position
			int pos = filePart.offset;

			// - Number of buildings
			int settlerCount = readBEIntFrom(pos);
			pos += 4;

			// - safety check
			if ((settlerCount * 6 > filePart.size) || (settlerCount < 0)) {
				throw new MapLoadException("wrong number of settlers in map File: " + settlerCount);
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
		}
	}

	// - Read the Player Info
	private void readPlayerInfo() throws MapLoadException {
		MapResourceInfo filePart = findAndDecryptFilePartSafe(EOriginalMapFilePartType.PLAYER_INFO);

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

	/**
	 * Reads in the Map Data / Landscape and MapObjects like trees
	 */
	void readMapData() throws MapLoadException {
		MapResourceInfo filePart = findAndDecryptFilePartSafe(EOriginalMapFilePartType.AREA);

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
	}

	public void addStartTowerMaterialsAndSettlers(EMapStartResources startResources) {
		addStartTowerMaterialsAndSettlers(startResources, null);
	}

	public void addStartTowerMaterialsAndSettlers(EMapStartResources startResources, PlayerSetting[] playerSettings) {
		// - only if there are no buildings
		if (hasBuildings) {
			return;
		}

		int playerCount = mapData.getPlayerCount();

		for (byte playerId = 0; playerId < playerCount; playerId++) {
			if (playerSettings != null && !playerSettings[playerId].isAvailable())
				continue;
			ShortPoint2D startPoint = mapData.getStartPoint(playerId);

			// - add the start Tower for this player
			mapData.setMapObject(startPoint.x, startPoint.y, new BuildingMapDataObject(EBuildingType.TOWER, playerId));

			// - list of all objects that have to be added for this player
			List<MapDataObject> mapObjects = EMapStartResources.generateStackObjects(startResources);
			mapObjects.addAll(EMapStartResources.generateMovableObjects(startResources, playerId));

			// - blocking area of the tower
			List<RelativePoint> towerTiles = Arrays.asList(EBuildingType.TOWER.getProtectedTiles());

			RelativePoint relativeMapObjectPoint = new RelativePoint(-3, 3);

			for (MapDataObject currentMapObject : mapObjects) {
				do {
					// - get next point
					relativeMapObjectPoint = nextPointOnSpiral(relativeMapObjectPoint);

					// - don't put things under the tower
					if (towerTiles.contains(relativeMapObjectPoint)) {
						continue;
					}

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

		if (previousX == basis && previousY > -basis) {
			return new RelativePoint(previousX, previousY - 1);
		} else if (previousX == -basis && previousY <= basis) {
			return new RelativePoint(previousX, previousY + 1);
		} else if (previousX < basis && previousY == basis) {
			return new RelativePoint(previousX + 1, previousY);
		} else if (previousX > -basis && previousY == -basis) {
			return new RelativePoint(previousX - 1, previousY);
		} else {
			return null;
		}
	}

	String getChecksum() {
		return Integer.toString(fileChecksum);
	}
}
