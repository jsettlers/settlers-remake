/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.android.maplist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;

/**
 * This is the basic map list. It can be extended by setting the name/description/image sources. It automatically listens to list changes.
 * 
 * @author michael
 * 
 * @param <T>
 */
public abstract class MapListAdapter<T> extends BaseAdapter implements IChangingListListener<T> {

	private final LayoutInflater inflater;
	private final Handler handler;
	private final ChangingList<? extends T> baseList;
	private List<? extends T> maps = Collections.emptyList();;

	public MapListAdapter(LayoutInflater inflater, ChangingList<? extends T> baseList) {
		this.inflater = inflater;
		this.baseList = baseList;
		handler = new Handler();
		baseList.setListener(this);
		listChanged(baseList);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public T getItem(int arg0) {
		return maps.get(arg0);
	}

	@Override
	public int getCount() {
		return maps.size();
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view;
		if (arg1 != null) {
			view = arg1;
		} else {
			view = inflater.inflate(R.layout.maplistitem, null);
		}

		T item = getItem(arg0);

		TextView name = (TextView) view.findViewById(R.id.mapitem_name);
		String title = getTitle(item);
		name.setText(title);

		TextView description = (TextView) view.findViewById(R.id.mapitem_descr);
		description.setText(getDescriptionString(item));

		ImageView image = (ImageView) view.findViewById(R.id.mapitem_icon);
		short[] bitmap = getImage(item);
		image.setImageBitmap(PreviewImageConverter.toBitmap(bitmap));
		return view;
	}

	public abstract String getTitle(T item);

	protected abstract short[] getImage(T item);

	protected abstract String getDescriptionString(T item);

	@Override
	public void listChanged(ChangingList<? extends T> list) {
		final List<T> newList = new ArrayList<T>(list.getItems());
		Collections.sort(newList, getDefaultComparator());
		handler.post(new Runnable() {
			@Override
			public void run() {
				maps = newList;
				notifyDataSetChanged();
			}
		});
	}

	protected abstract Comparator<? super T> getDefaultComparator();
}
