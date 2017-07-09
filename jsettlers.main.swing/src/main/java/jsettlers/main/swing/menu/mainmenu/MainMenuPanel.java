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
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
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
import jsettlers.graphics.localization.Labels;
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
import jsettlers.main.swing.settings.SettingsManager;
import jsettlers.main.swing.settings.UiPlayer;

/**
 * @author codingberlin
 */
public class MainMenuPanel extends SplitedBackgroundPanel {
	private static final long serialVersionUID = -6745474019479693347L;

	private static final Dimension PREFERRED_WEST_PANEL_SIZE = new Dimension(300, 300);

	private final JSettlersFrame settlersFrame;
	private final JPanel emptyPanel = new JPanel();
	private final OpenPanel joinMultiPlayerGamePanel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JPanel buttonPanel = new JPanel();

	public MainMenuPanel(JSettlersFrame settlersFrame, IMultiplayerConnector multiPlayerConnector) {
		this.settlersFrame = settlersFrame;

		OpenPanel openSinglePlayerPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), map -> settlersFrame.showNewSinglePlayerGameMenu(map));
		OpenPanel openSaveGamePanel = new OpenPanel(MapList.getDefaultList().getSavedMaps(), this::loadSavegame);
		OpenPanel newMultiPlayerGamePanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), this::showNewMultiplayerGamePanel);
		joinMultiPlayerGamePanel = new OpenPanel(new Vector<>(), this::showJoinMultiplayerGamePanel);
		SettingsMenuPanel settingsPanel = new SettingsMenuPanel(this);

		registerMenu("main-panel-new-single-player-game-button", e -> setCenter("main-panel-new-single-player-game-button", openSinglePlayerPanel));
		registerMenu("start-loadgame", e -> setCenter("start-loadgame", openSaveGamePanel));
		registerMenu("settings-title", e -> {
			setCenter("settings-title", settingsPanel);
			settingsPanel.initializeValues();
		});
		registerMenu("start-newmultiplayer", e -> setCenter("start-newmultiplayer-start", newMultiPlayerGamePanel));
		registerMenu("start-joinmultiplayer", e -> setCenter("start-joinmultiplayer-start", joinMultiPlayerGamePanel));
		registerMenu("main-panel-exit-button", e -> settlersFrame.exit());

		createStructure();
		SwingUtilities.updateComponentTreeUI(this);
		addListener(multiPlayerConnector);
	}

	private void registerMenu(String translationKey, ActionListener listener) {
		JToggleButton bt = new JToggleButton(Labels.getString(translationKey));
		bt.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		buttonGroup.add(bt);
		bt.addActionListener(listener);
		buttonPanel.add(bt);
	}

	private void loadSavegame(MapLoader map) {
		SavegameLoader savegameLoader = (SavegameLoader) map;

		if (savegameLoader != null) {
			PlayerSetting[] playerSettings = savegameLoader.getFileHeader().getPlayerSettings();

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

	private void showNewMultiplayerGamePanel(MapLoader map) {
		SettingsManager settingsManager = SettingsManager.getInstance();
		UiPlayer uiPlayer = settingsManager.getPlayer();
		IMultiplayerConnector connector = new MultiplayerConnector(settingsManager.get(SettingsManager.SETTING_SERVER),
				uiPlayer.getId(), uiPlayer.getName());
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

	private void addListener(IMultiplayerConnector multiPlayerConnector) {
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
		buttonPanel.setLayout(new GridLayout(0, 1, 20, 20));
		add(buttonPanel);
		add(emptyPanel);
		getTitleLabel().setVisible(false);
		buttonPanel.setPreferredSize(PREFERRED_WEST_PANEL_SIZE);
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
