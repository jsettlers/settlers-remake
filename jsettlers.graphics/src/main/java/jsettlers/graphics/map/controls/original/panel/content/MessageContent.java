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
package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.text.EFontSize;
import jsettlers.common.action.Action;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIPanel;

public class MessageContent extends AbstractContentProvider {

	private final UIPanel panel;

	public MessageContent(String message, String okMessage, Action okAction,
			String abortMessage, Action abortAction) {
		panel = new UIPanel();

		panel.addChild(new Label(message, EFontSize.NORMAL), .1f, .5f, .9f, .9f);

		if (abortMessage != null && abortAction != null) {
			LabeledButton okButton = new LabeledButton(abortMessage, abortAction);
			panel.addChild(okButton, .1f, .1f, .5f, .2f);
		}
		if (okMessage != null && okAction != null) {
			LabeledButton okButton = new LabeledButton(okMessage, okAction);
			panel.addChild(okButton, .5f, .1f, .9f, .2f);
		}
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return null;
	}

}
