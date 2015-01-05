package jsettlers.mapcreator.main.error;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

public class Error implements ILocatable {
	private final ShortPoint2D position;
	private final String description;

	public Error(ShortPoint2D position, String description) {
		this.position = position;
		this.description = description;

	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	@Override
	public String toString() {
		return description;
	}
}
