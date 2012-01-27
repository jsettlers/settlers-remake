package jsettlers.main.android;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkPlayer;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayerList extends BaseAdapter {

	private final INetworkScreenAdapter networkScreen;
	private INetworkPlayer[] playerList;
	private final Context context;

	public PlayerList(Context context, INetworkScreenAdapter networkScreen) {
		this.context = context;
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
		return playerList[idx];
	}

	@Override
	public long getItemId(int idx) {
		// TODO
		return getItem(idx).hashCode();
	}

	@Override
	public View getView(int idx, View arg1, ViewGroup arg2) {
		TextView view = new TextView(context);
		view.setText(getItem(idx).getPlayerName());
		return view;
	}

	public void changed() {
		playerList = networkScreen.getPlayers();
		notifyDataSetChanged();
	}

}
