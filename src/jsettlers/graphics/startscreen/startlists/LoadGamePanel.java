package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartScreenConnector;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class LoadGamePanel extends StartListPanel<ILoadableMapDefinition> {

	private final IStartScreenConnector screen;
	private final IContentSetable contentSetable;

	public LoadGamePanel(IStartScreenConnector screen, IContentSetable contentSetable) {
		super(screen.getStoredSingleplayerGames());
		this.screen = screen;
		this.contentSetable = contentSetable;
	}

	@Override
	protected Action getSubmitAction() {
		return new ExecutableAction() {
			@Override
			public void execute() {
				IStartingGame game =
				        screen.loadSingleplayerGame(getActiveListItem());
				contentSetable.setContent(new StartingGamePanel(game,
				        contentSetable));
			}
		};
	}

	@Override
	public UIListItem getItem(ILoadableMapDefinition item) {
		return new LoadableMapListItem(item);
	}

	@Override
	protected String getSubmitTextId() {
	    return "start-loadgame-start";
	}

}
