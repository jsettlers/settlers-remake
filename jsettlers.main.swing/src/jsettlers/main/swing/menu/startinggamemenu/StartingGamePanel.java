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
package jsettlers.main.swing.menu.startinggamemenu;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.components.BackgroundPanel;
import jsettlers.main.swing.JSettlersFrame;

/**
 * @author codingberlin
 */
public class StartingGamePanel extends BackgroundPanel implements IStartingGameListener {
	private static final long serialVersionUID = -2242937805688362838L;

	private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
	private final JSettlersFrame settlersFrame;

	public StartingGamePanel(JSettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
		createStructure();
		setStyle();
		localize();
	}

	private void createStructure() {
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());
		JPanel southPanel = new JPanel();
		outerPanel.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(messageLabel);
		add(outerPanel);
	}

	private void setStyle() {
		messageLabel.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void localize() {
		messageLabel.setText(Labels.getProgress(EProgressState.LOADING));
	}

	public void setStartingGame(IStartingGame startingGame) {
		startingGame.setListener(this);
	}

	@Override
	public void startProgressChanged(EProgressState state, float progress) {
		SwingUtilities.invokeLater(() -> messageLabel.setText(Labels.getProgress(state)));
	}

	@Override
	public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
		return settlersFrame.showStartedGame(game);
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {

	}

	@Override
	public void startFinished() {

	}

}
