package jsettlers.graphics.startscreen;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;

public class GameSettings implements IGameSettings {

	private final IMapItem map;
	private final int count;

	public GameSettings(IMapItem item, int count) {
		this.map = item;
		this.count = count;
	}
	
	@Override
	public IMapItem getMap() {
		return map;
	}

	@Override
	public int getPlayerCount() {
		return count;
	}


}
