package jsettlers.main.swing.menu.mainmenu;

import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
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
