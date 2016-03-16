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
package jsettlers.main.swing.menu.mainmenu;

import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.main.MultiplayerConnector;
import jsettlers.main.swing.SettlersFrame;
import jsettlers.main.swing.menu.openpanel.OpenPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author codingberlin
 */
public class NewMultiPlayerGame implements ActionListener {

	private final SettlersFrame settlersFrame;
	private OpenPanel relatedOpenPanel;

	public NewMultiPlayerGame(SettlersFrame settlersFrame){
		this.settlersFrame = settlersFrame;
	}

	public void setRelatedOpenPanel(OpenPanel relatedOpenPanel) {
		this.relatedOpenPanel = relatedOpenPanel;
	}

	@Override public void actionPerformed(ActionEvent e) {
		SettingsManager settingsManager = SettingsManager.getInstance();
		Player player = settingsManager.getPlayer();
		IMultiplayerConnector connector = new MultiplayerConnector(settingsManager.get(SettingsManager.SETTING_SERVER), player.getId(), player.getName());
		settlersFrame.showNewMultiPlayerGameMenu(relatedOpenPanel.getSelectedMap(), connector);
	}
}
