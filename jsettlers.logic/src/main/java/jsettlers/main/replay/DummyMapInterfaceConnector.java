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
package jsettlers.main.replay;

import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.menu.UIState;
import jsettlers.common.action.IAction;
import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;

class DummyMapInterfaceConnector implements IMapInterfaceConnector {

	private UIState uiState;

	public DummyMapInterfaceConnector() {
		uiState = new UIState(new ShortPoint2D(0, 0));
	}

	@Override
	public void showMessage(IMessage message) {
	}

	@Override
	public UIState getUIState() {
		return uiState;
	}

	@Override
	public void loadUIState(UIState uiStateData) {
		this.uiState = uiStateData;
	}

	@Override
	public void addListener(IMapInterfaceListener listener) {
	}

	@Override
	public void removeListener(IMapInterfaceListener guiInterface) {
	}

	@Override
	public void scrollTo(ShortPoint2D point, boolean mark) {
	}

	@Override
	public void setSelection(ISelectionSet selection) {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void fireAction(IAction action) {
	}

	@Override
	public void playSound(int soundId, float volume) {

	}
}
