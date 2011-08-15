package jsettlers.graphics.utils;

import java.awt.Font;

/**
 * This enum describes the possible font sizes of a text.
 * @author michael
 *
 */
public enum EFontSize {
	/**
	 * This is the small font. It is used for small numbers and other hints.
	 */
	SMALL(11),
	/**
	 * This is the normal font size for buttons and most labels.
	 */
	NORMAL(13),
	/**
	 * This is the font size for headlines
	 */
	HEADLINE(17);

	private static final String FONTNAME = "Arial";
	
	private final int size;

	private EFontSize(int size) {
		this.size = size;
    }
	
	public Font getFont() {
	    return new Font(FONTNAME, Font.TRUETYPE_FONT, this.size);
    }
}
