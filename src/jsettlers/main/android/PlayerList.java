package jsettlers.main.android;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayerList extends BaseAdapter {

	private final INetworkScreenAdapter networkScreen;
	private INetworkPlayer[] playerList;
	private final ViewGroup view;

	public PlayerList(ViewGroup listView, INetworkScreenAdapter networkScreen) {
		this.view = listView;
		this.networkScreen = networkScreen;
		playerList = networkScreen.getPlayers();
	}

	@Override
	public int getCount() {
		if (playerList != null) {
			return playerList.length;
		} else {
			return 0;
		}
	}

	@Override
	public INetworkPlayer getItem(int idx) {
		if (playerList != null) {
			return playerList[idx];
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int idx) {
		// TODO
		INetworkPlayer item = getItem(idx);
		return item == null ? 0 : item.hashCode();
	}

	@Override
	public View getView(int idx, View arg1, ViewGroup arg2) {
		TextView view = new TextView(this.view.getContext());
		INetworkPlayer item = getItem(idx);
		String name = item == null ? "" : item.getPlayerName();
		view.setText(name);
		return view;
	}

	public void changed() {
		playerList = networkScreen.getPlayers();
		view.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

}
