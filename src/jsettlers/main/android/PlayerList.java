package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayerList extends BaseAdapter {

	private final IChangingList<IMultiplayerPlayer> playerList;
	private List<? extends IMultiplayerPlayer> currentList;
	private final Handler handler;
	private final Context context;

	public PlayerList(Context context,
			IChangingList<IMultiplayerPlayer> playerList) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
		this.playerList = playerList;
		currentList = playerList.getItems();
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
		TextView view = new TextView(context);
		IMultiplayerPlayer item = getItem(idx);
		String name = item == null ? "" : item.getName();
		view.setText(name);
		view.setTextColor(item != null && item.isReady() ? R.color.network_ready
				: R.color.network_not_ready);
		return view;
	}

	public void changed() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				currentList = playerList.getItems();
				notifyDataSetChanged();
			}
		});
	}

}
