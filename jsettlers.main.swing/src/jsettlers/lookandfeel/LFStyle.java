package jsettlers.lookandfeel;

/**
 * Look and Feel style type
 * 
 * @author Andreas Butti
 */
public enum LFStyle {

	/**
	 * Stone look button
	 */
	BUTTON_STONE,

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
	 * Label with dynamic length
	 */
	LABEL_DYNAMIC,

	/**
	 * Half transparent dark panel
	 */
	PANEL_DARK,

	/**
	 * JTextField for black background
	 */
	TEXT_DEFAULT,

	/**
	 * Stone toggle button
	 */
	TOGGLE_BUTTON_STONE,

	/**
	 * Supports transparent background colors
	 */
	PANEL_DRAW_BG_CUSTOM,

	/**
	 * Settler Slider, based on progressbar
	 */
	PROGRESSBAR_SLIDER;

	/**
	 * Key used for putClientProperty
	 */
	public static final Object KEY = new Object();

}
