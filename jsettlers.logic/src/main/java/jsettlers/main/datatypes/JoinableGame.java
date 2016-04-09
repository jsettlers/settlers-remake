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

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.network.common.packets.MatchInfoPacket;

/**
 * This is a simple POJO implementing the {@link IJoinableGame} interface.
 * 
 * @author Andreas Eberle
 * 
 */
public class JoinableGame implements IJoinableGame {

	private String id;
	private String name;
	private IMapDefinition map;

	public JoinableGame(MatchInfoPacket matchInfo) {
		this.id = matchInfo.getId();
		this.name = matchInfo.getMatchName();
		this.map = MapList.getDefaultList().getMapById(matchInfo.getMapInfo().getId());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IMapDefinition getMap() {
		return map;
	}
}
