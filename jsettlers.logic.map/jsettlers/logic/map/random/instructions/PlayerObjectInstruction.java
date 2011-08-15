package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapObject;
import jsettlers.logic.map.random.grid.MapStoneObject;
import jsettlers.logic.map.random.grid.MapTreeObject;

public class PlayerObjectInstruction extends ObjectInstruction {

	private static Hashtable<String, String> defaults =
	        new Hashtable<String, String>();

	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "100");
		defaults.put("tight", "1");
		defaults.put("distance", "0");
		defaults.put("on", "grass");
		defaults.put("type", "tree");
		defaults.put("capacity", "0");
	}

	@Override
	protected MapObject getObject(PlayerStart start, Random random) {
	    MapObject object;
	    String type = getParameter("type", random).toUpperCase();
		if ("TREE".equals(type)) {
	    	object = MapTreeObject.getInstance();
	    } else if ("STONE".equals(type)) {
	    	object = MapStoneObject.getInstance(getIntParameter("capacity", random));
	    } else {
	    	throw new IllegalArgumentException("type " + type + " unknown");
	    }
	    return object;
    }

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
