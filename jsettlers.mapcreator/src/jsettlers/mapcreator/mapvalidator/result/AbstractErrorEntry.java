package jsettlers.mapcreator.mapvalidator.result;

/**
 * Base class for error entries in the list
 * 
 * @author Andreas Butti
 */
public class AbstractErrorEntry {

	/**
	 * Text to display
	 */
	private final String text;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 */
	public AbstractErrorEntry(String text) {
		this.text = text;
	}

	/**
	 * @return Text to display
	 */
	public String getText() {
		return text;
	}

}
