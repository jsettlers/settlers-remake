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
