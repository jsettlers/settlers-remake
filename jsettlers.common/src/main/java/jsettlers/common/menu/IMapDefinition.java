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
package jsettlers.common.menu;

import java.util.Date;
import java.util.List;

/**
 * This interface defines the methods supplying information about a map definition
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IMapDefinition {
	/**
	 * Gets the id of the map. This id must be unique! The id must also differ between maps in a different version.
	 * 
	 * @return The unique identifier of the represented map.
	 */
	String getMapId();

	/**
	 * Gets the name of the map.
	 * 
	 * @return A name describing the map.
	 */
	String getMapName();

	/**
	 * Gets the description of this map.
	 * 
	 * @return A string that describes this map. It may contain linebreaks.
	 */
	String getDescription();

	/**
	 * Gets the image of this map.
	 *
	 * @return The image data
	 */
	short[] getImage();

	/**
	 * Gets the minimum number of players that can play this map.
	 * 
	 * @return That number.
	 */
	int getMinPlayers();

	/**
	 * Gets the maximum number of players supported by this map.
	 * 
	 * @return The number of players supported by this map.
	 */
	int getMaxPlayers();

	/**
	 * Gets a list of players that played on the map.
	 * 
	 * @return The players from that loadable game.
	 */
	List<ILoadableMapPlayer> getPlayers();

	Date getCreationDate();
}