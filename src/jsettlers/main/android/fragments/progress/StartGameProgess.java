package jsettlers.main.android.fragments.progress;

import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;
import android.annotation.SuppressLint;


@SuppressLint("ValidFragment")
public class StartGameProgess extends ProgressFragment implements IStartingGameListener {

	public StartGameProgess(IStartingGame started) {
		started.setListener(this);
	}

	@Override
	public void startProgressChanged(EProgressState state, float progress) {
		setProgressState(state, progress);
	}

	@Override
	public MapInterfaceConnector startFinished(IStartedGame game) {
		return getJsettlersActivity().showGameMap(game);
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {
		// TODO Error message
		getJsettlersActivity().showStartScreen();
	}

}
