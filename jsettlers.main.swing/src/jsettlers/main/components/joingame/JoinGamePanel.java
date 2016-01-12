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

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.original.OriginalMapFileDataStructs;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.components.BackgroundPanel;
import jsettlers.main.swing.JSettlersSwingUtil;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;

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
	private final JComboBox<OriginalMapFileDataStructs.EMapStartResources> startResourcesComboBox = new JComboBox<>();
	private final JPanel playerSlotPanel = new JPanel();
	private final JButton cancelButton = new JButton();
	private final JButton startGameButton = new JButton();
	private final PlayerSlot[] playerSlots = new PlayerSlot[20];
	private MapLoader mapLoader;


	//TODO: spielerslots
	//TODO: anzahl spieler combobox füllen
	//TODO: Warenbestand combobox füllen
	//TODO: Spielstart


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
		centerPanel.add(playerSlotPanel, BorderLayout.NORTH);
		playerSlotPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JPanel headlinePanel = new JPanel();
		playerSlotPanel.add(headlinePanel);
		//TODO: labels auslagern, damit die gestyled werden können
		headlinePanel.add(new JLabel(Labels.getString("join-game-panel-player-name")));
		headlinePanel.add(new JLabel(Labels.getString("join-game-panel-civilisation")));
		headlinePanel.add(new JLabel(Labels.getString("join-game-panel-player-type")));
		headlinePanel.add(new JLabel(Labels.getString("join-game-panel-map-slot")));
		headlinePanel.add(new JLabel(Labels.getString("join-game-panel-team")));
		JPanel southPanelWrapper = new JPanel();
		contentPanel.add(southPanelWrapper, BorderLayout.SOUTH);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(0, 3, 20, 20));
		southPanelWrapper.add(southPanel);
		southPanel.add(cancelButton);
		southPanel.add(startGameButton);
		for (int i = 0; i < playerSlots.length; i++) {
			playerSlots[i] = new PlayerSlot();
			//TODO: Array wird wohl nicht funktionieren, da setInvisible trozdem Platz beansprucht
			playerSlotPanel.add(playerSlots[i]);
		}
	}

	private void setStyle() {
		mapNameLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		numberOfPlayersLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		startResourcesLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		peaceTimeLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		playerSlotPanel.putClientProperty(LFStyle.KEY, LFStyle.PANEL_DARK);
		titleLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
		cancelButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		startGameButton.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void localize() {
		numberOfPlayersLabel.setText(Labels.getString("join-game-panel-number-of-players"));
		startResourcesLabel.setText(Labels.getString("join-game-panel-start-resources"));
		cancelButton.setText(Labels.getString("join-game-panel-cancel"));
		startGameButton.setText(Labels.getString("join-game-panel-start"));
		peaceTimeLabel.setText(Labels.getString("join-game-panel-peace-time"));
	}

	private void addListener() {
		cancelButton.addActionListener(e -> settlersFrame.showMainMenu());
	}

	public void setSinglePlayerMap(MapLoader mapLoader) {
		this.mapLoader = mapLoader;
		mapNameLabel.setText(mapLoader.getMapName());
		mapImage.setIcon(new ImageIcon(JSettlersSwingUtil.createBufferedImageFrom(mapLoader)));
		titleLabel.setText(Labels.getString("join-game-panel-new-single-player-game-title"));
		peaceTimeComboBox.removeAllItems();
		peaceTimeComboBox.addItem(EPeaceTime.WITHOUT);
		resetNumberOfPlayersComboBox();
		resetVisibilityOfPlayerSlots();
	}

	private void resetNumberOfPlayersComboBox() {
		numberOfPlayersComboBox.removeAllItems();
		for (int i = 0; i < mapLoader.getMaxPlayers(); i++) {
			numberOfPlayersComboBox.addItem(i);
		}
		numberOfPlayersComboBox.setSelectedItem(mapLoader.getMaxPlayers());
	}

	private void resetVisibilityOfPlayerSlots() {
		int numberOfPlayers = (Integer) numberOfPlayersComboBox.getSelectedItem();
		for (int i = 0; i < playerSlots.length; i++) {
			playerSlots[i].setVisible(i < numberOfPlayers);
		}
	}

	private enum EPeaceTime {
		WITHOUT;

		@Override public String toString() {
			return Labels.getString("peace-time-" + name());
		}
	}
}
