package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;
import jsettlers.graphics.startscreen.progress.JoiningGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class NewMultiplayerGamePanel extends
        StartListPanel<IStartableMapDefinition> {
	private final class OpenMultiplayerGameInfo implements
	        IOpenMultiplayerGameInfo {
		private final IStartableMapDefinition map;

		public OpenMultiplayerGameInfo(IStartableMapDefinition map) {
			this.map = map;
		}

		@Override
		public int getMaxPlayers() {
			// We might limit this more...
			return map.getMaxPlayers();
		}

		@Override
		public String getMatchName() {
			return "TODO Matchname ("
			        + SettingsManager.getInstance().get(
			                SettingsManager.SETTING_USERNAME) + ")";
		}

		@Override
		public IStartableMapDefinition getMapDefinition() {
			return map;
		}
	}

	private final IStartScreen screen;
	private final IContentSetable contentSetable;

	public NewMultiplayerGamePanel(IStartScreen screen,
	        IContentSetable contentSetable) {
		super(screen.getMultiplayerMaps());
		this.screen = screen;
		this.contentSetable = contentSetable;
	}

	@Override
	protected Action getSubmitAction() {
		return new ExecutableAction() {
			@Override
			public void execute() {
				IOpenMultiplayerGameInfo gameInfo =
				        new OpenMultiplayerGameInfo(getActiveListItem());
				IJoiningGame joiningGame;

				SettingsManager sm = SettingsManager.getInstance();
				joiningGame =
				        screen.getMultiplayerConnector(
				                sm.get(SettingsManager.SETTING_SERVER),
				                sm.getPlayer())
				                .openNewMultiplayerGame(gameInfo);
				contentSetable.setContent(new JoiningGamePanel(joiningGame,
				        contentSetable));
			}
		};
	}

	@Override
	public UIListItem getItem(IStartableMapDefinition item) {
		return new StartableMapListItem(item);
	}

	@Override
	protected String getSubmitTextId() {
		return "start-newmultiplayer-start";
	}

}
