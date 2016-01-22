/*******************************************************************************
 * Copyright (c) 2015
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.components.joingame;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.*;
import jsettlers.logic.map.EMapStartResources;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.components.BackgroundPanel;
import jsettlers.lookandfeel.ui.BorderButton;
import jsettlers.main.JSettlersGame;
import jsettlers.main.swing.JSettlersSwingUtil;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * @author codingberlin
 */
public class JoinGamePanel extends BackgroundPanel {

	private final SettlersFrame settlersFrame;
	private final JLabel titleLabel = new JLabel();
	private final JPanel contentPanel = new JPanel();
	private final JPanel westPanel = new JPanel();
	private final JPanel mapPanel = new JPanel();
	private final JPanel settingsPanel = new JPanel();
	private final JPanel centerPanel = new JPanel();
	private final JLabel mapNameLabel = new JLabel();
	private final JLabel mapImage = new JLabel();
	private final JLabel numberOfPlayersLabel = new JLabel();
	private final JComboBox<Integer> numberOfPlayersComboBox = new JComboBox<>();
	private final JLabel peaceTimeLabel = new JLabel();
	private final JComboBox<EPeaceTime> peaceTimeComboBox = new JComboBox<>();
	private final JLabel startResourcesLabel = new JLabel();
	private final JComboBox<MapStartResourcesUIWrapper> startResourcesComboBox = new JComboBox<>();
	private final JPanel playerSlotPanelWrapper = new JPanel();
	private final JPanel playerSlotsPanel = new JPanel();
	private final JButton cancelButton = new JButton();
	private final JButton startGameButton = new JButton();
	private final JLabel slotsHeadlinePlayerNameLabel = new JLabel();
	private final JLabel slotsHeadlineCivilisation = new JLabel();
	private final JLabel slotsHeadlineType = new JLabel();
	private final JLabel slotsHeadlineMapSlot = new JLabel();
	private final JLabel slotsHeadlineTeam = new JLabel();
	private final JTextField chatInputField = new JTextField();
	private final JTextArea chatArea = new JTextArea();
	private final JButton sendChatMessageButton = new JButton();
	private MapLoader mapLoader;
	private List<PlayerSlot> playerSlots = new Vector<>();
	private PlayerSlotFactory playerSlotFactory;

	public JoinGamePanel(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		createStructure();
		setStyle();
		localize();
		addListener();
	}

	private void createStructure() {
		add(contentPanel);
		contentPanel.setLayout(new BorderLayout());
		JPanel titleLabelWrapper = new JPanel();
		contentPanel.add(titleLabelWrapper, BorderLayout.NORTH);
		titleLabelWrapper.add(titleLabel);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(westPanel, BorderLayout.WEST);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		westPanel.setLayout(new BorderLayout());
		westPanel.add(mapPanel, BorderLayout.NORTH);
		JPanel settingsPanelWrapper = new JPanel();
		westPanel.add(settingsPanelWrapper, BorderLayout.CENTER);
		settingsPanelWrapper.add(settingsPanel);
		settingsPanel.setLayout(new GridLayout(0, 2, 20, 20));
		mapPanel.setLayout(new BorderLayout());
		JPanel mapNameLabelWrapper = new JPanel();
		mapPanel.add(mapNameLabelWrapper, BorderLayout.NORTH);
		mapNameLabelWrapper.add(mapNameLabel);
		mapNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mapPanel.add(mapImage, BorderLayout.CENTER);
		mapImage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		settingsPanel.add(numberOfPlayersLabel);
		settingsPanel.add(numberOfPlayersComboBox);;
		settingsPanel.add(startResourcesLabel);
		settingsPanel.add(startResourcesComboBox);
		settingsPanel.add(peaceTimeLabel);
		settingsPanel.add(peaceTimeComboBox);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(playerSlotPanelWrapper, BorderLayout.NORTH);
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		JPanel chatInputPanel = new JPanel();
		chatInputPanel.setLayout(new BorderLayout());
		chatPanel.add(chatArea, BorderLayout.CENTER);
		chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
		chatInputPanel.add(chatInputField, BorderLayout.CENTER);
		chatInputPanel.add(sendChatMessageButton, BorderLayout.EAST);
		centerPanel.add(chatPanel, BorderLayout.CENTER);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotPanelWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		playerSlotsPanel.setLayout(new GridBagLayout());
		playerSlotPanelWrapper.add(playerSlotsPanel);
		JPanel southPanelWrapper = new JPanel();
		contentPanel.add(southPanelWrapper, BorderLayout.SOUTH);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(0, 3, 20, 20));
		southPanelWrapper.add(southPanel);
		southPanel.add(cancelButton);
		southPanel.add(startGameButton);
	}

	private void setStyle() {
		mapNameLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		numberOfPlayersLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		startResourcesLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		peaceTimeLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		playerSlotPanelWrapper.putClientProperty(LFStyle.KEY, LFStyle.PANEL_DARK);
		titleLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
		cancelButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		startGameButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		slotsHeadlinePlayerNameLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		slotsHeadlineCivilisation.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		slotsHeadlineType.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		slotsHeadlineMapSlot.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		slotsHeadlineTeam.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		sendChatMessageButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		chatInputField.putClientProperty(LFStyle.KEY, LFStyle.TEXT_DEFAULT);
		chatArea.putClientProperty(LFStyle.KEY, LFStyle.PANEL_DARK);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void localize() {
		numberOfPlayersLabel.setText(Labels.getString("join-game-panel-number-of-players"));
		startResourcesLabel.setText(Labels.getString("join-game-panel-start-resources"));
		cancelButton.setText(Labels.getString("join-game-panel-cancel"));
		startGameButton.setText(Labels.getString("join-game-panel-start"));
		peaceTimeLabel.setText(Labels.getString("join-game-panel-peace-time"));
		slotsHeadlinePlayerNameLabel.setText(Labels.getString("join-game-panel-player-name"));
		slotsHeadlineCivilisation.setText(Labels.getString("join-game-panel-civilisation"));
		slotsHeadlineType.setText(Labels.getString("join-game-panel-player-type"));
		slotsHeadlineMapSlot.setText(Labels.getString("join-game-panel-map-slot"));
		slotsHeadlineTeam.setText(Labels.getString("join-game-panel-team"));
		sendChatMessageButton.setText(Labels.getString("join-game-panel-send-chat-message"));
	}

	private void addListener() {
		numberOfPlayersComboBox.addActionListener(e -> updateNumberOfPlayerSlots());
	}

	public void setSinglePlayerMap(MapLoader mapLoader) {
		this.playerSlotFactory =  new SinglePlayerSlotFactory();
		numberOfPlayersComboBox.setEnabled(true);
		peaceTimeComboBox.setEnabled(true);
		startResourcesComboBox.setEnabled(true);
		startGameButton.setVisible(true);
		setChatVisible(false);
		cancelButton.addActionListener(e -> settlersFrame.showMainMenu());
		setStartButtonActionListener(e -> {
			long randomSeed = System.currentTimeMillis();
			PlayerSetting[] playerSettings = playerSlots.stream()
					.sorted((playerSlot, otherPlayerSlot) -> playerSlot.getSlot() - otherPlayerSlot.getSlot())
					.map(playerSlot -> new PlayerSetting(playerSlot.isAvailable(), playerSlot.getPlayerType(), playerSlot.getCivilisation(),
							playerSlot.getTeam()))
					.toArray(PlayerSetting[]::new);
			JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, playerSlots.get(0).getSlot(), playerSettings);
			IStartingGame startingGame = game.start();
			settlersFrame.showStartingGamePanel(startingGame);
		});
		setCancelButtonActionListener(e -> {
			settlersFrame.showMainMenu();
		});

		prepareUiFor(mapLoader);
	}

	private void setChatVisible(boolean isVisible) {
		chatArea.setVisible(isVisible);
		chatInputField.setVisible(isVisible);
		sendChatMessageButton.setVisible(isVisible);
		chatArea.setText("");
		chatInputField.setText("");
	}

	public void setNewMultiPlayerMap(MapLoader mapLoader, IMultiplayerConnector connector) {
		this.playerSlotFactory =  new HostOfMultiplayerPlayerSlotFactory();
		numberOfPlayersComboBox.setEnabled(false);
		peaceTimeComboBox.setEnabled(false);
		startResourcesComboBox.setEnabled(false);
		startGameButton.setVisible(true);
		setChatVisible(true);
		setStartButtonActionListener(e -> {});
		IJoiningGame joiningGame = connector.openNewMultiplayerGame(new OpenMultiPlayerGameInfo(mapLoader));
		joiningGame.setListener(new IJoiningGameListener() {
			@Override public void joinProgressChanged(EProgressState state, float progress) {

			}

			@Override public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
				initializeChatFor(connector);
				setStartButtonActionListener(e -> {
					connector.startGame();
				});
				connector.getPlayers().setListener(changingPlayers -> {
					onPlayersChanges(changingPlayers, connector);
				});
				connector.setMultiplayerListener(new IMultiplayerListener() {
					@Override public void gameIsStarting(IStartingGame game) {
						settlersFrame.showStartingGamePanel(game);
					}

					@Override public void gameAborted() {
						settlersFrame.showMainMenu();
					}
				});

			}
		});
		cancelButton.addActionListener(e -> {
			joiningGame.abort();
			settlersFrame.showMainMenu();
		});

		setCancelButtonActionListener(e -> {
			joiningGame.abort();
			settlersFrame.showMainMenu();
		});

		prepareUiFor(mapLoader);
	}

	public void setJoinMultiPlayerMap(IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap, MapLoader mapLoader) {
		this.playerSlotFactory =  new ClientOfMultiplayerPlayerSlotFactory();
		numberOfPlayersComboBox.setEnabled(false);
		peaceTimeComboBox.setEnabled(false);
		startResourcesComboBox.setEnabled(false);
		setChatVisible(true);
		cancelButton.addActionListener(e -> {
			settlersFrame.showMainMenu();
		});
		startGameButton.setVisible(false);

		prepareUiFor(mapLoader);

		joinMultiPlayerMap.getPlayers().setListener(changingPlayers -> {
			onPlayersChanges(changingPlayers, joinMultiPlayerMap);
		});
		joinMultiPlayerMap.setMultiplayerListener(new IMultiplayerListener() {
			@Override public void gameIsStarting(IStartingGame game) {
				settlersFrame.showStartingGamePanel(game);
			}

			@Override public void gameAborted() {
				settlersFrame.showMainMenu();
			}
		});
		initializeChatFor(joinMultiPlayerMap);
	}

	private void initializeChatFor(IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap) {
		joinMultiPlayerMap.setChatListener(new IChatMessageListener() {
			@Override public void chatMessageReceived(String authorId, String message) {
				chatArea.append(authorId + ": " + message + "\n");
			}

			@Override public void systemMessageReceived(IMultiplayerPlayer author, ENetworkMessage message) {
				chatArea.append(message.name() + "\n");
			}
		});
		Arrays.asList(sendChatMessageButton.getActionListeners()).stream().forEach(sendChatMessageButton::removeActionListener);
		sendChatMessageButton.addActionListener(e -> {
			String message = chatInputField.getText();
			if (!message.equals("")) {
				joinMultiPlayerMap.sendChatMessage(message);
				chatInputField.setText("");
			}
		});
	}

	private void onPlayersChanges(ChangingList<? extends IMultiplayerPlayer> changingPlayers, IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap) {
		List<? extends IMultiplayerPlayer> players = changingPlayers.getItems();
		String myId = SettingsManager.getInstance().get(SettingsManager.SETTING_UUID);
		for (int i = 0; i < players.size(); i++) {
			PlayerSlot playerSlot = playerSlots.get(i);
			IMultiplayerPlayer player = players.get(i);
			playerSlot.setPlayerName(player.getName());
			playerSlot.setReady(player.isReady());
			if (player.getId().equals(myId)) {
				playerSlot.setReadyButtonEnabled(true);
				playerSlot.informGameAboutReady(joinMultiPlayerMap);
			} else {
				playerSlot.setReadyButtonEnabled(false);
			}
		}
		for (int i = players.size(); i < playerSlots.size(); i++) {
			playerSlots.get(i).setTypeComboBox(EPlayerType.AI_VERY_HARD);
		}
		setCancelButtonActionListener(e -> {
			joinMultiPlayerMap.abort();
			settlersFrame.showMainMenu();
		});
	};

	private void prepareUiFor(MapLoader mapLoader) {
		this.mapLoader = mapLoader;
		mapNameLabel.setText(mapLoader.getMapName());
		mapImage.setIcon(new ImageIcon(JSettlersSwingUtil.createBufferedImageFrom(mapLoader)));
		titleLabel.setText(Labels.getString("join-game-panel-new-single-player-game-title"));
		peaceTimeComboBox.removeAllItems();
		peaceTimeComboBox.addItem(EPeaceTime.WITHOUT);
		startResourcesComboBox.removeAllItems();
		Arrays.asList(EMapStartResources.values()).stream()
				.map(MapStartResourcesUIWrapper::new)
				.forEach(startResourcesComboBox::addItem);
		startResourcesComboBox.setSelectedIndex(EMapStartResources.HIGH_GOODS.value-1);
		resetNumberOfPlayersComboBox();
		buildPlayerSlots();
		updateNumberOfPlayerSlots();
	}

	private void buildPlayerSlots() {
		int maximumNumberOfPlayers = this.mapLoader.getMaxPlayers();
		playerSlots.clear();
		for (byte i = 0; i < maximumNumberOfPlayers; i++) {
			PlayerSlot playerSlot = playerSlotFactory.createPlayerSlot(i, this.mapLoader);
			playerSlots.add(playerSlot);
		}
		for (byte i = 0; i < playerSlots.size(); i++) {
			playerSlots.get(i).setSlot(i);
			playerSlots.get(i).setTeam(i);
		}
	}

	private void setStartButtonActionListener(ActionListener actionListener) {
		ActionListener[] actionListeners = startGameButton.getActionListeners();
		Arrays.asList(actionListeners).stream().forEach(startGameButton::removeActionListener);
		startGameButton.addActionListener(actionListener);
	}

	private void setCancelButtonActionListener(ActionListener actionListener) {
		ActionListener[] actionListeners = cancelButton.getActionListeners();
		Arrays.asList(actionListeners).stream().forEach(cancelButton::removeActionListener);
		cancelButton.addActionListener(actionListener);
	}

	private void resetNumberOfPlayersComboBox() {
		numberOfPlayersComboBox.removeAllItems();
		for (int i = 1; i < mapLoader.getMaxPlayers() + 1; i++) {
			numberOfPlayersComboBox.addItem(i);
		}
		numberOfPlayersComboBox.setSelectedIndex(mapLoader.getMaxPlayers()-1);
	}

	private void updateNumberOfPlayerSlots() {
		if (playerSlotFactory == null || numberOfPlayersComboBox.getSelectedItem() == null) {
			return;
		}
		playerSlotsPanel.removeAll();
		addPlayerSlotHeadline();
		for (int i = 0; i<playerSlots.size(); i++) {
			if (i < (int) numberOfPlayersComboBox.getSelectedItem()) {
				playerSlots.get(i).addTo(playerSlotsPanel, i+1);
			} else {
				playerSlots.get(i).setAvailable(false);
			}
		}
		SwingUtilities.updateComponentTreeUI(playerSlotsPanel);
		SlotToggleGroup slotToggleGroup = new SlotToggleGroup();
		playerSlots.stream().forEach(slotToggleGroup::add);
	}

	private void addPlayerSlotHeadline() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotsPanel.add(slotsHeadlinePlayerNameLabel, constraints);
		constraints.gridx = 5;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotsPanel.add(slotsHeadlineCivilisation, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 7;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotsPanel.add(slotsHeadlineType, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 11;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotsPanel.add(slotsHeadlineMapSlot, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 12;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		playerSlotsPanel.add(slotsHeadlineTeam, constraints);
	}

	private enum EPeaceTime {
		WITHOUT;

		@Override public String toString() {
			return Labels.getString("peace-time-" + name());
		}
	}
}
