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
import jsettlers.lookandfeel.LFStyle;
import jsettlers.main.components.general.VolumeSlider;
import jsettlers.main.components.mainmenu.MainMenuPanel;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author codingberlin
 */
public class SettingsMenuPanel extends JPanel {
	private final MainMenuPanel mainMenuPanel;
	private final JLabel playerNameLabel = new JLabel();
	private final JTextField playerNameField = new JTextField();
	private final JLabel volumeLabel = new JLabel();
	private final VolumeSlider volumeSlider = new VolumeSlider();
	private final JButton cancelButton = new JButton();
	private final JButton saveButton = new JButton();

	public SettingsMenuPanel(MainMenuPanel mainMenuPanel) {
		this.mainMenuPanel = mainMenuPanel;
		createStructure();
		setStyle();
		localize();
		addListener();
	}

	private void createStructure() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2, 20, 20));
		panel.add(playerNameLabel);
		panel.add(playerNameField);
		panel.add(volumeLabel);
		panel.add(volumeSlider);
		panel.add(cancelButton);
		panel.add(saveButton);
		add(panel);
	}

	private void setStyle() {
		saveButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		cancelButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		playerNameLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		volumeLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
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
			mainMenuPanel.reset();
		});
		cancelButton.addActionListener(e -> mainMenuPanel.reset());
	}

}
