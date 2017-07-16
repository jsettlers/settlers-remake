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
package jsettlers.mapcreator.main.window;

import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import jsettlers.common.CommonConstants;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.newmap.MapFileHeader.MapType;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * This dialog edits the map header.
 * 
 * @author michael
 */
public class MapHeaderEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_MAPSIZE = 300;

	private static final int MIN_MAPSIZE = 100;
	private static final int MAX_MAPSIZE = 2000;
	private SpinnerNumberModel width;
	private SpinnerNumberModel height;
	private SpinnerNumberModel minPlayer;
	private SpinnerNumberModel maxPlayer;

	/**
	 * Text field with the map name
	 */
	private JTextField nameField;
	private JTextArea descriptionField;

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Header to display
	 * @param sizeChangable
	 *            If the size is editable or not
	 */
	public MapHeaderEditorPanel(MapFileHeader header, boolean sizeChangable) {
		generate(sizeChangable);
		setHeader(header);
	}

	private void generate(boolean sizeChangable) {
		nameField = new JTextField();
		descriptionField = new JTextArea(5, 40);
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);

		width = new SpinnerNumberModel(DEFAULT_MAPSIZE, MIN_MAPSIZE,
				MAX_MAPSIZE, 1);
		height = new SpinnerNumberModel(DEFAULT_MAPSIZE, MIN_MAPSIZE,
				MAX_MAPSIZE, 1);
		minPlayer = new SpinnerNumberModel(1, 1, CommonConstants.MAX_PLAYERS, 1);
		maxPlayer = new SpinnerNumberModel(1, 1, CommonConstants.MAX_PLAYERS, 1);

		JSpinner widthField = new JSpinner(width);
		JSpinner heightField = new JSpinner(height);
		JSpinner minPlayerField = new JSpinner(minPlayer);
		JSpinner maxPlayerField = new JSpinner(maxPlayer);

		JLabel nameLabel = new JLabel(EditorLabels.getLabel("header.map-name"));
		JLabel descriptionLabel = new JLabel(EditorLabels.getLabel("header.map-description"));
		JLabel widthLabel = new JLabel(EditorLabels.getLabel("header.width"));
		JLabel heightLabel = new JLabel(EditorLabels.getLabel("header.height"));
		JLabel minPlayerLabel = new JLabel(EditorLabels.getLabel("header.map-min-player"));
		JLabel maxPlayerLabel = new JLabel(EditorLabels.getLabel("header.map-max-player"));

		add(nameField);
		add(descriptionField);
		add(heightField);
		add(widthField);
		add(minPlayerField);
		add(maxPlayerField);

		add(nameLabel);
		add(descriptionLabel);
		add(widthLabel);
		add(heightLabel);
		add(maxPlayerLabel);
		add(minPlayerLabel);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// @formatter:off
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(nameLabel)
						.addComponent(descriptionLabel)
						.addComponent(heightLabel)
						.addComponent(widthLabel)
						.addComponent(maxPlayerLabel)
						.addComponent(minPlayerLabel))
				.addGroup(layout.createParallelGroup()
						.addComponent(nameField)
						.addComponent(descriptionField)
						.addComponent(heightField)
						.addComponent(widthField)
						.addComponent(maxPlayerField)
						.addComponent(minPlayerField)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(nameLabel)
						.addComponent(nameField))
				.addGroup(layout.createParallelGroup()
						.addComponent(descriptionLabel)
						.addComponent(descriptionField))
				.addGroup(layout.createParallelGroup()
						.addComponent(widthLabel)
						.addComponent(widthField))
				.addGroup(layout.createParallelGroup()
						.addComponent(heightLabel)
						.addComponent(heightField))
				.addGroup(layout.createParallelGroup()
						.addComponent(minPlayerLabel)
						.addComponent(minPlayerField))
				.addGroup(layout.createParallelGroup()
						.addComponent(maxPlayerLabel)
						.addComponent(maxPlayerField)));
		// @formatter:on

		if (!sizeChangable) {
			widthField.setEnabled(false);
			heightField.setEnabled(false);
		}
	}

	/**
	 * @return Text field with the map name
	 */
	public JTextField getNameField() {
		return nameField;
	}

	/**
	 * Load and display a map header
	 * 
	 * @param header
	 *            Header
	 */
	public void setHeader(MapFileHeader header) {
		nameField.setText(header.getName());
		descriptionField.setText(header.getDescription());
		width.setValue(Integer.valueOf(header.getWidth()));
		height.setValue(Integer.valueOf(header.getHeight()));
		minPlayer.setValue(Integer.valueOf(header.getMinPlayers()));
		maxPlayer.setValue(Integer.valueOf(header.getMaxPlayers()));
	}

	/**
	 * Gets a new header from inputfields
	 * 
	 * @return Header
	 */
	public MapFileHeader getHeader() {
		String name = nameField.getText();
		String description = descriptionField.getText();
		short width = this.width.getNumber().shortValue();
		short height = this.height.getNumber().shortValue();
		short minPlayer = this.minPlayer.getNumber().shortValue();
		short maxPlayer = this.maxPlayer.getNumber().shortValue();

		return new MapFileHeader(MapType.NORMAL, name, null, description, width, height, minPlayer, maxPlayer, new Date(),
				new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE]);
	}
}
