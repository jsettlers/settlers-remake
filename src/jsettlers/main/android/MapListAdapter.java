package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MapListAdapter extends BaseAdapter {

	private final List<? extends IMapItem> maps;
	private final Context context;

	public MapListAdapter(Context context, List<? extends IMapItem> maps) {
		this.context = context;
		this.maps = maps;
    }

	@Override
	public int getCount() {
		return maps.size();
	}

	@Override
	public Object getItem(int arg0) {
		return maps.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView view;
		if (arg1 != null) {
			view = (TextView) arg1;
		}else {
			view = new TextView(context);
		}
		IMapItem map = maps.get(arg0);
		view.setText(map.getName());
		return view;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

	public IMapItem get(int itemid) {
	    return maps.get(itemid);
    }

}
