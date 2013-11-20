package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.utils.UIInput;
import jsettlers.graphics.utils.UIPanel;

public class SettingScreen extends UIPanel {

	private static final ImageLink SETTINGS_BACKGROUND = new OriginalImageLink(
	        EImageLinkType.GUI, 2, 19, 0);
	private final IContentSetable connector;
	private final UIInput server;
	private final UIInput name;

	public SettingScreen(IContentSetable connector) {
		this.connector = connector;
		setBackground(SETTINGS_BACKGROUND);

		name = new UIInput();
		addChild(name, 349 / 800.0f, (600 - 224) / 600.0f, 580 / 800.0f,
		        (600 - 200) / 600.0f);
		server = new UIInput();
		addChild(server, 349 / 800.0f, (600 - 257) / 600.0f, 580 / 800.0f,
		        (600 - 233) / 600.0f);
		addButtons();
	}

	private void addButtons() {
		addChild(new UILabeledButton("settings_back", new ExecutableAction() {
			@Override
			public void execute() {
				connector.goToStartScreen("");
			}
		}), 0.38f, (600 - 291) / 600.0f, 0.48f, (600 - 267) / 600.0f);
		addChild(new UILabeledButton("settings_apply", new ExecutableAction() {
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
		textDrawer.drawString(400 / 800.0f * width, (600 - 175) / 600.0f * height,
		        Labels.getString("settings_title"));
		textDrawer.drawString((219 + 340) / 2.0f / 800.0f * width, (600 - 211)
		        / 600.0f * height, Labels.getString("settings_name"));
		textDrawer.drawString((219 + 340) / 2.0f / 800.0f * width, (600 - 245)
		        / 600.0f * height, Labels.getString("settings_server"));
	}

	private void saveSettings() {
		System.out.println("Name: " + name.getInputString() + ", Server: "
		        + server.getInputString());
	}
}
