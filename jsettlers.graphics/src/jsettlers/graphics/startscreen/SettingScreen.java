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
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIInput;
import jsettlers.graphics.ui.UIPanel;

public class SettingScreen extends UIPanel {

	private static final ImageLink SETTINGS_BACKGROUND = new OriginalImageLink(
			EImageLinkType.GUI, 2, 19, 0);
	private final IContentSetable connector;
	private final UIInput server;
	private final UIInput name;
	private final VolumeInput volume;

	private static final class VolumeInput extends UIPanel {
		private float volume = 0;
		private static final float BUTTON_WIDTH = .1f;
		private static final ImageLink BUTTON_IMAGE = null;

		// private static final ImageLink BUTTON_IMAGE = new DirectImageLink(
		// "slider.0");

		@Override
		public void drawAt(GLDrawContext gl) {
			FloatRectangle p = getPosition();
			float x = p.getMinX() + volume * (1 - BUTTON_WIDTH) * p.getWidth();
			if (BUTTON_IMAGE != null) {
				float x2 = x + BUTTON_WIDTH * p.getWidth();
				// ImageProvider.getInstance().getImage(BUTTON_IMAGE)
				// .drawImageAtRect(gl, x, p.getMinY(), x2, p.getMaxY());
				ImageProvider.getInstance().getImage(BUTTON_IMAGE).drawAt(gl, x, p.getMaxY());
			}
			super.drawAt(gl);
		}

		public void setVolume(float volume) {
			this.volume = Math.min(Math.max(volume, 0), 1);
		}

		public float getVolume() {
			return volume;
		}

		@Override
		public Action getAction(float relativex, float relativey) {
			final float newVolume =
					(relativex - BUTTON_WIDTH / 2) / (1 - BUTTON_WIDTH);
			return new ExecutableAction() {
				@Override
				public void execute() {
					setVolume(newVolume);
				}
			};
		}
	}

	public SettingScreen(IContentSetable connector) {
		this.connector = connector;
		setBackground(SETTINGS_BACKGROUND);

		name = new UIInput();
		addChild(name, 349 / 800.0f, (600 - 224) / 600.0f, 580 / 800.0f,
				(600 - 200) / 600.0f);
		server = new UIInput();
		// addChild(server, 349 / 800.0f, (600 - 257) / 600.0f, 580 / 800.0f,
		// (600 - 233) / 600.0f);
		volume = new VolumeInput();
		addChild(volume, 349 / 800.0f, (600 - 257) / 600.0f, 580 / 800.0f,
				(600 - 233) / 600.0f);
		addButtons();
		loadSettings();
	}

	private void loadSettings() {
		SettingsManager sm = SettingsManager.getInstance();
		server.setInputString(sm.get(SettingsManager.SETTING_SERVER));
		name.setInputString(sm.getPlayer().getName());
		volume.setVolume(sm.getVolume());
	}

	private void addButtons() {
		addChild(new LabeledButton(Labels.getString("settings-back"),
				new ExecutableAction() {
					@Override
					public void execute() {
						connector.goToStartScreen("");
					}
				}), 0.38f, (600 - 291) / 600.0f, 0.48f, (600 - 267) / 600.0f);
		addChild(new LabeledButton(Labels.getString("settings-ok"),
				new ExecutableAction() {
					@Override
					public void execute() {
						saveSettings();
						connector.goToStartScreen("");
					}
				}), 0.52f, (600 - 291) / 600.0f, 0.62f, (600 - 267) / 600.0f);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		TextDrawer textDrawer = gl.getTextDrawer(EFontSize.NORMAL);
		float width = getPosition().getWidth();
		float height = getPosition().getHeight();
		String title = Labels.getString("settings-title");
		textDrawer.drawString(
				400 / 800.0f * width - (float) textDrawer.getWidth(title) / 2,
				(600 - 175) / 600.0f * height, title);
		String name = Labels.getString("settings-name");
		textDrawer.drawString((219 + 340) / 2.0f / 800.0f * width
				- (float) textDrawer.getWidth(name) / 2, (600 - 211) / 600.0f
				* height, name);
		String volume = Labels.getString("settings-volume");
		textDrawer.drawString((219 + 340) / 2.0f / 800.0f * width
				- (float) textDrawer.getWidth(volume) / 2, (600 - 245) / 600.0f
				* height, volume);
	}

	private void saveSettings() {
		// System.out.println("Name: " + name.getInputString() + ", Server: "
		// + server.getInputString());
		SettingsManager sm = SettingsManager.getInstance();
		sm.set(SettingsManager.SETTING_USERNAME, name.getInputString());
		sm.set(SettingsManager.SETTING_SERVER, server.getInputString());
		sm.set(SettingsManager.SETTING_VOLUME, volume.getVolume() + "");
	}
}
