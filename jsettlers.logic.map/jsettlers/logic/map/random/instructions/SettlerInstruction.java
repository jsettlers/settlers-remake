package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapObject;
import jsettlers.logic.map.random.grid.MovableObject;

public class SettlerInstruction extends ObjectInstruction {
	private static Hashtable<String, String> defaults = new Hashtable<String, String>();
	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "1");
		defaults.put("distance", "20");
		defaults.put("on", "grass");
		defaults.put("type", "bearer");
	}

	@Override
	protected MapObject getObject(PlayerStart start, Random random) {
		EMovableType type = getParameter("type", random, EMovableType.class);

		return new MovableObject(type, start.getPlayer());
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
