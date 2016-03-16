/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.menu.mainmenu;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.swing.JSettlersFrame;
import jsettlers.main.swing.menu.openpanel.OpenPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author codingberlin
 */
public class JoinMultiplayerGame implements ActionListener {
	private final JSettlersFrame settlersFrame;
	private OpenPanel relatedOpenPanel;

	public JoinMultiplayerGame(JSettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
	}

	public void setRelatedOpenPanel(OpenPanel relatedOpenPanel) {
		this.relatedOpenPanel = relatedOpenPanel;
	}

	@Override public void actionPerformed(ActionEvent e) {
		NetworkGameMapLoader networkGameMapLoader = (NetworkGameMapLoader) relatedOpenPanel.getSelectedMap();
		IJoiningGame joiningGame = settlersFrame
				.getMultiPlayerConnector()
				.joinMultiplayerGame(networkGameMapLoader
						.getJoinableGame());
		joiningGame.setListener(new IJoiningGameListener() {
			@Override public void joinProgressChanged(EProgressState state, float progress) {

			}

			@Override public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
				SwingUtilities.invokeLater(() ->
						settlersFrame.showJoinMultiplayerMenu(connector, MapList.getDefaultList().getMapById(networkGameMapLoader.getMapId())));
			}
		});
	}
}
