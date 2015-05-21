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

import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.progress.JoiningGamePanel;
import jsettlers.graphics.utils.UIListItem;

public class NewMultiplayerGamePanel extends
		StartListPanel<IMapDefinition> {
	private final class OpenMultiplayerGameInfo implements
			IOpenMultiplayerGameInfo {
		private final IMapDefinition map;

		public OpenMultiplayerGameInfo(IMapDefinition map) {
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
		public IMapDefinition getMapDefinition() {
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
	protected void onSubmitAction() {
		IOpenMultiplayerGameInfo gameInfo = new OpenMultiplayerGameInfo(getActiveListItem());
		SettingsManager sm = SettingsManager.getInstance();
		IJoiningGame joiningGame = screen.getMultiplayerConnector(sm.get(SettingsManager.SETTING_SERVER), sm.getPlayer()).openNewMultiplayerGame(gameInfo);
		contentSetable.setContent(new JoiningGamePanel(joiningGame, contentSetable));
	}

	@Override
	public UIListItem getItem(IMapDefinition item) {
		return new StartableMapListItem(item);
	}

	@Override
	protected String getSubmitTextId() {
		return "start-newmultiplayer-start";
	}

}
