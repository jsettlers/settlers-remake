package jsettlers.graphics.startscreen.joining;

import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IChangingListListener;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.utils.UIList;
import jsettlers.graphics.utils.UIList.ListItemGenerator;
import jsettlers.graphics.utils.UIListItem;
import jsettlers.graphics.utils.UIPanel;

public class JoinPhaseScreen extends UIPanel implements IMultiplayerListener,
        IChangingListListener<IMultiplayerPlayer> {

	private final IContentSetable contentSetable;
	private final IJoinPhaseMultiplayerGameConnector connector;
	private UIList<IMultiplayerPlayer> multiplayerList;

	public JoinPhaseScreen(IJoinPhaseMultiplayerGameConnector connector,
	        IContentSetable contentSetable) {
		this.connector = connector;
		this.contentSetable = contentSetable;

		connector.setMultiplayerListener(this);

		addStartButton();
		addPlayerList();
	}

	private void addPlayerList() {
		multiplayerList =
		        new UIList<IMultiplayerPlayer>(connector.getPlayers()
		                .getItems(),
		                new ListItemGenerator<IMultiplayerPlayer>() {
			                @Override
			                public UIListItem getItem(IMultiplayerPlayer item) {
				                return new GenericListItem(item.getName(), item
				                        .toString());
			                }
		                }, .1f);
	}

	private void addStartButton() {
		UILabeledButton startButton =
		        new UILabeledButton("TODO: Startlabel", new ExecutableAction() {
			        @Override
			        public void execute() {
				        connector.startGame();
			        }
		        });
		this.addChild(startButton, .3f, 0, 1, .1f);
	}

	@Override
	public void gameStarted(IStartingGame game) {
		contentSetable.setContent(new StartingGamePanel(game, contentSetable));
	}

	@Override
	public void gameAborted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAttach() {
		connector.getPlayers().setListener(this);
	}

	@Override
	public void onDetach() {
		connector.getPlayers().setListener(null);
	}

	@Override
	public void listChanged(IChangingList<IMultiplayerPlayer> list) {
		multiplayerList.setItems(list.getItems());
	}

}
