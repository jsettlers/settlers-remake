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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Display a dialog to create a new map or open an existing one, displayed at startup
 * 
 * @author Andreas Butti
 */
public class NewOrOpenDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Panel with the editfield
	 */
	private final NewFilePanel newFilePanel = new NewFilePanel();

	/**
	 * Listener for Double click
	 */
	private final ActionListener doubleClickListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			confirmed = true;
			doOkAction();
			dispose();
		}
	};

	/**
	 * Panel with the map list
	 */
	private final OpenPanel openPanel = new OpenPanel(doubleClickListener);

	/**
	 * Panel with the map list
	 */
	private final LastUsedPanel lastUsed = new LastUsedPanel(doubleClickListener);

	/**
	 * Main tabs
	 */
	private final JTabbedPane tabs = new JTabbedPane();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public NewOrOpenDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("neworopen.header"));

		tabs.addTab(EditorLabels.getLabel("neworopen.lastused"), lastUsed);
		tabs.addTab(EditorLabels.getLabel("neworopen.open"), openPanel);
		tabs.addTab(EditorLabels.getLabel("neworopen.new"), newFilePanel);

		add(tabs, BorderLayout.CENTER);

		if (!lastUsed.hasFiles()) {
			tabs.setSelectedComponent(openPanel);
		}

		pack();

		// prevent to big dialog
		if (getWidth() > 1024) {
			setSize(new Dimension(1024, getHeight()));
		}

		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return true for last used
	 */
	public boolean isLastUsed() {
		return tabs.getSelectedIndex() == 0;
	}

	/**
	 * @return true for open
	 */
	public boolean isOpenAction() {
		return tabs.getSelectedIndex() == 1;
	}

	/**
	 * @return Panel with the new file data
	 */
	public NewFilePanel getNewFilePanel() {
		return newFilePanel;
	}

	/**
	 * @return Panel with the map list
	 */
	public OpenPanel getLastUsed() {
		return lastUsed;
	}

	/**
	 * @return Panel with the map list
	 */
	public OpenPanel getOpenPanel() {
		return openPanel;
	}

}
