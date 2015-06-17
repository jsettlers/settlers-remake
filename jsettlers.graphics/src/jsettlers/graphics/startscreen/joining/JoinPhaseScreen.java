/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.startscreen.joining;

import jsettlers.common.images.DirectImageLink;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIList;
import jsettlers.graphics.ui.UIListItem;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.UIList.ListItemGenerator;

public class JoinPhaseScreen extends UIPanel implements IMultiplayerListener,
		IChangingListListener<IMultiplayerPlayer> {

	private final class PlayerListItemGenerator implements ListItemGenerator<IMultiplayerPlayer> {
		@Override
		public UIListItem getItem(IMultiplayerPlayer item) {
			return new GenericListItem(item.getName(), item
					.toString());
		}
	}

	private final class StartGameAction extends ExecutableAction {
		@Override
		public void execute() {
			connector.startGame();
		}
	}

	private final class ToggleReadyAction extends ExecutableAction {
		@Override
		public void execute() {
			ready = !ready;
			connector.setReady(ready);
		}
	}

	public static final ImageLink BACKGROUND = new OriginalImageLink(
			EImageLinkType.GUI, 2, 29, 0);
	// public static final ImageLink BACKGROUND = new DirectImageLink(
	// "joinphase.0");

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
						new PlayerListItemGenerator(), .1f);
		this.addChild(multiplayerList, .0375f, 1 - .895f, .54125f, 1 - .166f);
	}

	private void addStartButton() {
		LabeledButton startButton =
				new LabeledButton(Labels.getString("start-joining-start"),
						new StartGameAction());
		this.addChild(startButton, .78f, 1 - .895f, .96f, 1 - .816f);
	}

	private void addReadyButton() {
		LabeledButton startButton =
				new LabeledButton(Labels.getString("start-joining-ready"),
						new ToggleReadyAction()) {
					@Override
					protected ImageLink getBackgroundImage() {
						if (ready) {
							return new DirectImageLink("ready.1");
						} else {
							return new DirectImageLink("ready.0");
						}
					}
				};
		this.addChild(startButton, .5725f, 1 - .895f, .77f, 1 - .816f);
	}

	@Override
	public void gameAborted() {
		// TODO Error message, back to start screen.
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
