package jsettlers.main.replay;

import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;

class DummyStartingGameListener implements IStartingGameListener {
	private final Object waitMutex = new Object();
	private IStartedGame startedGame = null;

	@Override
	public void startProgressChanged(EProgressState state, float progress) {
	}

	@Override
	public IMapInterfaceConnector startFinished(IStartedGame game) {
		startedGame = game;
		synchronized (waitMutex) {
			waitMutex.notifyAll();
		}
		return new DummyMapInterfaceConnector();
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {
		System.err.println("start failed due to: " + errorType);
		exception.printStackTrace();
		System.exit(1);
	}

	public IStartedGame waitForGameStartup() {
		while (startedGame == null) {
			synchronized (waitMutex) {
				try {
					waitMutex.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return startedGame;
	}
}
