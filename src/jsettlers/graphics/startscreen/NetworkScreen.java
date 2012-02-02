package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.command.GOCommandEvent;

import java.util.ArrayList;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkPlayer;
import jsettlers.graphics.INetworkScreenAdapter.INetworkScreenListener;
import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

public class NetworkScreen extends RedrawListenerHaver implements SettlersContent, INetworkScreenListener {
	private final INetworkScreenAdapter networkScreen;
	private final UIPanel root;
	private boolean playerListValid;
	private UIPanel listContainer;
	private Button startAllowedButton;
	private boolean startAllowed = false;

	public NetworkScreen(final INetworkScreenAdapter networkScreen) {
		this.networkScreen = networkScreen;
		networkScreen.setListener(this);
		root = new UIPanel();
		root.setBackground(new ImageLink(EImageLinkType.GUI, 2, 25, 0));

		listContainer = new UIPanel();
		root.addChild(listContainer, .04f, .1f, .96f, .7f);

		startAllowedButton =
		        new Button(new FinishAction(), new ImageLink(
		                EImageLinkType.SETTLER, 2, 17, 0), new ImageLink(
		                EImageLinkType.SETTLER, 2, 17, 1),
		                Labels.getString("allow_start_game_descr"));
		root.addChild(startAllowedButton, 0.65f, .05f, .7f, .08f);
		
		UILabeledButton startButton = new UILabeledButton(Labels.getString("start_netgame"), new ExecutableAction() {
			@Override
			public void execute() {
				networkScreen.startNetworkMatch();
			}
		});
		root.addChild(startButton, 0.8f, .03f, .95f, .1f);

		UILabeledButton abortButton = new UILabeledButton(Labels.getString("abort_netgame"), new ExecutableAction() {
			@Override
			public void execute() {
				networkScreen.leaveGame();
			}
		});
		root.addChild(abortButton, 0.1f, .03f, .25f, .1f);
	}

	private class FinishAction extends ExecutableAction {
		@Override
		public void execute() {
			startAllowed = !startAllowed;
			startAllowedButton.setActive(startAllowed);
			networkScreen.setReady(startAllowed);
		}
	}

	@Override
	public void drawContent(GLDrawContext gl2, int width, int height) {
		reloadPlayerList();
		root.setPosition(new FloatRectangle(0, 0, width, height));
		root.drawAt(gl2);
	}

	private void reloadPlayerList() {
		if (!playerListValid) {
			playerListValid = true;
			listContainer.removeAll();
			ArrayList<PlayerItem> players = new ArrayList<PlayerItem>();
			if (networkScreen.getPlayers() != null) {
				for (INetworkPlayer player : networkScreen.getPlayers()) {
					if (player != null) {
						players.add(new PlayerItem(player));
					}
				}
			}
			UIList<PlayerItem> list = new UIList<PlayerItem>(players, .2f);
			listContainer.addChild(list, 0, 0, 1, 1);
		}
	}

	private class PlayerItem extends GenericListItem {
		public PlayerItem(INetworkPlayer player) {
			super(player.getPlayerName(), "...");
		}
	}

	@Override
	public void handleEvent(GOEvent event) {
		if (event instanceof GOCommandEvent) {
			event.setHandler(new GOEventHandler() {
				@Override
				public void phaseChanged(GOEvent event) {
				}

				@Override
				public void finished(GOEvent event) {
					GOCommandEvent c = (GOCommandEvent) event;
					UIPoint position = c.getCommandPosition();
					performActionAt(position.getX(), position.getY());
				}

				@Override
				public void aborted(GOEvent event) {
				}
			});
		}
	}

	/**
	 * Performs a action
	 * 
	 * @param x
	 *            in screen space.
	 * @param y
	 */
	protected void performActionAt(double x, double y) {
		float realx = (float) x / root.getPosition().getWidth();
		float realy = (float) y / root.getPosition().getHeight();
		Action action = root.getAction(realx, realy);
		if (action == null) {
			return;
		}
		if (action instanceof ExecutableAction) {
			((ExecutableAction) action).execute();
		}
		requestRedraw();
	}

	@Override
	public void playerListChanged() {
		playerListValid = false;
		requestRedraw();
	}

	@Override
	public void addChatMessage(String message) {
		// TODO Auto-generated method stub

		requestRedraw();
	}

}
