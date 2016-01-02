package jsettlers.mapcreator.mapvalidator.result;

import jsettlers.common.position.ShortPoint2D;

/**
 * An error entry in the validation list
 * 
 * @author Andreas Butti
 */
public class ErrorEntry extends AbstractErrorEntry {

	/**
	 * Position of the error
	 */
	private ShortPoint2D pos;

	/**
	 * Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	private String typeId;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 * @param pos
	 *            Position of the error
	 * @param typeId
	 *            Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public ErrorEntry(String text, ShortPoint2D pos, String typeId) {
		super(text);
		this.pos = pos;
		this.typeId = typeId;
	}

	/**
	 * @return Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @return Position of the error
	 */
	public ShortPoint2D getPos() {
		return pos;
	}

}
