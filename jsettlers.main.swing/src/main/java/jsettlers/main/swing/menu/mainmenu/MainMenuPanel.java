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
package jsettlers.main.swing.menu.mainmenu;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.Player;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.savegame.SavegameLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.MultiplayerConnector;
import jsettlers.main.swing.JSettlersFrame;
import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.components.SplitedBackgroundPanel;
import jsettlers.main.swing.menu.openpanel.OpenPanel;
import jsettlers.main.swing.menu.settingsmenu.SettingsMenuPanel;

/**
 * @author codingberlin
 */
public class MainMenuPanel extends SplitedBackgroundPanel {
	private static final long serialVersionUID = -6745474019479693347L;

	private static final Dimension PREFERRED_WEST_PANEL_SIZE = new Dimension(300, 300);

	private final JSettlersFrame settlersFrame;
	private final JPanel emptyPanel = new JPanel();
	private final SettingsMenuPanel settingsPanel;
	private final JButton exitButton = new JButton();
	private final JToggleButton newSinglePlayerGameButton = new JToggleButton();
	private final JToggleButton loadSaveGameButton = new JToggleButton();
	private final JToggleButton settingsButton = new JToggleButton();
	private final JToggleButton newNetworkGameButton = new JToggleButton();
	private final JToggleButton joinNetworkGameButton = new JToggleButton();
	private final OpenPanel openSinglePlayerPanel;
	private final OpenPanel openSaveGamePanel;
	private final OpenPanel newMultiPlayerGamePanel;
	private final OpenPanel joinMultiPlayerGamePanel;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public MainMenuPanel(JSettlersFrame settlersFrame, IMultiplayerConnector multiPlayerConnector) {
		this.settlersFrame = settlersFrame;

		openSinglePlayerPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), this::showNewSingleplayerGamePanel);
		openSaveGamePanel = new OpenPanel(MapList.getDefaultList().getSavedMaps(), this::loadSavegame);
		newMultiPlayerGamePanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), this::showNewMultiplayerGamePanel);
		joinMultiPlayerGamePanel = new OpenPanel(new Vector<MapLoader>(), this::showJoinMultiplayerGamePanel);
		settingsPanel = new SettingsMenuPanel(this);

		createStructure();
		setStyle();
		localize();
		addListener(multiPlayerConnector);
	}

	private void loadSavegame(MapLoader map) {
		SavegameLoader savegameLoader = (SavegameLoader) map;

		if (savegameLoader != null) {
			PlayerSetting[] playerSettings = PlayerSetting.createSettings(savegameLoader.getFileHeader().getPlayerConfigurations());

			byte playerId = 0; // find playerId of HUMAN player
			for (byte i = 0; i < playerSettings.length; i++) {
				if (playerSettings[i].getPlayerType() == EPlayerType.HUMAN) {
					playerId = i;
					break;
				}
			}

			JSettlersGame game = new JSettlersGame(savegameLoader, -1, playerId, playerSettings);
			IStartingGame startingGame = game.start();
			settlersFrame.showStartingGamePanel(startingGame);
		}
	}

	private void showNewSingleplayerGamePanel(MapLoader map) {
		settlersFrame.showNewSinglePlayerGameMenu(map);
	}

	private void showNewMultiplayerGamePanel(MapLoader map) {
		SettingsManager settingsManager = SettingsManager.getInstance();
		Player player = settingsManager.getPlayer();
		IMultiplayerConnector connector = new MultiplayerConnector(settingsManager.get(SettingsManager.SETTING_SERVER),
				player.getId(), player.getName());
		settlersFrame.showNewMultiPlayerGameMenu(map, connector);
	}

	private void showJoinMultiplayerGamePanel(MapLoader map) {
		NetworkGameMapLoader networkGameMapLoader = (NetworkGameMapLoader) map;
		IJoiningGame joiningGame = settlersFrame.getMultiPlayerConnector().joinMultiplayerGame(networkGameMapLoader.getJoinableGame());
		joiningGame.setListener(new IJoiningGameListener() {
			@Override
			public void joinProgressChanged(EProgressState state, float progress) {
			}

			@Override
			public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
				SwingUtilities.invokeLater(
						() -> settlersFrame.showJoinMultiplayerMenu(connector, MapList.getDefaultList().getMapById(networkGameMapLoader.getMapId())));
			}
		});
	}

	private void setStyle() {
		newSinglePlayerGameButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		loadSaveGameButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		settingsButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		newNetworkGameButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		joinNetworkGameButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		exitButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);

		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(openSinglePlayerPanel);
		SwingUtilities.updateComponentTreeUI(openSaveGamePanel);
		SwingUtilities.updateComponentTreeUI(settingsPanel);
		SwingUtilities.updateComponentTreeUI(newMultiPlayerGamePanel);
		SwingUtilities.updateComponentTreeUI(joinMultiPlayerGamePanel);
	}

	private void localize() {
		exitButton.setText(Labels.getString("main-panel-exit-button"));
		newSinglePlayerGameButton.setText(Labels.getString("main-panel-new-single-player-game-button"));
		loadSaveGameButton.setText(Labels.getString("start-loadgame"));
		settingsButton.setText(Labels.getString("settings-title"));
		newNetworkGameButton.setText(Labels.getString("start-newmultiplayer"));
		joinNetworkGameButton.setText(Labels.getString("start-joinmultiplayer"));
	}

	private void addListener(IMultiplayerConnector multiPlayerConnector) {
		newSinglePlayerGameButton.addActionListener(e -> setCenter("main-panel-new-single-player-game-button", openSinglePlayerPanel));
		loadSaveGameButton.addActionListener(e -> setCenter("start-loadgame", openSaveGamePanel));
		newNetworkGameButton.addActionListener(e -> setCenter("start-newmultiplayer-start", newMultiPlayerGamePanel));
		joinNetworkGameButton.addActionListener(e -> setCenter("start-joinmultiplayer-start", joinMultiPlayerGamePanel));
		exitButton.addActionListener(e -> settlersFrame.exit());
		settingsButton.addActionListener(e -> {
			setCenter("settings-title", settingsPanel);
			settingsPanel.initializeValues();
		});
		multiPlayerConnector
				.getJoinableMultiplayerGames()
				.setListener(networkGames -> {
					List<MapLoader> mapLoaders = networkGames.getItems()
							.stream()
							.map(NetworkGameMapLoader::new)
							.collect(Collectors.toList());
					SwingUtilities.invokeLater(() -> joinMultiPlayerGamePanel.setMapLoaders(mapLoaders));
				});
	}

	private void createStructure() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(0, 1, 20, 20));
		westPanel.add(newSinglePlayerGameButton);
		westPanel.add(loadSaveGameButton);
		westPanel.add(settingsButton);
		westPanel.add(newNetworkGameButton);
		westPanel.add(joinNetworkGameButton);
		westPanel.add(exitButton);
		add(westPanel);
		buttonGroup.add(newSinglePlayerGameButton);
		buttonGroup.add(loadSaveGameButton);
		buttonGroup.add(settingsButton);
		buttonGroup.add(newNetworkGameButton);
		buttonGroup.add(joinNetworkGameButton);
		add(emptyPanel);
		getTitleLabel().setVisible(false);
		westPanel.setPreferredSize(PREFERRED_WEST_PANEL_SIZE);
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
