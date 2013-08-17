package jsettlers.graphics.startscreen;

import go.graphics.text.EFontSize;

import java.util.LinkedList;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.startlists.JoinableGamePanel;
import jsettlers.graphics.startscreen.startlists.LoadGamePanel;
import jsettlers.graphics.startscreen.startlists.NewGamePanel;
import jsettlers.graphics.startscreen.startlists.NewMultiplayerGamePanel;
import jsettlers.graphics.utils.UIPanel;

public class StartScreen extends UIPanel {
	public static final OriginalImageLink BACKGROUND = new OriginalImageLink(
	        EImageLinkType.GUI, 2, 29, 0);

	private final LinkedList<UILabeledButton> mainButtons =
	        new LinkedList<UILabeledButton>();
	private final UIPanel content;

	private final IStartScreen connector;

	private final IContentSetable contentSetable;

	public StartScreen(IStartScreen connector, IContentSetable contentSetable) {
		this.connector = connector;
		// root.setBackground(new ImageLink(EImageLinkType.GUI, 2, 29, 0));
		this.contentSetable = contentSetable;

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
	}

	void setContent(UIPanel panel) {
		content.removeAll();
		content.addChild(panel, 0, 0, 1, 1);
	}

	private class MainButton {
		private final UILabeledButton button;

		private MainButton(String labelId, final UIPanel panel, float top) {
			ExecutableAction action = new ExecutableAction() {
				@Override
				public void execute() {
					for (UILabeledButton b : mainButtons) {
						b.setActive(false);
					}
					setContent(panel);
					button.setActive(true);
				}
			};
			button =
			        new UILabeledButton(Labels.getString(labelId), action,
			                EFontSize.HEADLINE);
		}

		private UILabeledButton getButton() {
			return button;
		}
	}

	private void addMainButton(String labelId, final UIPanel panel, float top) {
		final UILabeledButton child =
		        new MainButton(labelId, panel, top).getButton();
		addChild(child, .05f, top - .1f, .45f, top);
		mainButtons.add(child);
	}

}
