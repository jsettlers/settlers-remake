package jsettlers.main.android.maplist;

import java.util.List;

import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import android.view.LayoutInflater;

public class FreshMapListAdapter extends MapListAdapter<IMapItem> {

	private final List<? extends IMapItem> maps;

	public FreshMapListAdapter(LayoutInflater inflater, List<? extends IMapItem> maps) {
		super(inflater);
		this.maps = maps;
	}
	
	@Override
	public int getCount() {
		return maps.size();
	}

	@Override
	public IMapItem getItem(int arg0) {
		return maps.get(arg0);
	}
	
	@Override
    public String getTitle(int arg0) {
	    IMapItem map = maps.get(arg0);
		String title = map.getName();
	    return title;
    }

	@Override
	protected short[] getImage(int arg0) {
	    IMapItem map = maps.get(arg0);
	    return map.getImage();
    }

	@Override
	protected String getDescriptionString(int mapn) {
		IMapItem map = maps.get(mapn);
		return map.getMinPlayers() + " - " + map.getMaxPlayers();
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

}
