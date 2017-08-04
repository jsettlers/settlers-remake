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
package jsettlers.logic.map.loading.savegame;

import java.io.IOException;
import java.io.ObjectInputStream;

import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.input.PlayerState;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.grid.GameSerializer;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.loading.newmap.RemakeMapLoader;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SavegameLoader extends RemakeMapLoader {

	public SavegameLoader(IListedMap file, MapFileHeader header) {
		super(file, header);
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings) throws MapLoadException {
		return loadMainGrid(playerSettings, EMapStartResources.HIGH_GOODS);
	}

		@Override
	public MainGridWithUiSettings loadMainGrid(PlayerSetting[] playerSettings, EMapStartResources startResources) throws MapLoadException {
		try (ObjectInputStream ois = new ObjectInputStream(super.getMapDataStream())) {
			MatchConstants.deserialize(ois);
			PlayerState[] playerStates = (PlayerState[]) ois.readObject();
			GameSerializer gameSerializer = new GameSerializer();
			MainGrid mainGrid = gameSerializer.load(ois);
			mainGrid.initWithPlayerSettings(playerSettings);
			RescheduleTimer.loadFrom(ois);

			ois.close();

			return new MainGridWithUiSettings(mainGrid, playerStates);
		} catch (IOException | ClassNotFoundException ex) {
			throw new MapLoadException(ex);
		}
	}

	@Override
	public IMapData getMapData() throws MapLoadException {
		throw new UnsupportedOperationException("A savegame can't supply IMapData");
	}
}
