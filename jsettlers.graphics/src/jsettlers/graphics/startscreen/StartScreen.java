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
package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.util.LinkedList;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.startlists.JoinableGamePanel;
import jsettlers.graphics.startscreen.startlists.LoadGamePanel;
import jsettlers.graphics.startscreen.startlists.NewGamePanel;
import jsettlers.graphics.startscreen.startlists.NewMultiplayerGamePanel;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIPanel;

public class StartScreen extends UIPanel {
	public static final ImageLink BACKGROUND = new OriginalImageLink(
			EImageLinkType.GUI, 2, 29, 0);

	private static final OriginalImageLink SETTINGS = new OriginalImageLink(EImageLinkType.SETTLER, 2, 28, 0);
	private static final OriginalImageLink SETTINGS_ACTIVE = new OriginalImageLink(EImageLinkType.SETTLER, 2, 28, 1);

	private final LinkedList<LabeledButton> mainButtons =
			new LinkedList<LabeledButton>();
	private final UIPanel content;

	private final IStartScreen connector;

	private final IContentSetable contentSetable;

	private final String revision;

	public StartScreen(IStartScreen connector, IContentSetable contentSetable, String revision) {
		this.connector = connector;
		this.contentSetable = contentSetable;
		this.revision = revision;

		setBackground(BACKGROUND);
		addButtons();

		content = new UIPanel();
		addChild(content, .55f, .05f, .95f, .95f);
	}

	private void addButtons() {
		addMainButton("start-newgame", new NewGamePanel(connector,
				contentSetable), .9f);
		addMainButton("start-loadgame", new LoadGamePanel(connector,
				contentSetable), .75f);
		addMainButton("start-newmultiplayer", new NewMultiplayerGamePanel(
				connector, contentSetable), .6f);
		addMainButton(
				"start-joinmultiplayer",
				new JoinableGamePanel(connector, contentSetable),
				.45f);
		// addMainButton("start-restoremultiplayer", new NewGamePanel(connector,
		// contentSetable), .3f);

		addChild(new Button(new ExecutableAction() {
			@Override
			public void execute() {
				contentSetable.setContent(new SettingScreen(contentSetable));
			}
		}, SETTINGS, SETTINGS_ACTIVE, ""), 0.1f, 0.1f, 0.175f, 0.2f);
	}

	void setContent(UIPanel panel) {
		content.removeAll();
		content.addChild(panel, 0, 0, 1, 1);
	}

	private class MainButton {
		private final LabeledButton button;

		private MainButton(String labelId, final UIPanel panel, float top) {
			ExecutableAction action = new ExecutableAction() {
				@Override
				public void execute() {
					for (LabeledButton b : mainButtons) {
						b.setActive(false);
					}
					setContent(panel);
					button.setActive(true);
				}
			};
			button =
					new LabeledButton(Labels.getString(labelId), action,
							EFontSize.HEADLINE);
		}

		private LabeledButton getButton() {
			return button;
		}
	}

	private void addMainButton(String labelId, final UIPanel panel, float top) {
		final LabeledButton child =
				new MainButton(labelId, panel, top).getButton();
		addChild(child, .05f, top - .1f, .45f, top);
		mainButtons.add(child);
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		super.drawBackground(gl);

		TextDrawer drawer = gl.getTextDrawer(EFontSize.SMALL);
		float width = (float) drawer.getWidth(revision);
		drawer.drawString(getPosition().getCenterX() - width / 2,
				getPosition().getMinY() + 5, revision);
	}
}
