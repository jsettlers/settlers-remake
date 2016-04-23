/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Show the map settings dialog
 * 
 * @author Andreas Butti
 *
 */
public abstract class SettingsDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * The editor panel
	 */
	private final MapHeaderEditorPanel headerEditor;

	/**
	 * Original map header
	 */
	private final MapFileHeader header;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center the dialog on
	 * @param header
	 *            Header to edit
	 */
	public SettingsDialog(JFrame parent, MapFileHeader header) {
		super(parent);
		setTitle(EditorLabels.getLabel("settings.header"));
		this.header = header;
		headerEditor = new MapHeaderEditorPanel(header, false);
		add(headerEditor, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * Apply the new header configuration
	 * 
	 * @param header
	 *            New header
	 */
	public abstract void applyNewHeader(MapFileHeader header);

	@Override
	protected boolean beforeOkAction() {
		MapFileHeader nheader = headerEditor.getHeader();
		if (nheader.getWidth() != header.getWidth() || nheader.getHeight() != header.getHeight()) {
			JOptionPane.showMessageDialog(SettingsDialog.this, "Widh and height are fixed.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		applyNewHeader(nheader);
		return true;
	}

}
