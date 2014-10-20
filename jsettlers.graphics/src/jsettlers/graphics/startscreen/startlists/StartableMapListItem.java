package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;

public class StartableMapListItem extends GenericListItem {
	StartableMapListItem(IMapDefinition map) {
		super(map.getName(), map.getId());
	}
}
