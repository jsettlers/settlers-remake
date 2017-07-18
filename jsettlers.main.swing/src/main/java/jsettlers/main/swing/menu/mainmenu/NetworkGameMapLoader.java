/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.menu.mainmenu;

import java.util.Date;
import java.util.List;

import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.ILoadableMapPlayer;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.player.PlayerSetting;

/**
 * @author codingberlin
 */
public class NetworkGameMapLoader extends MapLoader {

	private final MapLoader mapLoader;
	private final String gameName;
	private final IJoinableGame joinableGame;

	public NetworkGameMapLoader(IJoinableGame joinableGame) {
		this.joinableGame = joinableGame;
		this.mapLoader = MapList.getDefaultList().getMapById(joinableGame.getMap().getMapId());
		this.gameName = joinableGame.getName();
	}

	public IJoinableGame getJoinableGame() {
		return joinableGame;
	}

	@Override
	public MapFileHeader getFileHeader() {
		return mapLoader.getFileHeader();
	}

	@Override
	public IListedMap getListedMap() {
		return mapLoader.getListedMap();
	}

	@Override
	public IMapData getMapData() throws MapLoadException {
		return mapLoader.getMapData();
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		return mapLoader.loadMainGrid(playerSettings);
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings, EMapStartResources startResources)
			throws MapLoadException {
		return mapLoader.loadMainGrid(playerSettings, startResources);
	}

	@Override
	public String getMapName() {
		return gameName + "(" + mapLoader.getMapName() + ")";
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
	public List<ILoadableMapPlayer> getPlayers() {
		return mapLoader.getPlayers();
	}

	@Override
	public Date getCreationDate() {
		return mapLoader.getCreationDate();
	}

	@Override
	public String getMapId() {
		return mapLoader.getMapId();
	}
}
