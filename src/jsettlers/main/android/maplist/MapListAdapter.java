package jsettlers.main.android.maplist;

import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class MapListAdapter<T> extends BaseAdapter {

	private final LayoutInflater inflater;

	public MapListAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
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

		TextView name = (TextView) view.findViewById(R.id.mapitem_name);
		String title = getTitle(arg0);
		name.setText(title);

		TextView description = (TextView) view.findViewById(R.id.mapitem_descr);
		description.setText(getDescriptionString(arg0));

		ImageView image = (ImageView) view.findViewById(R.id.mapitem_icon);
		short[] bitmap = getImage(arg0);
		image.setImageBitmap(PreviewImageConverter.toBitmap(bitmap));
		return view;
	}

	public abstract String getTitle(int arg0);

	protected abstract short[] getImage(int arg0);

	@Override
	public abstract T getItem(int position);

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	protected abstract String getDescriptionString(int mapn);

}
