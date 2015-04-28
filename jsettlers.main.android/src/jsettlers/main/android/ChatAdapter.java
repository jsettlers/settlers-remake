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

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.graphics.startscreen.interfaces.ENetworkMessage;
import jsettlers.graphics.startscreen.interfaces.IChatMessageListener;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter implements IChatMessageListener {

	private abstract static class ChatMessage {
		public abstract int getViewType();

		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = createView(inflater, parent);
			}
			fillView(convertView);
			return convertView;
		}

		protected abstract View createView(LayoutInflater inflater, ViewGroup parent);

		protected abstract void fillView(View convertView);
	}

	private static final class UserChatMessage extends ChatMessage {
		private final String authorId;
		private final String message;

		public UserChatMessage(String authorId, String message) {
			this.authorId = authorId;
			this.message = message;
		}

		@Override
		public int getViewType() {
			return 0;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent) {
			return new TextView(inflater.getContext());
		}

		@Override
		protected void fillView(View view) {
			((TextView) view).setText(authorId + ": " + message);
		}
	}

	private static final class SystemChatMessage extends ChatMessage {
		private final String authorId;
		private final ENetworkMessage message;

		public SystemChatMessage(String authorId, ENetworkMessage message) {
			this.authorId = authorId;
			this.message = message;
		}

		@Override
		public int getViewType() {
			return 1;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent) {
			return new TextView(inflater.getContext());
		}

		@Override
		protected void fillView(View view) {
			((TextView) view).setText(authorId + ": " + message);
		}
	}

	private final ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
	private final BlockingQueue<ChatMessage> incomming = new LinkedBlockingQueue<ChatMessage>();
	private final Handler handler;
	private final Runnable dataSetChangedNotifier = new Runnable() {
		@Override
		public void run() {
			incomming.drainTo(messages);
			notifyDataSetChanged();
		}
	};
	private final LayoutInflater inflater;

	public ChatAdapter(LayoutInflater inflater,
			IJoinPhaseMultiplayerGameConnector connector) {
		this.inflater = inflater;
		this.handler = new Handler(inflater.getContext().getMainLooper());
		connector.setChatListener(this);
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
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return messages.get(position).getViewType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return messages.get(position).getView(inflater, convertView, parent);
	}

	public void addChatMessage(ChatMessage message) {
		incomming.add(message);
		handler.post(dataSetChangedNotifier);
	}

	@Override
	public void chatMessageReceived(String authorId, String message) {
		addChatMessage(new UserChatMessage(authorId, message));
	}

	@Override
	public void systemMessageReceived(IMultiplayerPlayer player, ENetworkMessage message) {
		String playerName = player != null ? player.getName() : "";
		addChatMessage(new SystemChatMessage(playerName, message));
	}
}
