package jsettlers.mapcreator.mapvalidator.result;

import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;

/**
 * Header entry
 * 
 * @author Andreas Butti
 */
public class ErrorHeader extends AbstractErrorEntry {

	/**
	 * Fix, if any
	 */
	private AbstractFix fix;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 * @param fix
	 *            Fix, if any
	 */
	public ErrorHeader(String text, AbstractFix fix) {
		super(text);
		this.fix = fix;
	}

	/**
	 * @return Fix, if any
	 */
	public AbstractFix getFix() {
		return fix;
	}

}
