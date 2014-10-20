package jsettlers.main.android.fragments.progress;

import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
import jsettlers.main.android.fragments.JoinPhaseFragment;
import android.annotation.SuppressLint;

@SuppressLint("ValidFragment")
public class JoinGameProgress extends ProgressFragment implements IJoiningGameListener {

	private final IJoiningGame joining;

	public JoinGameProgress(IJoiningGame joining) {
		this.joining = joining;
		joining.setListener(this);
	}

	@Override
	public void joinProgressChanged(EProgressState state, float progress) {
		setProgressState(state, progress)
		;
	}

	@Override
	public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
		getJsettlersActivity().showFragment(new JoinPhaseFragment(connector));
	}

}
