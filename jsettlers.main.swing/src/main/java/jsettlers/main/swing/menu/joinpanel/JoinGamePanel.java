/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.main.swing.menu.joinpanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.menu.ENetworkMessage;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IChatMessageListener;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.swing.settings.SettingsManager;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.swing.JSettlersFrame;
import jsettlers.main.swing.JSettlersSwingUtil;
import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.GBC;
import jsettlers.main.swing.lookandfeel.components.BackgroundPanel;
import jsettlers.main.swing.menu.joinpanel.slots.PlayerSlot;
import jsettlers.main.swing.menu.joinpanel.slots.SlotToggleGroup;
import jsettlers.main.swing.menu.joinpanel.slots.factories.ClientOfMultiplayerPlayerSlotFactory;
import jsettlers.main.swing.menu.joinpanel.slots.factories.HostOfMultiplayerPlayerSlotFactory;
import jsettlers.main.swing.menu.joinpanel.slots.factories.IPlayerSlotFactory;
import jsettlers.main.swing.menu.joinpanel.slots.factories.SinglePlayerSlotFactory;

import java8.util.J8Arrays;

/**
 * Layout:
 * 
 * +---------------------------------------------------------------+
 * |              titleLabel                                       |
 * +------------------------+--------------------------------------+
 * |                        |       playerSlotsPanel               |
 * |                        +--------------------------------------+
 * | westPanel              |       chatPanel                      |
 * |                        +--------------------------------------+
 * |                        |       southPanel                     |
 * +------------------------+--------------------------------------+
 * 
 * @author codingberlin
 */
public class JoinGamePanel extends BackgroundPanel {
	private static final long serialVersionUID = -1186791399814385303L;

	private final JSettlersFrame settlersFrame;
	private final JLabel titleLabel = new JLabel();
	private final JPanel westPanel = new JPanel();
	private final JPanel mapPanel = new JPanel();
	private final JPanel settingsPanel = new JPanel();
	private final JLabel mapNameLabel = new JLabel();
	private final JLabel mapImage = new JLabel();
	private final JLabel numberOfPlayersLabel = new JLabel();
	private final JComboBox<Integer> numberOfPlayersComboBox = new JComboBox<>();
	private final JLabel peaceTimeLabel = new JLabel();
	private final JComboBox<EPeaceTime> peaceTimeComboBox = new JComboBox<>();
	private final JLabel startResourcesLabel = new JLabel();
	private final JComboBox<MapStartResourcesUIWrapper> startResourcesComboBox = new JComboBox<>();
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
	private final List<PlayerSlot> playerSlots = new ArrayList<>();
	private IPlayerSlotFactory playerSlotFactory;

	public JoinGamePanel(JSettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		createStructure();
		setStyle();
		localize();
		addListener();
	}

	private void createStructure() {
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
		settingsPanel.add(numberOfPlayersComboBox);
		settingsPanel.add(startResourcesLabel);
		settingsPanel.add(startResourcesComboBox);
		settingsPanel.add(peaceTimeLabel);
		settingsPanel.add(peaceTimeComboBox);
		sendChatMessageButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout(0, 10));
		JPanel chatInputPanel = new JPanel();
		chatInputPanel.setLayout(new BorderLayout(10, 0));
		chatPanel.add(chatArea, BorderLayout.CENTER);
		chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
		chatInputPanel.add(chatInputField, BorderLayout.CENTER);
		chatInputPanel.add(sendChatMessageButton, BorderLayout.EAST);
		
		playerSlotsPanel.setLayout(new GridBagLayout());
		playerSlotsPanel.setBorder(new EmptyBorder(20, 25, 20, 20));
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));
		southPanel.add(cancelButton);
		startGameButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));
		southPanel.add(startGameButton);
		
		JPanel content = new JPanel(new GridBagLayout());
		content.add(titleLabel, new GBC().grid(0, 0).size(2, 1).fillx().insets(0, 0, 30, 0));
		content.add(westPanel, new GBC().grid(0, 1).size(1, 3).filly());
		content.add(new JScrollPane(playerSlotsPanel), new GBC().grid(1, 1).fillx().filly());
		content.add(chatPanel, new GBC().grid(1, 2).fillx().filly().insets(30, 0, 0, 0));
		content.add(southPanel, new GBC().grid(1, 3).fillx().insets(30, 0, 0, 0));
		add(content);
	}

	private void setStyle() {
		mapNameLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_LONG);
		numberOfPlayersLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_SHORT);
		startResourcesLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_SHORT);
		peaceTimeLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_SHORT);
		titleLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_HEADER);
		cancelButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		startGameButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		slotsHeadlinePlayerNameLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_DYNAMIC);
		slotsHeadlineCivilisation.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_DYNAMIC);
		slotsHeadlineType.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_DYNAMIC);
		slotsHeadlineMapSlot.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_DYNAMIC);
		slotsHeadlineTeam.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_DYNAMIC);
		sendChatMessageButton.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_MENU);
		chatInputField.putClientProperty(ELFStyle.KEY, ELFStyle.TEXT_DEFAULT);
		chatArea.putClientProperty(ELFStyle.KEY, ELFStyle.PANEL_DARK);
		startResourcesComboBox.putClientProperty(ELFStyle.KEY, ELFStyle.COMBOBOX);
		numberOfPlayersComboBox.putClientProperty(ELFStyle.KEY, ELFStyle.COMBOBOX);
		peaceTimeComboBox.putClientProperty(ELFStyle.KEY, ELFStyle.COMBOBOX);
		chatArea.putClientProperty(ELFStyle.KEY, ELFStyle.TEXT_DEFAULT);
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
		this.playerSlotFactory = new SinglePlayerSlotFactory();
		titleLabel.setText(Labels.getString("join-game-panel-new-single-player-game-title"));
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
					.map(playerSlot -> {
						if (playerSlot.isAvailable()) {
							return new PlayerSetting(playerSlot.getPlayerType(), playerSlot.getCivilisation(),
									playerSlot.getTeam());
						} else {
							return new PlayerSetting();
						}
					})
					.toArray(PlayerSetting[]::new);
			JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, playerSlots.get(0).getSlot(), playerSettings);
			IStartingGame startingGame = game.start();
			settlersFrame.showStartingGamePanel(startingGame);
		});
		setCancelButtonActionListener(e -> settlersFrame.showMainMenu());

		prepareUiFor(mapLoader);
	}

	public void setNewMultiPlayerMap(MapLoader mapLoader, IMultiplayerConnector connector) {
		this.playerSlotFactory = new HostOfMultiplayerPlayerSlotFactory();
		titleLabel.setText(Labels.getString("join-game-panel-new-multi-player-game-title"));
		numberOfPlayersComboBox.setEnabled(false);
		peaceTimeComboBox.setEnabled(false);
		startResourcesComboBox.setEnabled(false);
		startGameButton.setVisible(true);
		setChatVisible(true);
		setStartButtonActionListener(e -> {
		});
		IJoiningGame joiningGame = connector.openNewMultiplayerGame(new OpenMultiPlayerGameInfo(mapLoader));
		joiningGame.setListener(new IJoiningGameListener() {
			@Override
			public void joinProgressChanged(EProgressState state, float progress) {

			}

			@Override
			public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
				SwingUtilities.invokeLater(() -> {
					initializeChatFor(connector);
					setStartButtonActionListener(e -> connector.startGame());
					connector.getPlayers().setListener(changingPlayers -> onPlayersChanges(changingPlayers, connector));
					connector.setMultiplayerListener(new IMultiplayerListener() {
						@Override
						public void gameIsStarting(IStartingGame game) {
							settlersFrame.showStartingGamePanel(game);
						}

						@Override
						public void gameAborted() {
							settlersFrame.showMainMenu();
						}
					});

					onPlayersChanges(connector.getPlayers(), connector); // init the UI with the players
				});
			}
		});

		setCancelButtonActionListener(e -> {
			joiningGame.abort();
			settlersFrame.showMainMenu();
		});

		prepareUiFor(mapLoader);
	}

	public void setJoinMultiPlayerMap(IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap, MapLoader mapLoader) {
		playerSlotFactory = new ClientOfMultiplayerPlayerSlotFactory();
		titleLabel.setText(Labels.getString("join-game-panel-join-multi-player-game-title"));
		numberOfPlayersComboBox.setEnabled(false);
		peaceTimeComboBox.setEnabled(false);
		startResourcesComboBox.setEnabled(false);
		setChatVisible(true);
		cancelButton.addActionListener(e -> settlersFrame.showMainMenu());
		startGameButton.setVisible(false);

		prepareUiFor(mapLoader);

		joinMultiPlayerMap.getPlayers().setListener(changingPlayers -> onPlayersChanges(changingPlayers, joinMultiPlayerMap));
		joinMultiPlayerMap.setMultiplayerListener(new IMultiplayerListener() {
			@Override
			public void gameIsStarting(IStartingGame game) {
				settlersFrame.showStartingGamePanel(game);
			}

			@Override
			public void gameAborted() {
				settlersFrame.showMainMenu();
			}
		});
		initializeChatFor(joinMultiPlayerMap);

		onPlayersChanges(joinMultiPlayerMap.getPlayers(), joinMultiPlayerMap); // init the UI with the players
	}

	private void initializeChatFor(IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap) {
		joinMultiPlayerMap.setChatListener(new IChatMessageListener() {
			@Override
			public void chatMessageReceived(String authorId, String message) {
				chatArea.append(authorId + ": " + message + "\n");
			}

			@Override
			public void systemMessageReceived(IMultiplayerPlayer author, ENetworkMessage message) {
				chatArea.append(Labels.getString("network-message-" + message.name()) + "\n");
			}
		});
		ActionListener sendChatMessage = e -> {
			String message = chatInputField.getText();
			if (!message.equals("")) {
				joinMultiPlayerMap.sendChatMessage(message);
				chatInputField.setText("");
			}
		};
		J8Arrays.stream(sendChatMessageButton.getActionListeners()).forEach(sendChatMessageButton::removeActionListener);
		J8Arrays.stream(chatInputField.getActionListeners()).forEach(chatInputField::removeActionListener);
		sendChatMessageButton.addActionListener(sendChatMessage);
		chatInputField.addActionListener(sendChatMessage);

	}

	private void setChatVisible(boolean isVisible) {
		chatArea.setVisible(isVisible);
		chatInputField.setVisible(isVisible);
		sendChatMessageButton.setVisible(isVisible);
		chatArea.setText("");
		chatInputField.setText("");
	}

	private void onPlayersChanges(ChangingList<? extends IMultiplayerPlayer> changingPlayers, IJoinPhaseMultiplayerGameConnector joinMultiPlayerMap) {
		SwingUtilities.invokeLater(() -> {
			List<? extends IMultiplayerPlayer> players = changingPlayers.getItems();
			String myId = SettingsManager.getInstance().get(SettingsManager.SETTING_UUID);
			for (int i = 0; i < players.size(); i++) {
				PlayerSlot playerSlot = playerSlots.get(i);
				IMultiplayerPlayer player = players.get(i);
				playerSlot.setPlayerName(player.getName());
				playerSlot.setReady(player.isReady());
				playerSlot.setPlayerType(EPlayerType.HUMAN, false);
				if (player.getId().equals(myId)) {
					playerSlot.setReadyButtonEnabled(true);
					playerSlot.informGameAboutReady(joinMultiPlayerMap);
				} else {
					playerSlot.setReadyButtonEnabled(false);
				}
			}
			for (int i = players.size(); i < playerSlots.size(); i++) {
				playerSlots.get(i).setPlayerType(EPlayerType.AI_VERY_HARD, false);
			}
			setCancelButtonActionListener(e -> {
				joinMultiPlayerMap.abort();
				settlersFrame.showMainMenu();
			});
		});
	}

	private void prepareUiFor(MapLoader mapLoader) {
		this.mapLoader = mapLoader;
		mapNameLabel.setText(mapLoader.getMapName());
		mapImage.setIcon(new ImageIcon(JSettlersSwingUtil.createBufferedImageFrom(mapLoader)));
		peaceTimeComboBox.removeAllItems();
		peaceTimeComboBox.addItem(EPeaceTime.WITHOUT);
		startResourcesComboBox.removeAllItems();
		J8Arrays.stream(EMapStartResources.values())
				.map(MapStartResourcesUIWrapper::new)
				.forEach(startResourcesComboBox::addItem);
		startResourcesComboBox.setSelectedIndex(EMapStartResources.HIGH_GOODS.value - 1);
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

		PlayerSetting[] playerSettings = mapLoader.getFileHeader().getPlayerSettings();
		for (byte i = 0; i < playerSlots.size(); i++) {
			PlayerSlot playerSlot = playerSlots.get(i);
			PlayerSetting playerSetting = playerSettings[i];

			playerSlot.setSlot(i);

			if (playerSetting.getTeamId() != null) {
				playerSlot.setTeam(playerSetting.getTeamId(), false);
			} else {
				playerSlot.setTeam(i);
			}

			if (playerSetting.getCivilisation() != null) {
				playerSlot.setCivilisation(playerSetting.getCivilisation(), false);
			}

			if (playerSetting.getPlayerType() != null) {
				playerSlot.setPlayerType(playerSetting.getPlayerType(), false);
			}
		}
	}

	private void setStartButtonActionListener(ActionListener actionListener) {
		ActionListener[] actionListeners = startGameButton.getActionListeners();
		J8Arrays.stream(actionListeners).forEach(startGameButton::removeActionListener);
		startGameButton.addActionListener(actionListener);
	}

	private void setCancelButtonActionListener(ActionListener actionListener) {
		ActionListener[] actionListeners = cancelButton.getActionListeners();
		J8Arrays.stream(actionListeners).forEach(cancelButton::removeActionListener);
		cancelButton.addActionListener(actionListener);
	}

	private void resetNumberOfPlayersComboBox() {
		numberOfPlayersComboBox.removeAllItems();
		for (int i = 1; i < mapLoader.getMaxPlayers() + 1; i++) {
			numberOfPlayersComboBox.addItem(i);
		}
		numberOfPlayersComboBox.setSelectedIndex(mapLoader.getMaxPlayers() - 1);
	}

	private void updateNumberOfPlayerSlots() {
		if (playerSlotFactory == null || numberOfPlayersComboBox.getSelectedItem() == null) {
			return;
		}
		playerSlotsPanel.removeAll();
		addPlayerSlotHeadline();
		for (int i = 0; i < playerSlots.size(); i++) {
			if (i < (int) numberOfPlayersComboBox.getSelectedItem()) {
				playerSlots.get(i).addTo(playerSlotsPanel, i + 1);
			} else {
				playerSlots.get(i).setAvailable(false);
			}
		}
		SwingUtilities.updateComponentTreeUI(playerSlotsPanel);
		SlotToggleGroup slotToggleGroup = new SlotToggleGroup();
		playerSlots.forEach(slotToggleGroup::add);
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

		@Override
		public String toString() {
			return Labels.getString("peace-time-" + name());
		}
	}
}
