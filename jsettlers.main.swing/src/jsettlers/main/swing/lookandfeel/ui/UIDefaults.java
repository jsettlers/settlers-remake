/*******************************************************************************
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
