package jsettlers.mapcreator.main.error;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;

public class Error implements ILocatable {
	private final ISPosition2D position;
	private final String description;

	public Error(ISPosition2D position, String description) {
		this.position = position;
		this.description = description;
		
	}

	@Override
    public ISPosition2D getPos() {
	    return position;
    }
	
	@Override
	public String toString() {
	    return description;
	}
}
