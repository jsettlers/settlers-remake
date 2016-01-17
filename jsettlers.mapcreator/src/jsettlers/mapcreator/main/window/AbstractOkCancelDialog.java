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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Base class for all OK / Cancel dialogs
 * 
 * @author Andreas Butti
 *
 */
public abstract class AbstractOkCancelDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * If the user pressed OK
	 */
	protected boolean confirmed = false;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public AbstractOkCancelDialog(JFrame parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		initButton();
	}

	/**
	 * @return If the user pressed OK
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * Initialize buttons
	 */
	private void initButton() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btOk = new JButton(EditorLabels.getLabel("general.OK"));
		btOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!doOkAction()) {
					return;
				}
				confirmed = true;
				dispose();
			}
		});

		JButton btCancel = new JButton(EditorLabels.getLabel("general.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!doCancelAction()) {
					return;
				}
				dispose();
			}
		});

		buttonPanel.add(btCancel);
		buttonPanel.add(btOk);

		Dimension size = btOk.getPreferredSize();
		if (btCancel.getPreferredSize().width > size.width) {
			size.width = btCancel.getPreferredSize().width;
		}
		btOk.setPreferredSize(size);
		btCancel.setPreferredSize(size);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Action performed before cancel
	 * 
	 * @return true to close the dialog
	 */
	protected boolean doCancelAction() {
		return true;
	}

	/**
	 * Action performed before OK
	 * 
	 * @return true to close the dialog
	 */
	protected boolean doOkAction() {
		return true;
	}
}
