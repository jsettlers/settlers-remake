package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.progress.JoiningGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class JoinableGamePanel extends StartListPanel<IJoinableGame> {

	private final IMultiplayerConnector connector;
	private final IStartScreen screen;
	private final IContentSetable contentSetable;

	public JoinableGamePanel(IStartScreen screen,
	        IContentSetable contentSetable, IMultiplayerConnector connector) {
		super(connector.getJoinableMultiplayerGames());
		this.screen = screen;
		this.contentSetable = contentSetable;
		this.connector = connector;
	}

	@Override
	public UIListItem getItem(IJoinableGame item) {
		return new JoinableGameItem(item);
	}

	@Override
	protected Action getSubmitAction() {
		return new ExecutableAction() {
			@Override
			public void execute() {
				IJoiningGame joiningGame =
				        connector.joinMultiplayerGame(getActiveListItem());
				contentSetable.setContent(new JoiningGamePanel(joiningGame,
				        contentSetable));
			}
		};
	}
}
