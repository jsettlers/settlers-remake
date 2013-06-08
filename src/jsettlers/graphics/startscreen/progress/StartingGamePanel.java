package jsettlers.graphics.startscreen.progress;

import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.EGameError;
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
		MapContent content = new MapContent(game, new SoundPlayer() {
			
			@Override
			public void setSoundDataRetriever(ISoundDataRetriever soundDataRetriever) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void playSound(int soundStart, float lvolume, float rvolume) {
				// TODO Auto-generated method stub
				
			}
		});
		contentSetable.setContent(content);
	    return content.getInterfaceConnector();
    }

	@Override
    public void startFailed(EGameError errorType, Exception exception) {
	    // TODO Auto-generated method stub
	    
    }

}
