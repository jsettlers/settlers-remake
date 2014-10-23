package jsettlers.main.android.maplist;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import android.view.LayoutInflater;

public class LoadableMapListAdapter extends MapListAdapter<IMapDefinition> {

	public LoadableMapListAdapter(LayoutInflater inflater, ChangingList<IMapDefinition> baseList) {
		super(inflater, baseList);
	}

	@Override
	public String getTitle(IMapDefinition item) {
		return item.getName();
	}

	@Override
	protected short[] getImage(IMapDefinition item) {
		return item.getImage();
	}

	@Override
	protected String getDescriptionString(IMapDefinition item) {
		return item.getCreationDate().toLocaleString();
	}
}
