package jsettlers.graphics.startscreen.startlists;

import java.util.UUID;

import jsettlers.common.CommonConstants;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.Player;
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
			return "TODO Matchname";
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
				// TODO: Make player name changeable, UUID persistent.
				IOpenMultiplayerGameInfo gameInfo =
				        new OpenMultiplayerGameInfo(getActiveListItem());
				IJoiningGame joiningGame;

				joiningGame =
				        screen.getMultiplayerConnector(
				                CommonConstants.DEFAULT_SERVER_ADDRESS,
				                new Player(UUID.randomUUID().toString(),
				                        "testplayer")).openNewMultiplayerGame(
				                gameInfo);
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
