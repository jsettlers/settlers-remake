package jsettlers.logic.map.random.grid;

import jsettlers.common.map.object.MapObject;

public class PlaceholderObject implements MapObject {
	private static PlaceholderObject instance;

	private PlaceholderObject() {
	}

	public static synchronized PlaceholderObject getInstance() {
		if (instance == null) {
			instance = new PlaceholderObject();
		}
		return instance;
	}
}
