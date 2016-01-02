package jsettlers.mapcreator.mapvalidator.result;

import jsettlers.mapcreator.mapvalidator.result.fix.IFix;

/**
 * Header entry
 * 
 * @author Andreas Butti
 */
public class ErrorHeader extends AbstractErrorEntry {

	/**
	 * Fix, if any
	 */
	private IFix fix;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 * @param fix
	 *            Fix, if any
	 */
	public ErrorHeader(String text, IFix fix) {
		super(text);
		this.fix = fix;
	}

	/**
	 * @return Fix, if any
	 */
	public IFix getFix() {
		return fix;
	}

}
