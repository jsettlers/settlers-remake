package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.INetworkScreenAdapter.INetworkPlayer;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayerList extends BaseAdapter {
	

	private final INetworkScreenAdapter networkScreen;
	private List<INetworkPlayer> playerList;
	private final Context context;

	public PlayerList(Context context, INetworkScreenAdapter networkScreen) {
	    this.context = context;
		this.networkScreen = networkScreen;
		playerList = networkScreen.getPlayerList();
    }

	@Override
    public int getCount() {
	    return playerList.size();
    }

	@Override
    public Object getItem(int arg0) {
	    return playerList.get(arg0);
    }

	@Override
    public long getItemId(int arg0) {
		//TODO
	    return playerList.get(arg0).hashCode();
    }

	@Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView view = new TextView(context);
		view.setText(playerList.get(arg0).getPlayerName());
	    return view;
    }

	public void changed() {
		playerList = networkScreen.getPlayerList();
	    notifyDataSetChanged();
    }

}
