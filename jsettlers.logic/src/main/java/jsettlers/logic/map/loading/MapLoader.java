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
package jsettlers.logic.map.loading;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.original.OriginalMapLoader;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.newmap.MapFileHeader.MapType;
import jsettlers.logic.map.loading.newmap.FreshMapLoader;
import jsettlers.logic.map.loading.newmap.RemakeMapLoader;
import jsettlers.logic.map.loading.savegame.SavegameLoader;

import java.util.Locale;

/**
 * This is the base class that prvides support for loading maps and starting a new game from them.
 * 
 */
public abstract class MapLoader implements IGameCreator, Comparable<MapLoader>, IMapDefinition {

	protected MapFileHeader header;

	public static final String MAP_EXTENSION = ".rmap";
	public static final String MAP_EXTENSION_COMPRESSED = ".zmap";
	public static final String MAP_EXTENSION_ORIGINAL = ".map";
	public static final String MAP_EXTENSION_ORIGINAL_MAP_EDITOR = ".edm";

	public abstract MapFileHeader getFileHeader();

	public static MapLoader getLoaderForListedMap(IListedMap listedMap) throws MapLoadException {
		if ((checkExtention(listedMap.getFileName(), MapLoader.MAP_EXTENSION_ORIGINAL))
				|| (checkExtention(listedMap.getFileName(), MapLoader.MAP_EXTENSION_ORIGINAL_MAP_EDITOR))) {
			// - original Siedler 3 Map
			return new OriginalMapLoader(listedMap);
		} else {
			// - Siedler 3 Remake Savegame or Map
			MapFileHeader header = RemakeMapLoader.loadHeader(listedMap);

			switch (header.getType()) {
			case NORMAL:
				return new FreshMapLoader(listedMap, header);
			case SAVED_SINGLE:
				return new SavegameLoader(listedMap, header);
			default:
				throw new MapLoadException("Unkown EMapType: " + header.getType());
			}
		}
	}

	public static boolean checkExtention(String filename, String Extention) {
		if (filename == null)
			return false;
		return filename.toLowerCase(Locale.ENGLISH).endsWith(Extention.toLowerCase(Locale.ENGLISH));
	}

	public static boolean isExtensionKnown(String filename) {
		if (checkExtention(filename, MAP_EXTENSION_ORIGINAL))
			return true;
		if (checkExtention(filename, MAP_EXTENSION))
			return true;
		if (checkExtention(filename, MAP_EXTENSION_COMPRESSED))
			return true;
		return checkExtention(filename, MAP_EXTENSION_ORIGINAL_MAP_EDITOR);
	}

	// - Interface: Comparable<MapLoader>
	@Override
	public int compareTo(MapLoader other) {
		MapFileHeader myHeader = this.getFileHeader();
		MapFileHeader otherHeader = other.getFileHeader();
		if (myHeader.getType() == otherHeader.getType() && myHeader.getType() == MapType.SAVED_SINGLE) {
			return -this.getCreationDate().compareTo(other.getCreationDate()); // order by date descending
		} else {
			return this.getMapName().compareToIgnoreCase(other.getMapName()); // order by name ascending
		}
	}

	public abstract IListedMap getListedMap();

	/**
	 * Gets the map data for this loader, if the data is available.
	 * 
	 * @return
	 */
	public abstract IMapData getMapData() throws MapLoadException;

}
