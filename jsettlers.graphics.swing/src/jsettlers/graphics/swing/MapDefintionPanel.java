/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class MapDefintionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm");

	/**
	 * Create a panel with fake content.
	 */
	public MapDefintionPanel() {
		this("map name", "additional info", new Date(), true);
	}

	public MapDefintionPanel(String name, String additionalInfo, Date date, boolean border) {
		if (border) {
			setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		}

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		Component verticalStrut = Box.createVerticalStrut(5);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 1;
		gbc_verticalStrut.gridy = 0;
		add(verticalStrut, gbc_verticalStrut);

		JLabel lblMapName = new JLabel(name);
		GridBagConstraints gbc_lblMapName = new GridBagConstraints();
		gbc_lblMapName.insets = new Insets(0, 0, 2, 5);
		gbc_lblMapName.anchor = GridBagConstraints.WEST;
		gbc_lblMapName.gridx = 0;
		gbc_lblMapName.gridy = 1;
		add(lblMapName, gbc_lblMapName);

		JLabel lblDate = new JLabel(date != null ? dateFormat.format(date) : "");
		GridBagConstraints gbc_lblDate = new GridBagConstraints();
		gbc_lblDate.insets = new Insets(0, 0, 0, 5);
		gbc_lblDate.gridheight = 2;
		gbc_lblDate.gridx = 1;
		gbc_lblDate.gridy = 1;
		add(lblDate, gbc_lblDate);

		JLabel lblAdditionalInfo = new JLabel(additionalInfo);
		GridBagConstraints gbc_lblAdditionalInfo = new GridBagConstraints();
		gbc_lblAdditionalInfo.insets = new Insets(0, 0, 0, 5);
		gbc_lblAdditionalInfo.anchor = GridBagConstraints.WEST;
		gbc_lblAdditionalInfo.gridx = 0;
		gbc_lblAdditionalInfo.gridy = 2;
		add(lblAdditionalInfo, gbc_lblAdditionalInfo);

		Component verticalStrut_1 = Box.createVerticalStrut(5);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut_1.gridx = 1;
		gbc_verticalStrut_1.gridy = 3;
		add(verticalStrut_1, gbc_verticalStrut_1);

	}
}
