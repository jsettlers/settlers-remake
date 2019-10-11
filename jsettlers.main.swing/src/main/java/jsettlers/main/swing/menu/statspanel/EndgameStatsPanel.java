/*******************************************************************************
 * Copyright (c) 2019
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
package jsettlers.main.swing.menu.statspanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import jsettlers.common.menu.IStartedGame;
import jsettlers.common.player.IEndgameStatistic;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.swing.JSettlersFrame;
import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.components.BackgroundPanel;

public class EndgameStatsPanel extends BackgroundPanel {
	private JTable statsTable = new JTable();

	public EndgameStatsPanel(JSettlersFrame settlersFrame) {
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());

		statsTable.setEnabled(false);
		statsTable.putClientProperty(ELFStyle.KEY, ELFStyle.TABLE);
		mainPane.add(statsTable, BorderLayout.NORTH);

		JButton exit = new JButton(Labels.getString("stats-panel-exit"));
		exit.putClientProperty(ELFStyle.KEY, ELFStyle.BUTTON_STONE);
		exit.addActionListener(actionEvent -> settlersFrame.showMainMenu());
		mainPane.add(exit, BorderLayout.SOUTH);

		add(mainPane);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private static final String[] columns = new String[] {
			Labels.getString("stats-panel-name"),
			Labels.getString("stats-panel-team"),
			Labels.getString("stats-panel-manna"),
			Labels.getString("stats-panel-gold"),
			Labels.getString("stats-panel-soldiers"),
	};

	public void setGame(IStartedGame game) {
		IInGamePlayer[] players = game.getAllInGamePlayers();
		String[][] values = new String[players.length+1][];
		values[0] = columns;
		for(int i = 0; i != players.length; i++) values[i+1] = getPlayerRow(players[i]);

		statsTable.setModel(new DefaultTableModel(values, columns));
	}

	private String[] getPlayerRow(IInGamePlayer player) {
		String[] row = new String[5];
		IEndgameStatistic igs = player.getEndgameStatistic();

		row[0] = igs.getName();
		row[1] = "" + igs.getTeam();
		row[2] = "" + igs.getAmountOfProducedMana();
		row[3] = "" + igs.getAmountOfProducedGold();
		row[4] = "" + igs.getAmountOfProducedSoldiers();

		return row;
	}
}
