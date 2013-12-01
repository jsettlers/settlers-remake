package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.progress.JoiningGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class JoinableGamePanel extends StartListPanel<IJoinableGame> {

	private final IStartScreen screen;
	private final IContentSetable contentSetable;
	private IMultiplayerConnector connector;
	private boolean gameStarted;

	public JoinableGamePanel(IStartScreen screen, IContentSetable contentSetable) {
		super(null);
		this.screen = screen;
		this.contentSetable = contentSetable;
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
				gameStarted = true;
				contentSetable.setContent(new JoiningGamePanel(joiningGame,
				        contentSetable));
			}
		};
	}

	@Override
	public void onAttach() {
		SettingsManager sm = SettingsManager.getInstance();
		connector =
		        screen.getMultiplayerConnector(
		                sm.get(SettingsManager.SETTING_SERVER), sm.getPlayer());
		super.onAttach();
	}

	@Override
	protected IChangingList<IJoinableGame> getList() {
		return connector.getJoinableMultiplayerGames();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (!gameStarted) {
			connector.shutdown();
		}
		connector = null;
	}

	@Override
	protected String getSubmitTextId() {
		return "start-joinmultiplayer-start";
	}
}
