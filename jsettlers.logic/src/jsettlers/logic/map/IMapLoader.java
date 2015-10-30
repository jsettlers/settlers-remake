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
package jsettlers.logic.map;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.original.OriginalMapLoader;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.loader.*;

/**
 * Classes of this interface are to load a game file
 * 
 */
public abstract class IMapLoader implements IGameCreator, Comparable<MapLoader>, IMapDefinition{
	
	public static final String MAP_EXTENSION = ".smap";
	public static final String MAP_EXTENSION_COMPRESSED = ".zmap";
	public static final String MAP_EXTENSION_ORIGINAL = ".map";
	
	public abstract MapFileHeader getFileHeader();
	
	
	public static IMapLoader getLoaderForListedMap(IListedMap listedMap) throws MapLoadException
	{
		
		if (listedMap.getFileName().endsWith(IMapLoader.MAP_EXTENSION_ORIGINAL))
		{
			//- original Siedler 3 Map
			return new OriginalMapLoader(listedMap);
		}
		else
		{
			//- Siedler 3 Remake Savegame or Map
			MapFileHeader header = MapLoader.loadHeader(listedMap);
	
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
}
