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
package jsettlers.main.swing.lookandfeel;

/**
 * Look and Feel style type
 * 
 * @author Andreas Butti
 */
public enum ELFStyle {

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
	PROGRESSBAR_SLIDER,

	/**
	 * Combobox in settler style
	 */
	COMBOBOX,

	/**
	 * Tables in settlers label style
	 */
	TABLE;

	/**
	 * Key used for putClientProperty
	 */
	public static final Object KEY = new Object();

}
