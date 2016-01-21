package jsettlers.main.components.mainmenu;

import jsettlers.main.components.openpanel.OpenPanel;
import jsettlers.main.swing.SettlersFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author codingberlin
 */
public class JoinMultiplayerGame implements ActionListener {
	private final SettlersFrame settlersFrame;
	private OpenPanel relatedOpenPanel;

	public JoinMultiplayerGame(SettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
	}

	public void setRelatedOpenPanel(OpenPanel relatedOpenPanel) {
		this.relatedOpenPanel = relatedOpenPanel;
	}

	@Override public void actionPerformed(ActionEvent e) {
		NetworkGameMapLoader networkGameMapLoader = (NetworkGameMapLoader) relatedOpenPanel.getSelectedMap();
	}
}
