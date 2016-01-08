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

	public MainMenuPanel(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		openSinglePlayerPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), new ShowNewSinglePlayerGame(settlersFrame));
		openSaveGamePanel = new OpenPanel(
				transformRemakeMapLoadersToMapLoaders(MapList.getDefaultList().getSavedMaps().getItems()),
				new ShowNewSinglePlayerGame(settlersFrame));
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
		newSinglePlayerGameButton.addActionListener(e -> setCenter(openSinglePlayerPanel));
		loadSaveGameButton.addActionListener(e -> setCenter(openSaveGamePanel));
		exitButton.addActionListener(e -> settlersFrame.exit());
	}

	private void localize() {
		exitButton.setText(Labels.getString("main-panel-exit-button"));
		newSinglePlayerGameButton.setText(Labels.getString("main-panel-new-single-player-game-button"));
		loadSaveGameButton.setText(Labels.getString("start-loadgame"));
	}

	private void createStructure() {
		setLayout(new BorderLayout());
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));
		westPanel.add(newSinglePlayerGameButton);
		westPanel.add(loadSaveGameButton);
		westPanel.add(exitButton);
		add(westPanel, BorderLayout.WEST);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(newSinglePlayerGameButton);
		buttonGroup.add(loadSaveGameButton);
		add(emptyPanel, BorderLayout.CENTER);
		westPanel.setPreferredSize(PREFERRED_EAST_SIZE);
	}

	private void setCenter(final OpenPanel panelToBeSet) {
		remove(1);
		add(panelToBeSet, BorderLayout.CENTER);
		settlersFrame.revalidate();
		settlersFrame.repaint();
	}
}
