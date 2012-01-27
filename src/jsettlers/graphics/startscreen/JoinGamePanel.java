package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;

import java.util.ArrayList;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.INetworkConnector.INetworkListener;
import jsettlers.graphics.utils.UIPanel;

public class JoinGamePanel extends UIPanel implements INetworkListener {

	private UIList<DisplayableMatch> list;
	private boolean listIsOld = true;
	private final INetworkConnector networkConnector;

	public JoinGamePanel(INetworkConnector networkConnector) {
		this.networkConnector = networkConnector;
		networkConnector.setServerAddress("localhost");

		// start button
		EActionType action = EActionType.JOIN_NETWORK;
		UILabeledButton startbutton =
		        new UILabeledButton(Labels.getName(action), new Action(action));
		this.addChild(startbutton, .3f, 0, 1, .1f);

		networkConnector.setListener(this);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		if (listIsOld) {
			ArrayList<DisplayableMatch> displaymatches =
			        new ArrayList<DisplayableMatch>();
			for (IMatch m : networkConnector.getMatches()) {
				if (m != null)
					displaymatches.add(new DisplayableMatch(m));
			}
			list = new UIList<DisplayableMatch>(displaymatches, .1f);

			this.addChild(list, 0, .15f, 1, 1);

			listIsOld = false;
		}
		super.drawAt(gl);
	}

	private class DisplayableMatch extends GenericListItem {
		private final IMatch match;

		public DisplayableMatch(IMatch m) {
			super(m.getMatchName(), "Players: " + m.getMaxPlayers()
			        + ", matchid: " + m.getMatchID());
			this.match = m;
		}
	}

	@Override
	public void matchListChanged(INetworkConnector connector) {
		listIsOld = true;
	}

	public IMatch getSelected() {
		DisplayableMatch activeItem = list.getActiveItem();
		if (activeItem != null) {
			return activeItem.match;
		} else {
			return null;
		}
	}

}
