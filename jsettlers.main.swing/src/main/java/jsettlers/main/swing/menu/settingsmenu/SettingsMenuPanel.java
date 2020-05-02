/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.main.swing.menu.settingsmenu;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import go.graphics.swing.contextcreator.BackendSelector;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.menu.mainmenu.MainMenuPanel;
import jsettlers.main.swing.settings.SettingsManager;

/**
 * Panel with the settings of the game
 */
public class SettingsMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Reference to main panel
	 */
	private final MainMenuPanel mainMenuPanel;

	/**
	 * Name of the player
	 */
	private final JTextField playerNameField = new JTextField();
	private final SettingsSlider volumeSlider = new SettingsSlider("%", 0,100, null);
	private final SettingsSlider fpsLimitSlider = new SettingsSlider("fps", 0,240, "timerless redraw");
	private final SettingsSlider guiScaleSlider = new SettingsSlider("%", 50,400, "system default");
	private final BackendSelector backendSelector = new BackendSelector();

	/**
	 * Panel with the settings entries (2 column grid)
	 */
	private final JPanel settinsgPanel = new JPanel();

	/**
	 * Constructor
	 *
	 * @param mainMenuPanel
	 *            Reference to main panel
	 */
	public SettingsMenuPanel(MainMenuPanel mainMenuPanel) {
		this.mainMenuPanel = mainMenuPanel;
		setLayout(new BorderLayout());
		settinsgPanel.setLayout(new GridLayout(0, 2, 10, 10));
		add(settinsgPanel, BorderLayout.NORTH);

		playerNameField.putClientProperty(ELFStyle.KEY, ELFStyle.TEXT_DEFAULT);
		backendSelector.putClientProperty(ELFStyle.KEY, ELFStyle.COMBOBOX);
		
		SwingUtilities.updateComponentTreeUI(playerNameField);
		addSetting("settings-name", playerNameField);

		addSetting("settings-volume", volumeSlider);
		
		addSetting("settings-fps-limit", fpsLimitSlider);
		
		addSetting("settings-backend", backendSelector);

		addSetting("settings-gui-scale", guiScaleSlider);
		
		initButton();
	}

	/**
	 * Add a settings entry
	 *
	 * @param translationKey
	 *            Translation key to read translation
	 * @param settingComponent
	 *            Component to display
	 */
	private void addSetting(String translationKey, JComponent settingComponent) {
		JLabel header = new JLabel(Labels.getString(translationKey));
		header.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_SHORT);
		settinsgPanel.add(header);
		settinsgPanel.add(settingComponent);
	}

	/**
	 * Init the Button
	 */
	private void initButton() {
		JButton cancelButton = new JButton(Labels.getString("settings-back"));
		cancelButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		cancelButton.addActionListener(e -> mainMenuPanel.reset());

		JButton saveButton = new JButton(Labels.getString("settings-ok"));
		saveButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		saveButton.addActionListener(e -> {
			SettingsManager settingsManager = SettingsManager.getInstance();
			settingsManager.setUserName(playerNameField.getText());
			settingsManager.setVolume(volumeSlider.getValue() / 100f);
			settingsManager.setFpsLimit(fpsLimitSlider.getValue());
			settingsManager.setBackend(backendSelector.getSelectedItem()+"");
			settingsManager.setGuiScale(guiScaleSlider.getValue()/100f);
			mainMenuPanel.reset();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 0, 20, 20));
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void initializeValues() {
		SettingsManager settingsManager = SettingsManager.getInstance();
		playerNameField.setText(settingsManager.getPlayer().getName());
		volumeSlider.setValue((int) (settingsManager.getVolume() * 100));
		fpsLimitSlider.setValue(settingsManager.getFpsLimit());
		backendSelector.setSelectedItem(settingsManager.getBackend());
		guiScaleSlider.setValue(Math.round(settingsManager.getGuiScale()*100));
	}
}
