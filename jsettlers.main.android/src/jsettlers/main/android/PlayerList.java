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
package jsettlers.main.android;

import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerList extends BaseAdapter implements
		IChangingListListener<IMultiplayerPlayer> {

	private final ChangingList<IMultiplayerPlayer> playerList;
	private List<? extends IMultiplayerPlayer> currentList;
	private final Handler handler;
	private final Context context;

	public PlayerList(Context context,
			ChangingList<IMultiplayerPlayer> playerList) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
		this.playerList = playerList;
		currentList = playerList.getItems();
		playerList.setListener(this);
	}

	@Override
	public int getCount() {
		return currentList.size();
	}

	@Override
	public IMultiplayerPlayer getItem(int idx) {
		return currentList.get(idx);
	}

	@Override
	public long getItemId(int idx) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int idx, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.playeritem, arg2, false);
		}
		IMultiplayerPlayer item = getItem(idx);
		String name = item == null ? "" : item.getName();
		((TextView) arg1.findViewById(R.id.player_name)).setText(name);
		String description = item == null ? "" : item.getId();
		((TextView) arg1.findViewById(R.id.player_details))
				.setText(description);
		int readyIcon = item != null && item.isReady() ? R.drawable.ready
				: R.drawable.unready;
		((ImageView) arg1.findViewById(R.id.player_readyflag))
				.setImageResource(readyIcon);
		return arg1;
	}

	@Override
	public void listChanged(ChangingList<IMultiplayerPlayer> list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				currentList = playerList.getItems();
				notifyDataSetChanged();
			}
		});
	}

}
