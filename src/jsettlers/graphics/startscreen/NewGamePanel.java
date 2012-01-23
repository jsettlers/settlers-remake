package jsettlers.graphics.startscreen;


import java.util.ArrayList;
import java.util.List;

import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.graphics.utils.UIPanel;

public class NewGamePanel extends UIPanel {

	private UIList<MapListItem> list;

	public NewGamePanel(List<? extends IMapItem> maps, boolean startNetwork) {
		ArrayList<MapListItem> items =
		        new ArrayList<NewGamePanel.MapListItem>();
		for (IMapItem map : maps) {
			items.add(new MapListItem(map));
		}
		list = new UIList<MapListItem>(items, .1f);

		this.addChild(list, 0, .15f, 1, 1);

		// start button
		EActionType action = startNetwork ? EActionType.START_NETWORK : EActionType.START_NEW_GAME;
		UILabeledButton startbutton =
		        new UILabeledButton(Labels.getName(action),
		                new Action(action));
		this.addChild(startbutton, .3f, 0, 1, .1f);
	}

	private class MapListItem extends GenericListItem {
		private final IMapItem item;

		public MapListItem(IMapItem item) {
			super(item.getName(), "");
			this.item = item;
		}

		public IMapItem getMap() {
			return item;
		}
	}

	/**
	 * Gets the game settings for the selected game.
	 * 
	 * @return
	 */
	public IGameSettings getGameSettings() {
		MapListItem item = list.getActiveItem();

		if (item != null) {
			//TODO: allow user to select count
			return new GameSettings(item.getMap(), 3);
		} else {
			return null;
		}
	}

	public IMatchSettings getNetworkGameSettings() {
		MapListItem item = list.getActiveItem();
	    if (item != null) {
	    	return new NetworkGameSettings(item.getMap(), "player's game");
	    } else {
	    	return null;
	    }
    }

}
