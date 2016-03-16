package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Constant colors for L&F
 * 
 * @author Andreas Butti
 *
 */
public interface UIDefaults {

	/**
	 * Label header
	 */
	public static final Color HEADER_TEXT_COLOR = new Color(0xFF5D18);

	/**
	 * Label color
	 */
	public static final Color LABEL_TEXT_COLOR = Color.YELLOW;

	/**
	 * Default font
	 */
	public static final Font FONT = new Font("Sans", Font.BOLD, 14);

	/**
	 * Default font
	 */
	public static final Font FONT_PLAIN = new Font("Sans", Font.PLAIN, 14);

	/**
	 * Half transparent black
	 */
	public static final Color HALFTRANSPARENT_BLACK = new Color(0, 0, 0, 160);

	/**
	 * Arrow color for Scrollbar and JCombobox
	 */
	public static final Color ARROW_COLOR = new Color(0x909090);

}
