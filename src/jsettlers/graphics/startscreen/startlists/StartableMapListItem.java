package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;

public class StartableMapListItem extends GenericListItem {
	StartableMapListItem(IStartableMapDefinition map) {
		super(map.getName(), map.getId());
	}
}
