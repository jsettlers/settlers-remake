package jsettlers.graphics.startscreen.startlists;

import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.utils.UIListItem;

public class JoinableGameItem extends GenericListItem implements UIListItem {

	public JoinableGameItem(IJoinableGame item) {
		super(item.getMap().getName(), item.getId());
	}

}
