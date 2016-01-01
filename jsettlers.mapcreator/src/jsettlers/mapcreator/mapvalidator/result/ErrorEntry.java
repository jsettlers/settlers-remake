package jsettlers.mapcreator.mapvalidator.result;

import jsettlers.common.position.ShortPoint2D;

/**
 * An error entry in the validation list
 * 
 * @author Andreas Butti
 */
public class ErrorEntry extends AbstarctErrorEntry {

	private ShortPoint2D pos;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 * @param pos
	 */
	public ErrorEntry(String text, ShortPoint2D pos) {
		super(text);
		this.pos = pos;
	}

	public ShortPoint2D getPos() {
		return pos;
	}

}
