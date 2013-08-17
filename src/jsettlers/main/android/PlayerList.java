package jsettlers.main.android;

import java.util.List;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IChangingListListener;
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
	public void listChanged(IChangingList<IMultiplayerPlayer> list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				currentList = playerList.getItems();
				notifyDataSetChanged();
			}
		});
	}

}
