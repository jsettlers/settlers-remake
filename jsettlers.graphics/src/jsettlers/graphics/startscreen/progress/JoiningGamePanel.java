package jsettlers.graphics.startscreen.progress;

import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
import jsettlers.graphics.startscreen.joining.JoinPhaseScreen;

public class JoiningGamePanel extends ProgressPanel implements IJoiningGameListener {

	private final IContentSetable contentSetable;

	public JoiningGamePanel(IJoiningGame joiningGame,
			IContentSetable contentSetable) {
		this.contentSetable = contentSetable;
		joiningGame.setListener(this);
	}

	@Override
	public void joinProgressChanged(EProgressState state, float progress) {
		setProgressState(state, progress);
	}

	@Override
	public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
		contentSetable.setContent(new JoinPhaseScreen(connector, contentSetable));
	}

}
