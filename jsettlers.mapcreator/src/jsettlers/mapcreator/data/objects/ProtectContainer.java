package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.RelativePoint;

public class ProtectContainer implements ObjectContainer {

	private static ProtectContainer instance;

	@Override
	public MapObject getMapObject() {
		return null;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
				new RelativePoint(0, 0)
		};
	}

	public static ProtectContainer getInstance() {
		if (instance == null) {
			instance = new ProtectContainer();
		}
		return instance;
	}

}
