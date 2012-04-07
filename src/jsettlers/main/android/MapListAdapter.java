package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MapListAdapter extends BaseAdapter {

	private final List<? extends IMapItem> maps;
	private final LayoutInflater inflater;

	public MapListAdapter(LayoutInflater inflater, List<? extends IMapItem> maps) {
		this.inflater = inflater;
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
		View view;
		if (arg1 != null) {
			view = arg1;
		} else {
			view = inflater.inflate(R.layout.maplistitem, null);
		}

		IMapItem map = maps.get(arg0);
		TextView name = (TextView) view.findViewById(R.id.mapitem_name);
		name.setText(map.getName());

		TextView description = (TextView) view.findViewById(R.id.mapitem_descr);
		description.setText(getDescriptionString(map));

		ImageView image = (ImageView) view.findViewById(R.id.mapitem_icon);
		image.setImageBitmap(PreviewImageConverter.toBitmap(map.getImage()));
		return view;
	}

	private static String getDescriptionString(IMapItem map) {
		return map.getMinPlayers() + " - " + map.getMaxPlayers();
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
