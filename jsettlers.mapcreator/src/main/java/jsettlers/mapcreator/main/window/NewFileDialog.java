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

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Display new file dialog
 * 
 * @author Andreas Butti
 */
public class NewFileDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Panel with the editfield
	 */
	private NewFilePanel newFilePanel = new NewFilePanel();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public NewFileDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("newfile.header"));

		add(newFilePanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return The selected ground type
	 */
	public ELandscapeType getGroundTypes() {
		return newFilePanel.getGroundTypes();
	}

	/**
	 * @return The configured map header
	 */
	public MapFileHeader getHeader() {
		return newFilePanel.getHeader();
	}
}
