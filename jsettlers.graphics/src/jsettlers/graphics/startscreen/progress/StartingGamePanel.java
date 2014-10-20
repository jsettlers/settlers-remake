package jsettlers.graphics.startscreen.progress;

import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;

public class StartingGamePanel extends ProgressPanel implements IStartingGameListener {

	private final IStartingGame game;
	private final IContentSetable contentSetable;

	public StartingGamePanel(IStartingGame game, IContentSetable contentSetable) {
		this.game = game;
		this.contentSetable = contentSetable;
		game.setListener(this);
	}

	@Override
    public void startProgressChanged(EProgressState state, float progress) {
	    setProgressState(state, progress);
    }

	@Override
    public MapInterfaceConnector startFinished(IStartedGame game) {
		MapContent content = new MapContent(game, contentSetable.getSoundPlayer());
		contentSetable.setContent(content);
		game.setGameExitListener(new IGameExitListener() {
			@Override
			public void gameExited(IStartedGame game) {
				contentSetable.goToStartScreen("");
			}
		});
	    return content.getInterfaceConnector();
    }

	@Override
    public void startFailed(EGameError errorType, Exception exception) {
	    // TODO Auto-generated method stub
	    
    }

}
