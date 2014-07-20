package jsettlers.graphics.startscreen.startlists;

import java.util.List;

import jsettlers.graphics.startscreen.GenericListItem;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.utils.UIListItem;

public class LoadableMapListItem extends GenericListItem implements UIListItem {

	public LoadableMapListItem(IMapDefinition item) {
		// TODO: Load time.
		super(item.getName(), toPlayerString(item.getPlayers()));
	}

	private static String toPlayerString(List<ILoadableMapPlayer> players) {
		StringBuffer buffer = new StringBuffer();
		for (ILoadableMapPlayer p : players) {
			if (buffer.length() != 0) {
				buffer.append(", ");
			}
			buffer.append(p.getName());
		}
		return buffer.toString();
	}

}
