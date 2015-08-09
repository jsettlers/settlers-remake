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
package jsettlers.graphics.startscreen.startlists;

import java.util.Comparator;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.progress.JoiningGamePanel;
import jsettlers.graphics.ui.UIListItem;

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
	protected void onSubmitAction() {
		IJoiningGame joiningGame = connector.joinMultiplayerGame(getActiveListItem());
		gameStarted = true;
		contentSetable.setContent(new JoiningGamePanel(joiningGame, contentSetable));
	}

	@Override
	public void onAttach() {
		SettingsManager sm = SettingsManager.getInstance();
		connector = screen.getMultiplayerConnector(sm.get(SettingsManager.SETTING_SERVER), sm.getPlayer());
		super.onAttach();
	}

	@Override
	protected ChangingList<IJoinableGame> getList() {
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

	@Override
	protected Comparator<? super IJoinableGame> getDefaultComparator() {
		return IJoinableGame.MATCH_NAME_COMPARATOR;
	}
}
