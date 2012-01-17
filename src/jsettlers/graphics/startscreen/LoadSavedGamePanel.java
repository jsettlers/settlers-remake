package jsettlers.graphics.startscreen;

import java.util.ArrayList;
import java.util.List;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.graphics.utils.UIPanel;

public class LoadSavedGamePanel extends UIPanel {
	private UIList<MapListItem> list;

	public LoadSavedGamePanel(List<? extends ILoadableGame> maps) {
		ArrayList<MapListItem> items =
		        new ArrayList<MapListItem>();
		for (ILoadableGame map : maps) {
			items.add(new MapListItem(map));
		}
		list = new UIList<MapListItem>(items, .1f);

		this.addChild(list, 0, .15f, 1, 1);

		// start button
		UILabeledButton startbutton =
		        new UILabeledButton(Labels.getName(EActionType.LOAD_GAME),
		                new Action(EActionType.LOAD_GAME));
		this.addChild(startbutton, .3f, 0, 1, .1f);
	}
	
	private class MapListItem extends GenericListItem {
		private final ILoadableGame item;

		public MapListItem(ILoadableGame item) {
			super(item.getName(), item.getSaveTime().toLocaleString());
			this.item = item;
		}

		public ILoadableGame getMap() {
			return item;
		}
	}

	public ILoadableGame getSelected() {
	    return list.getActiveItem().getMap();
    }

}
