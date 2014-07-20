package jsettlers.graphics.startscreen.joining;

import jsettlers.common.images.DirectImageLink;
import jsettlers.common.images.ImageLink;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.IContentSetable;
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
	public static final ImageLink BACKGROUND = new DirectImageLink(
			"joinphase.0");

	private final IContentSetable contentSetable;
	private final IJoinPhaseMultiplayerGameConnector connector;
	private UIList<IMultiplayerPlayer> multiplayerList;

	private boolean ready = false;

	public JoinPhaseScreen(IJoinPhaseMultiplayerGameConnector connector,
			IContentSetable contentSetable) {
		this.connector = connector;
		this.contentSetable = contentSetable;
		setBackground(BACKGROUND);

		connector.setMultiplayerListener(this);

		addStartButton();
		addReadyButton();
		addPlayerList();
		addChatList();

		connector.setReady(ready);
	}

	private void addChatList() {
		ChatList chatList = new ChatList();
		connector.setChatListener(chatList);
		this.addChild(chatList, .5725f, 1 - .733f, .96f, 1 - .166f);
	}

	private void addPlayerList() {
		// TODO: Ping 2 settlers 14 0..7 => good .. bad
		// TODO: Ready / Not ready: 2 settlers 16 0 and 1
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
		this.addChild(multiplayerList, .0375f, 1 - .895f, .54125f, 1 - .166f);
	}

	private void addStartButton() {
		UILabeledButton startButton =
				new UILabeledButton(Labels.getString("start-joining-start"),
						new ExecutableAction() {
							@Override
							public void execute() {
								connector.startGame();
							}
						});
		this.addChild(startButton, .78f, 1 - .895f, .96f, 1 - .816f);
	}

	private void addReadyButton() {
		UILabeledButton startButton =
				new UILabeledButton(Labels.getString("start-joining-ready"),
						new ExecutableAction() {
							@Override
							public void execute() {
								ready = !ready;
								connector.setReady(ready);
							}
						}) {
					@Override
					protected ImageLink getBackgroundImage() {
						return !ready ? new DirectImageLink("ready.0") : new DirectImageLink("ready.1");
					}
				};
		this.addChild(startButton, .5725f, 1 - .895f, .77f, 1 - .816f);
	}

	@Override
	public void gameAborted() {
		// TODO Error message, back to sart screen.
		throw new UnsupportedOperationException();
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
	public void listChanged(ChangingList<IMultiplayerPlayer> list) {
		multiplayerList.setItems(list.getItems());
	}

	@Override
	public void gameIsStarting(IStartingGame game) {
		contentSetable.setContent(new StartingGamePanel(game, contentSetable));
	}
}
