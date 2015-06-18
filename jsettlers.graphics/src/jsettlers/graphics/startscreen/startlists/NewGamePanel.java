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
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.ui.UIListItem;

public class NewGamePanel extends StartListPanel<IMapDefinition> {
	private final IStartScreen screen;
	private final IContentSetable contentSetable;

	public NewGamePanel(IStartScreen screen, IContentSetable contentSetable) {
		super(screen.getSingleplayerMaps());
		this.screen = screen;
		this.contentSetable = contentSetable;
	}

	@Override
	protected void onSubmitAction() {
		IStartingGame game = screen.startSingleplayerGame(getActiveListItem());
		contentSetable.setContent(new StartingGamePanel(game, contentSetable));
	}

	@Override
	public UIListItem getItem(IMapDefinition item) {
		return new StartableMapListItem(item);
	}

	@Override
	protected String getSubmitTextId() {
		return "start-newgame-start";
	}
}
