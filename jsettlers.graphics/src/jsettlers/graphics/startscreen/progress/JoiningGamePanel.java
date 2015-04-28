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

import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
import jsettlers.graphics.startscreen.joining.JoinPhaseScreen;

public class JoiningGamePanel extends ProgressPanel implements IJoiningGameListener {

	private final IContentSetable contentSetable;

	public JoiningGamePanel(IJoiningGame joiningGame,
			IContentSetable contentSetable) {
		this.contentSetable = contentSetable;
		joiningGame.setListener(this);
	}

	@Override
	public void joinProgressChanged(EProgressState state, float progress) {
		setProgressState(state, progress);
	}

	@Override
	public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
		contentSetable.setContent(new JoinPhaseScreen(connector, contentSetable));
	}

}
