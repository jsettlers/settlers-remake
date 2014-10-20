package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.grid.PlaceholderObject;

public class BuildingInstruction extends ObjectInstruction {
	private static Hashtable<String, String> defaults = new Hashtable<String, String>();
	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "1");
		defaults.put("distance", "0");
		defaults.put("on", "grass");
		defaults.put("type", "tower");
	}

	@Override
	protected void placeObject(MapGrid grid, PlayerStart start, int x, int y,
	        Random random) {
		EBuildingType type = getParameter("type", random, EBuildingType.class);

		for (RelativePoint relative : type.getProtectedTiles()) {
			grid.setMapObject(x + relative.getDx(), y + relative.getDy(),
			        PlaceholderObject.getInstance());
		}

		grid.setMapObject(x, y, new BuildingObject(type, start.getPlayerId()));
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
