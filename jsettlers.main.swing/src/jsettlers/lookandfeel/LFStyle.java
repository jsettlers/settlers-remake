package jsettlers.lookandfeel;

/**
 * Look and Feel style type
 * 
 * @author Andreas Butti
 */
public enum LFStyle {

	/**
	 * Stone look menu button
	 */
	BUTTON_MENU,

	/**
	 * Header JLabel
	 */
	LABEL_HEADER,

	/**
	 * JLabel
	 */
	LABEL_LONG,

	/**
	 * JLabel
	 */
	LABEL_SHORT,

	/**
	 * Half transparent dark panel
	 */
	PANEL_DARK,

	/**
	 * JTextField for black background
	 */
	TEXT_DEFAULT,

	/**
	 * Supports transparent background colors
	 */
	PANEL_DRAW_BG_CUSTOM;

	/**
	 * Key used for putClientProperty
	 */
	public static final Object KEY = new Object();

}
