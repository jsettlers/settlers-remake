package jsettlers.main.android.maplist;

import java.util.List;

import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import android.view.LayoutInflater;

public class LoadableMapListAdapter extends MapListAdapter {

	private final List<? extends ILoadableGame> maps;

	public LoadableMapListAdapter(LayoutInflater inflater,
            List<? extends ILoadableGame> maps) {
	    super(inflater);
		this.maps = maps;
    }
	
	@Override
	public int getCount() {
		return maps.size();
	}

	@Override
	public ILoadableGame getItem(int arg0) {
		return maps.get(arg0);
	}
	
	@Override
	protected String getTitle(int arg0) {
	    ILoadableGame map = maps.get(arg0);
		String title = map.getName();
	    return title;
    }

	@Override
	protected short[] getImage(int arg0) {
	    ILoadableGame map = maps.get(arg0);
	    return map.getImage();
    }

	@Override
	protected String getDescriptionString(int mapn) {
		ILoadableGame map = maps.get(mapn);
		return map.getSaveTime().toLocaleString();
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}
	

}
