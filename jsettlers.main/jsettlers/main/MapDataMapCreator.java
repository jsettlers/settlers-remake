package jsettlers.main;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.IMapDataProvider;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.logic.map.newGrid.MainGrid;

@Deprecated
public class MapDataMapCreator implements IGameCreator {

	private final IMapDataProvider map;
	
	private IMapData data;

	public MapDataMapCreator(IMapDataProvider map) {
		this.map = map;
    }

	@Override
	public MainGrid getMainGrid() throws MapLoadException {
		if (data == null) {
			data = map.getData();
		}

		MainGrid mainGrid = MainGrid.create(data);
        if (mainGrid == null) {
        	throw new MapLoadException("loaded map was null");
        }
		return mainGrid;
	}

	@Override
	public UIState getUISettings(int player) throws MapLoadException {
		if (data == null) {
			data = map.getData();
		}
		return new UIState(player, data.getStartPoint(player));
	}

}
