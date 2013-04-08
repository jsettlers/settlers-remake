package jsettlers.main.android;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

	private final ArrayList<String> messages = new ArrayList<String>();
	private final Context context;
	private final Handler handler;
	private final Runnable dataSetChangedNotifier = new Runnable() {
		@Override
		public void run() {
			notifyDataSetChanged();
		}
	};

	public ChatAdapter(Context context) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setText(messages.get(position));
		return textView;
	}

	public void addChatMessage(String message) {
		messages.add(message);
		handler.post(dataSetChangedNotifier);
	}

}
