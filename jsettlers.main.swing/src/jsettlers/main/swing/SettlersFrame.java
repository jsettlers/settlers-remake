/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.main.swing;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.sound.SoundPlayer;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.logic.map.MapLoader;
import jsettlers.main.components.joingame.JoinGamePanel;
import jsettlers.main.components.mainmenu.MainMenuPanel;
import jsettlers.main.components.startinggamemenu.StartingGamePanel;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.Timer;

/**
 * @author codingberlin
 */
public class SettlersFrame extends JFrame {

	private final MainMenuPanel mainPanel = new MainMenuPanel(this);
	private final StartingGamePanel startingGamePanel = new StartingGamePanel(this);
	private final JoinGamePanel joinGamePanel = new JoinGamePanel(this);
	private SoundPlayer soundPlayer = new SwingSoundPlayer();

	public SettlersFrame() throws HeadlessException {
		showMainMenu();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1200, 800));
		pack();
		setVisible(true);
	}

	public void showMainMenu() {
		setNewContentPane(mainPanel);
	}

	public void showStartingGamePanel(IStartingGame startingGame) {
		startingGamePanel.setStartingGame(startingGame);
		setNewContentPane(startingGamePanel);
	}

	private void setNewContentPane(Container newContent) {
		setContentPane(newContent);
		revalidate();
		repaint();
	}

	public void exit() {
		System.exit(0);
	}

	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

	public void setContent(MapContent content) {
		Region region = new Region(500, 500);
		region.setContent(content);
		Area area = new Area();
		area.add(region);

		new Timer("opengl-redraw").schedule(new TimerTask() {
			@Override
			public void run() {
				region.requestRedraw();
			}
		}, 100, 25);

		SwingUtilities.invokeLater(() -> {
			setContentPane(new AreaContainer(area));
			revalidate();
			repaint();
		});
	}

	public void showJoinGameMenu(MapLoader mapLoader) {
		joinGamePanel.setSinglePlayerMap(mapLoader);
		setNewContentPane(joinGamePanel);
	}
}
