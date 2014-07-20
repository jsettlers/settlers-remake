package jsettlers.main.android.maplist;

import jsettlers.graphics.startscreen.interfaces.ChangingList;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapDefinition;
import android.view.LayoutInflater;

public class LoadableMapListAdapter extends MapListAdapter<ILoadableMapDefinition> {

	public LoadableMapListAdapter(LayoutInflater inflater,
			ChangingList<ILoadableMapDefinition> baseList) {
		super(inflater, baseList);
	}

	@Override
	public String getTitle(ILoadableMapDefinition item) {
		return item.getName();
	}

	@Override
	protected short[] getImage(ILoadableMapDefinition item) {
		return item.getImage();
	}

	@Override
	protected String getDescriptionString(ILoadableMapDefinition item) {
		return item.getCreationDate().toLocaleString();
	}
}
