/*******************************************************************************
 * Copyright (c) 2015
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.components.mainmenu;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.RemakeMapLoader;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.components.SplitedBackgroundPanel;
import jsettlers.main.components.openpanel.OpenPanel;
import jsettlers.main.components.settingsmenu.SettingsMenuPanel;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * @author codingberlin
 */
public class MainMenuPanel extends SplitedBackgroundPanel {

	public static final Dimension PREFERRED_EAST_SIZE = new Dimension(300, 300);
	private final SettlersFrame settlersFrame;
	private final JPanel emptyPanel = new JPanel();
	private final SettingsMenuPanel settingsPanel;
	private final JButton exitButton = new JButton();
	private final JToggleButton newSinglePlayerGameButton = new JToggleButton();
	private final JToggleButton loadSaveGameButton = new JToggleButton();
	private final JToggleButton settingsButton = new JToggleButton();
	private final OpenPanel openSinglePlayerPanel;
	private final OpenPanel openSaveGamePanel;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public MainMenuPanel(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		openSinglePlayerPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), new ShowNewSinglePlayerGame(settlersFrame));
		StartSaveGame startSaveGame = new StartSaveGame(settlersFrame);
		openSaveGamePanel = new OpenPanel(
				transformRemakeMapLoadersToMapLoaders(MapList.getDefaultList().getSavedMaps().getItems()),
				startSaveGame);
		startSaveGame.setRelatedOpenPanel(openSaveGamePanel);
		settingsPanel = new SettingsMenuPanel(this);
		createStructure();
		setStyle();
		localize();
		addListener();
	}

	private List<MapLoader> transformRemakeMapLoadersToMapLoaders(List<RemakeMapLoader> remakeMapLoaders) {
		List<MapLoader> mapLoaders = new Vector<MapLoader>();
		mapLoaders.addAll(remakeMapLoaders);
		return mapLoaders;
	}

	private void setStyle() {
		newSinglePlayerGameButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		loadSaveGameButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		settingsButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		exitButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);

		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(openSinglePlayerPanel);
		SwingUtilities.updateComponentTreeUI(openSaveGamePanel);
		SwingUtilities.updateComponentTreeUI(settingsPanel);
	}

	private void localize() {
		exitButton.setText(Labels.getString("main-panel-exit-button"));
		newSinglePlayerGameButton.setText(Labels.getString("main-panel-new-single-player-game-button"));
		loadSaveGameButton.setText(Labels.getString("start-loadgame"));
		settingsButton.setText(Labels.getString("settings-title"));
	}

	private void addListener() {
		newSinglePlayerGameButton.addActionListener(e -> setCenter("main-panel-new-single-player-game-button", openSinglePlayerPanel));
		loadSaveGameButton.addActionListener(e -> setCenter("start-loadgame", openSaveGamePanel));
		exitButton.addActionListener(e -> settlersFrame.exit());
		settingsButton.addActionListener(e -> {
			setCenter("settings-title", settingsPanel);
			settingsPanel.initializeValues();
		});
	}

	private void createStructure() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(0, 1, 20, 20));
		westPanel.add(newSinglePlayerGameButton);
		westPanel.add(loadSaveGameButton);
		westPanel.add(settingsButton);
		westPanel.add(exitButton);
		add(westPanel);
		buttonGroup.add(newSinglePlayerGameButton);
		buttonGroup.add(loadSaveGameButton);
		buttonGroup.add(settingsButton);
		add(emptyPanel);
		getTitleLabel().setVisible(false);
		westPanel.setPreferredSize(PREFERRED_EAST_SIZE);
	}

	public void reset() {
		setCenter(emptyPanel);
		getTitleLabel().setVisible(false);
		buttonGroup.clearSelection();
	}

	private void setCenter(final String titleKey, final JPanel panelToBeSet) {
		getTitleLabel().setText(Labels.getString(titleKey));
		getTitleLabel().setVisible(true);
		setCenter(panelToBeSet);
	}

	private void setCenter(final JPanel panelToBeSet) {
		remove(2);
		add(panelToBeSet);
		settlersFrame.revalidate();
		settlersFrame.repaint();

	}
}
