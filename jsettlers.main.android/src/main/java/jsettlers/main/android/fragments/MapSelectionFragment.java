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
package jsettlers.main.android.fragments;

import jsettlers.main.android.R;
import jsettlers.main.android.maplist.MapListAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public abstract class MapSelectionFragment<T> extends JsettlersFragment {

	private final class ListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int itemid,
				long arg3) {
			MapListAdapter<T> adapter = (MapListAdapter<T>) arg0.getAdapter();
			mapSelected(adapter.getItem(itemid));
		}
	}

	private T selectedItem;
	private MapListAdapter<T> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.maplist, container, false);
	}

	@Override
	public void onViewCreated(View mainView, Bundle savedInstanceState) {
		super.onViewCreated(mainView, savedInstanceState);
		TextView headline = (TextView) getView().findViewById(
				R.id.maplist_headline);
		headline.setText(getHeadlineText());

		ListView list = (ListView) mainView.findViewById(R.id.maplist);
		adapter = generateListAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickListener());

		Button startButton = (Button) mainView
				.findViewById(R.id.maplist_startbutton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedItem != null) {
					startGame(selectedItem);
				}
			}
		});
		startButton.setEnabled(false);
		startButton.setText(getStartButtonText());

		Button deleteButton = (Button) mainView
				.findViewById(R.id.maplist_deletebutton);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedItem != null) {
					deleteGame(selectedItem);
					mapSelected(null);
					adapter.notifyDataSetChanged();
				}
			}
		});
		deleteButton.setEnabled(false);
		if (!supportsDeletion()) {
			deleteButton.setVisibility(View.GONE);
		}

		if (!supportsPlayerCount()) {
			getPlayerCountField().setVisibility(View.GONE);
		}
	}

	protected abstract MapListAdapter<T> generateListAdapter();

	protected abstract String getItemDescription(T item);

	protected abstract boolean supportsDeletion();

	protected abstract void deleteGame(T game);

	protected abstract void startGame(T game);

	protected abstract boolean supportsPlayerCount();

	protected abstract int getSuggestedPlayerCount(T game);

	private EditText getPlayerCountField() {
		return (EditText) getView().findViewById(R.id.maplist_players);
	}

	protected int getPlayerCount() {
		try {
			return Integer.parseInt(getPlayerCountField().getText().toString());
		} catch (NumberFormatException e) {
			// showText(R.string.illegal_playercount);
			return 0;
		}
	}

	protected void setPlayerCount(int count) {
		getPlayerCountField().setText(count + "");
	}

	private void mapSelected(T item) {
		selectedItem = item;
		TextView name = (TextView) getView().findViewById(R.id.maplist_name);
		TextView description = (TextView) getView().findViewById(
				R.id.maplist_description);
		name.setText(item == null ? "" : adapter.getTitle(item));
		description.setText(item == null ? ""
				: getItemDescription(selectedItem));

		Button startButton = (Button) getView().findViewById(
				R.id.maplist_startbutton);
		startButton.setEnabled(item != null);

		Button deleteButton = (Button) getView().findViewById(
				R.id.maplist_deletebutton);
		deleteButton.setEnabled(item != null);

		if (supportsPlayerCount()) {
			setPlayerCount(getSuggestedPlayerCount(item));
		}
	}

	protected abstract int getHeadlineText();

	protected abstract int getStartButtonText();

}
