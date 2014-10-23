package jsettlers.main.android.maplist;

import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import android.view.LayoutInflater;

public class MapDefinitionListAdapter<T extends IMapDefinition> extends MapListAdapter<T> {

	private final List<? extends T> maps;

	public MapDefinitionListAdapter(LayoutInflater inflater, ChangingList<T> changingList) {
		super(inflater, changingList);
		this.maps = changingList.getItems();
	}

	@Override
	public String getTitle(T map) {
		String title = map.getName();
		return title;
	}

	@Override
	protected short[] getImage(T map) {
		return map.getImage();
	}

	@Override
	protected String getDescriptionString(T map) {
		return "";
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

}
