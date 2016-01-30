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

import jsettlers.common.*;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.graphics.image.*;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.lookandfeel.LFStyle;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author codingberlin
 */
public class PlayerSlot {

	private final JLabel playerNameLabel = new JLabel();
	private final JComboBox<CivilisationUiWrapper> civilisationComboBox = new JComboBox<>();
	private final JComboBox<PlayerTypeUiWrapper> typeComboBox = new JComboBox<>();
	private final JComboBox<Byte> slotComboBox = new JComboBox<>();
	private final JComboBox<Byte> teamComboBox = new JComboBox<>();
	private final JButton readyButton = new JButton();
	private byte oldSlotValue;
	private SlotListener slotListener;
	private boolean isAvailable;
	private boolean isReady = true;
	private IJoinPhaseMultiplayerGameConnector gameToBeInformedAboutReady;

	public PlayerSlot() {
		isAvailable = true;
		setStyle();
		localize();
		addListener();
		initializeComboBoxes();
	}

	public void addTo(JPanel panel, int row) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = row + 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(readyButton, constraints);
		constraints.gridx = 1;
		constraints.gridy = row + 1;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(playerNameLabel, constraints);
		constraints.gridx = 5;
		constraints.gridy = row + 1;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(civilisationComboBox, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 7;
		constraints.gridy = row + 1;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(typeComboBox, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 11;
		constraints.gridy = row + 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(slotComboBox, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 12;
		constraints.gridy = row + 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(teamComboBox, constraints);
	}

	private void setStyle() {
		playerNameLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_DYNAMIC);
		teamComboBox.putClientProperty(LFStyle.KEY, LFStyle.COMBOBOX);
		slotComboBox.putClientProperty(LFStyle.KEY, LFStyle.COMBOBOX);
		typeComboBox.putClientProperty(LFStyle.KEY, LFStyle.COMBOBOX);
		civilisationComboBox.putClientProperty(LFStyle.KEY, LFStyle.COMBOBOX);
		updateReadyButtonStyle();
	}

	private void updateReadyButtonStyle() {
		if (isReady()){
			readyButton.setIcon(new ImageIcon(((SingleImage) ImageProvider.getInstance().getSettlerSequence(2, 17).getImage(0)).generateBufferedImage()));
			readyButton.setPressedIcon(new ImageIcon(((SingleImage) ImageProvider.getInstance().getSettlerSequence(2, 17).getImage(1)).generateBufferedImage()));
		} else {
			readyButton.setIcon(new ImageIcon(((SingleImage) ImageProvider.getInstance().getSettlerSequence(2, 18).getImage(0)).generateBufferedImage()));
			readyButton.setPressedIcon(new ImageIcon(((SingleImage) ImageProvider.getInstance().getSettlerSequence(2, 18).getImage(1)).generateBufferedImage()));
		}
	}

	private void localize() {
	}

	private void addListener() {
		typeComboBox.addActionListener(e -> updateAiPlayerName());
		civilisationComboBox.addActionListener(e -> updateAiPlayerName());
		slotComboBox.addActionListener(e -> {
			if (slotListener != null) {
				slotListener.slotHasChanged(oldSlotValue, getSlot());
			}
			oldSlotValue = getSlot();
		});
		readyButton.addActionListener(e -> {
			setReady(!isReady());
			if (gameToBeInformedAboutReady != null) {
				gameToBeInformedAboutReady.setReady(isReady());
			}
		});
	}

	private void updateAiPlayerName() {
		if (typeComboBox.getSelectedItem() == null || civilisationComboBox.getSelectedItem() == null) {
			return;
		}

		if (!EPlayerType.HUMAN.equals(((PlayerTypeUiWrapper) typeComboBox.getSelectedItem()).getPlayerType())) {
			setPlayerName(Labels.getString(
					"player-name-" + getCivilisation().name() + "-" +
							((PlayerTypeUiWrapper) typeComboBox.getSelectedItem()).getPlayerType().name()));
		}
	}

	private void initializeComboBoxes() {
		civilisationComboBox.addItem(new CivilisationUiWrapper());
		civilisationComboBox.addItem(new CivilisationUiWrapper(ECivilisation.ROMAN));
	}

	public void setPlayerName(String playerName) {
		playerNameLabel.setText(playerName);
	}

	public void setPossibleTypes(EPlayerType[] playerTypes) {
		typeComboBox.removeAll();
		Arrays.asList(playerTypes)
				.stream()
				.map(PlayerTypeUiWrapper::new)
				.forEach(typeComboBox::addItem);
	}

	public void setSlotAndTeams(Byte slotAndTeamCount) {
		slotComboBox.removeAllItems();
		teamComboBox.removeAllItems();
		for (byte i = 1; i < slotAndTeamCount + 1; i++) {
			slotComboBox.addItem(i);
			teamComboBox.addItem(i);
		}
	}

	public void setSlot(byte slot) {
		slotComboBox.setSelectedIndex(slot);
		oldSlotValue = slot;
	}

	public void setTeam(byte team) {
		teamComboBox.setSelectedIndex(team);
	}

	public void setAvailable(boolean available) {
		isAvailable = available;
	}

	public byte getSlot() {
		return (byte) slotComboBox.getSelectedIndex();
	}

	public byte getTeam() {
		return (byte) teamComboBox.getSelectedIndex();
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public EPlayerType getPlayerType() {
		return ((PlayerTypeUiWrapper) typeComboBox.getSelectedItem()).getPlayerType();
	}

	public ECivilisation getCivilisation() {
		return ((CivilisationUiWrapper) civilisationComboBox.getSelectedItem()).getCivilisation();
	}

	public void setSlotListener(SlotListener slotListener) {
		this.slotListener = slotListener;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean ready) {
		isReady = ready;
		updateReadyButtonStyle();
	}

	public void disableAllInputs() {
		slotComboBox.setEnabled(false);
		civilisationComboBox.setEnabled(false);
		teamComboBox.setEnabled(false);
		typeComboBox.setEnabled(false);
	}

	public void setReadyButtonEnabled(boolean isEnabled) {
		readyButton.setEnabled(isEnabled);
	}

	public void setCivilisation(ECivilisation civilisation) {
		for (int i = 0; i < civilisationComboBox.getItemCount(); i++) {
			if (civilisationComboBox.getItemAt(i).getCivilisation() == civilisation) {
				civilisationComboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	public void setTypeComboBox(EPlayerType playerType) {
		for (int i = 0; i < typeComboBox.getItemCount(); i++) {
			if (typeComboBox.getItemAt(i).getPlayerType() == playerType) {
				typeComboBox.setSelectedIndex(i);
				break;
			}
		}
		updateAiPlayerName();
	}

	public void informGameAboutReady(IJoinPhaseMultiplayerGameConnector gameToBeInformedAboutReady) {
		this.gameToBeInformedAboutReady = gameToBeInformedAboutReady;
	}
}
