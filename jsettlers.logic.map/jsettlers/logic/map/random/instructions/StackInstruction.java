package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.random.generation.PlayerStart;

public class StackInstruction extends ObjectInstruction {
	private static Hashtable<String, String> defaults = new Hashtable<String, String>();
	static {
		defaults.put("dx", "0");
		defaults.put("dy", "0");
		defaults.put("count", "1");
		defaults.put("distance", "20");
		defaults.put("on", "grass");
		defaults.put("material", "plank");
		defaults.put("capacity", "4..8");
	}

	@Override
	protected MapObject getObject(PlayerStart start, Random random) {
		EMaterialType type = getParameter("material", random, EMaterialType.class);

		return new StackObject(type, getIntParameter("capacity", random));
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
