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
package jsettlers.main.components.settingsmenu;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author codingberlin
 */
public class SettingsMenuPanel extends JPanel {
	private final SettlersFrame settlersFrame;
	private final JLabel playerNameLabel = new JLabel();
	private final JTextField playerNameField = new JTextField();
	private final JLabel volumeLabel = new JLabel();
	private final JSlider volumeSlider = new JSlider();
	private final JButton cancelButton = new JButton();
	private final JButton saveButton = new JButton();


	public SettingsMenuPanel(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		createStructure();
		localize();
		addListener();
	}

	private void createStructure() {
		setLayout(new GridLayout(3, 2));
		add(playerNameLabel);
		add(playerNameField);
		add(volumeLabel);
		add(volumeSlider);
		add(cancelButton);
		add(saveButton);
	}

	private void localize() {
		playerNameLabel.setText(Labels.getString("settings-name"));
		volumeLabel.setText(Labels.getString("settings-volume"));
		saveButton.setText(Labels.getString("settings-ok"));
		cancelButton.setText(Labels.getString("settings-back"));
	}

	public void initializeValues() {
		SettingsManager settingsManager = SettingsManager.getInstance();
		playerNameField.setText(settingsManager.getPlayer().getName());
		volumeSlider.setValue((int) (settingsManager.getVolume() * 100));
	}

	private void addListener() {
		saveButton.addActionListener(e -> {
			SettingsManager settingsManager = SettingsManager.getInstance();
			settingsManager.set(SettingsManager.SETTING_USERNAME, playerNameField.getText());
			settingsManager.set(SettingsManager.SETTING_VOLUME, (volumeSlider.getValue() / 100D) + "");
			SwingUtilities.invokeLater(settlersFrame::showMainMenu);
		});
		cancelButton.addActionListener(e -> settlersFrame.showMainMenu());
	}

}
