package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;

import java.util.ArrayList;
import java.util.LinkedList;

import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.graphics.utils.UIPanel;

public class NewGamePanel extends UIPanel {

	private final IMapItem[] maps;
	private UIList<MapListItem> list;

	public NewGamePanel(IMapItem[] maps) {
		this.maps = maps;

		ArrayList<MapListItem> items =
		        new ArrayList<NewGamePanel.MapListItem>();
		for (IMapItem map : maps) {
			items.add(new MapListItem(map));
		}
		list = new UIList<MapListItem>(items, .1f);

		this.addChild(list, 0, .15f, 1, 1);

		// start button
		UILabeledButton startbutton =
		        new UILabeledButton(Labels.getName(EActionType.START_NEW_GAME),
		                new Action(EActionType.START_NEW_GAME));
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
			return new GameSettings(item.getMap(), 3);
		} else {
			return null;
		}
	}

}
