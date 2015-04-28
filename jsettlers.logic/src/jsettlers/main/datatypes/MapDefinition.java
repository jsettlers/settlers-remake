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
package jsettlers.main.datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.save.loader.MapLoader;

/**
 * This class is an implementatiopn of the interfaces {@link IStartableMapDefinition} and {@link ILoadableMapDefinition}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MapDefinition implements IMapDefinition {

	private final MapLoader mapLoader;

	public MapDefinition(MapLoader mapLoader) {
		this.mapLoader = mapLoader;
	}

	@Override
	public String getId() {
		return mapLoader.getMapID();
	}

	@Override
	public String getName() {
		return mapLoader.getMapName();
	}

	@Override
	public String getDescription() {
		return mapLoader.getDescription();
	}

	@Override
	public short[] getImage() {
		return mapLoader.getImage();
	}

	@Override
	public int getMinPlayers() {
		return mapLoader.getMinPlayers();
	}

	@Override
	public int getMaxPlayers() {
		return mapLoader.getMaxPlayers();
	}

	@Override
	public List<ILoadableMapPlayer> getPlayers() { // TODO @Andreas Eberle: supply saved players information.
		return new ArrayList<ILoadableMapPlayer>();
	}

	@Override
	public Date getCreationDate() {
		return mapLoader.getCreationDate();
	}

}
