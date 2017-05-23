/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import jsettlers.logic.map.loading.MapLoader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Dialog to open an existing map
 * 
 * @author Andreas Butti
 */
public class OpenExistingDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Listener for Double click
	 */
	private final ActionListener doubleClickListener = e -> {
		confirmed = true;
		beforeOkAction();
		dispose();
	};

	/**
	 * Panel with the map list
	 */
	private final OpenPanel openPanel = new OpenPanel(doubleClickListener);

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public OpenExistingDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("openfile.header"));

		add(openPanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return The selected map
	 */
	public MapLoader getSelectedMap() {
		return openPanel.getSelectedMap();
	}
}
