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
import jsettlers.main.components.openpanel.OpenPanel;
import jsettlers.main.swing.SettlersFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * @author codingberlin
 */
public class MainMenuPanel extends JPanel {

	public static final Dimension PREFERRED_EAST_SIZE = new Dimension(300, 300);
	private final SettlersFrame settlersFrame;
	private final JPanel emptyPanel = new JPanel();
	private final JButton exitButton = new JButton();
	private final JToggleButton newSinglePlayerGameButton = new JToggleButton();
	private final JToggleButton loadSaveGameButton = new JToggleButton();
	private final OpenPanel openSinglePlayerPanel;
	private final OpenPanel openSaveGamePanel;
	private JToggleButton selectedToggleButton;

	public MainMenuPanel(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		openSinglePlayerPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), new ShowNewSinglePlayerGame(settlersFrame));
		openSaveGamePanel = new OpenPanel(
				transformRemakeMapLoadersToMapLoaders(MapList.getDefaultList().getSavedMaps().getItems()), new ShowNewSinglePlayerGame(settlersFrame));
		createStructure();
		localize();
		addListener();
	}

	private List<MapLoader> transformRemakeMapLoadersToMapLoaders(List<RemakeMapLoader> remakeMapLoaders) {
		List<MapLoader> mapLoaders = new Vector<MapLoader>();
		mapLoaders.addAll(remakeMapLoaders);
		return mapLoaders;
	}

	private void addListener() {
		newSinglePlayerGameButton.addActionListener(setEast(newSinglePlayerGameButton, openSinglePlayerPanel));
		loadSaveGameButton.addActionListener(setEast(loadSaveGameButton, openSaveGamePanel));
		exitButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				settlersFrame.exit();
			}
		});
	}

	private void localize() {
		exitButton.setText(Labels.getString("main-panel-exit-button"));
		newSinglePlayerGameButton.setText(Labels.getString("main-panel-new-single-player-game-button"));
		loadSaveGameButton.setText(Labels.getString("start-loadgame"));
	}

	private void createStructure() {
		setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.add(newSinglePlayerGameButton);
		centerPanel.add(loadSaveGameButton);
		centerPanel.add(exitButton);
		add(centerPanel, BorderLayout.CENTER);
		add(emptyPanel, BorderLayout.EAST);
		emptyPanel.setPreferredSize(PREFERRED_EAST_SIZE);
		openSinglePlayerPanel.setPreferredSize(PREFERRED_EAST_SIZE);
		openSaveGamePanel.setPreferredSize(PREFERRED_EAST_SIZE);
	}

	private ActionListener setEast(final JToggleButton clickedButton, final OpenPanel panelToBeSet) {
		return new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (selectedToggleButton != null) {
					selectedToggleButton.getModel().setSelected(false);
				}
				selectedToggleButton = clickedButton;
				remove(1);
				add(panelToBeSet, BorderLayout.EAST);
				settlersFrame.revalidate();
				settlersFrame.repaint();
			}
		};
	}
}
