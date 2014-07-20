package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class LoadGamePanel extends StartListPanel<IMapDefinition> {

	private final IStartScreen screen;
	private final IContentSetable contentSetable;

	public LoadGamePanel(IStartScreen screen, IContentSetable contentSetable) {
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
	public UIListItem getItem(IMapDefinition item) {
		return new LoadableMapListItem(item);
	}

	@Override
	protected String getSubmitTextId() {
		return "start-loadgame-start";
	}

}
