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
package jsettlers.graphics.startscreen.progress;

import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;

public class StartingGamePanel extends ProgressPanel implements IStartingGameListener {

	private final IStartingGame game;
	private final IContentSetable contentSetable;

	public StartingGamePanel(IStartingGame game, IContentSetable contentSetable) {
		this.game = game;
		this.contentSetable = contentSetable;
		game.setListener(this);
	}

	@Override
	public void startProgressChanged(EProgressState state, float progress) {
		setProgressState(state, progress);
	}

	@Override
	public MapInterfaceConnector preLoadFinished(IStartedGame game) {
		MapContent content = new MapContent(game, contentSetable.getSoundPlayer());
		contentSetable.setContent(content);
		game.setGameExitListener(new IGameExitListener() {
			@Override
			public void gameExited(IStartedGame game) {
				contentSetable.goToStartScreen("");
			}
		});
		return content.getInterfaceConnector();
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startFinished() {
		// TODO Auto-generated method stub
	}

}
