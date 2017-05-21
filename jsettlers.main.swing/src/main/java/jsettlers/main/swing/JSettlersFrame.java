/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import jsettlers.common.CommitInfo;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.Player;
import jsettlers.graphics.map.ETextDrawPosition;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.MultiplayerConnector;
import jsettlers.main.swing.menu.joinpanel.JoinGamePanel;
import jsettlers.main.swing.menu.mainmenu.MainMenuPanel;
import jsettlers.main.swing.menu.startinggamemenu.StartingGamePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author codingberlin
 */
public class JSettlersFrame extends JFrame {
	private static final long serialVersionUID = 2607082717493797224L;

	private final IMultiplayerConnector multiPlayerConnector;
	private final MainMenuPanel mainPanel;
	private final StartingGamePanel startingGamePanel = new StartingGamePanel(this);
	private final JoinGamePanel joinGamePanel = new JoinGamePanel(this);
	private final SoundPlayer soundPlayer = new SwingSoundPlayer();

	private Timer redrawTimer;
	private boolean fullScreen = false;

	JSettlersFrame() throws HeadlessException {
		setTitle("JSettlers - Version: " + CommitInfo.COMMIT_HASH_SHORT);

		SettingsManager settingsManager = SettingsManager.getInstance();

		Player player = settingsManager.getPlayer();
		multiPlayerConnector = new MultiplayerConnector(settingsManager.get(SettingsManager.SETTING_SERVER), player.getId(), player.getName());
		mainPanel = new MainMenuPanel(this, multiPlayerConnector);

		showMainMenu();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1200, 800));
		pack();
		setLocationRelativeTo(null);

		fullScreen = settingsManager.getFullScreenMode();
		updateFullScreenMode();

		KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyboardFocusManager.addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					toogleFullScreenMode();
					return true; // consume this key event.
				}
			}
			return false;
		});
	}

	private void toogleFullScreenMode() {
		fullScreen = !fullScreen;
		SettingsManager.getInstance().setFullScreenMode(fullScreen);
		updateFullScreenMode();
	}

	private void updateFullScreenMode() {
		dispose();

		setResizable(!fullScreen);
		setUndecorated(fullScreen);

		pack();
		setVisible(true);

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		graphicsDevice.setFullScreenWindow(fullScreen ? this : null);
	}

	private void abortRedrawTimerIfPresent() {
		if (redrawTimer != null) {
			redrawTimer.cancel();
			redrawTimer = null;
		}
	}

	public void showMainMenu() {
		abortRedrawTimerIfPresent();
		setNewContentPane(mainPanel);
	}

	public void showStartingGamePanel(IStartingGame startingGame) {
		startingGamePanel.setStartingGame(startingGame);
		setNewContentPane(startingGamePanel);
	}

	private void setNewContentPane(Container newContent) {
		abortRedrawTimerIfPresent();
		setContentPane(newContent);
		revalidate();
		repaint();
	}

	public void exit() {
		abortRedrawTimerIfPresent();
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

		redrawTimer = new Timer("opengl-redraw");
		redrawTimer.schedule(new TimerTask() {
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

	public void showNewSinglePlayerGameMenu(MapLoader mapLoader) {
		joinGamePanel.setSinglePlayerMap(mapLoader);
		setNewContentPane(joinGamePanel);
	}

	public void showNewMultiPlayerGameMenu(MapLoader mapLoader, IMultiplayerConnector connector) {
		joinGamePanel.setNewMultiPlayerMap(mapLoader, connector);
		setNewContentPane(joinGamePanel);
	}

	public IMultiplayerConnector getMultiPlayerConnector() {
		return multiPlayerConnector;
	}

	public void showJoinMultiplayerMenu(IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector, MapLoader mapLoader) {
		joinGamePanel.setJoinMultiPlayerMap(joinPhaseMultiplayerGameConnector, mapLoader);
		setNewContentPane(joinGamePanel);
	}

	public IMapInterfaceConnector showStartedGame(IStartedGame startedGame) {
		MapContent content = new MapContent(startedGame, soundPlayer, ETextDrawPosition.TOP_RIGHT);
		SwingUtilities.invokeLater(() -> setContent(content));
		startedGame.setGameExitListener(exitGame -> SwingUtilities.invokeLater(this::showMainMenu));
		return content.getInterfaceConnector();
	}
}
